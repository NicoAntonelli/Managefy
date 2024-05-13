package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.*;
import nicoAntonelli.managefy.utils.DateFormatterSingleton;
import nicoAntonelli.managefy.repositories.SaleRepository;
import nicoAntonelli.managefy.repositories.SaleLineRepository;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SaleService {
    private final SaleRepository saleRepository;
    private final SaleLineRepository saleLineRepository;
    private final BusinessService businessService; // Dependency
    private final ClientService clientService; // Dependency
    private final ProductService productService; // Dependency
    private final DateFormatterSingleton dateFormatterSingleton;

    @Autowired
    public SaleService(SaleRepository saleRepository,
                       SaleLineRepository saleLineRepository,
                       BusinessService businessService,
                       ClientService clientService,
                       ProductService productService) {
        this.saleRepository = saleRepository;
        this.saleLineRepository = saleLineRepository;
        this.businessService = businessService;
        this.clientService = clientService;
        this.productService = productService;
        this.dateFormatterSingleton = DateFormatterSingleton.getInstance();
    }

    public List<Sale> GetSalesIncomplete(Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        return saleRepository.findIncompleteByBusiness(businessID);
    }

    public List<Sale> GetSalesByInterval(Long businessID, String initialDate, String finalDate, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        // Interval validations
        if (initialDate == null || finalDate == null
                || initialDate.isBlank() || finalDate.isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'GetSalesByInterval' - Both start and end dates must be supplied");
        }

        LocalDateTime startDate, endDate;
        try {
            startDate = LocalDateTime.parse(initialDate, dateFormatterSingleton.value);
            endDate = LocalDateTime.parse(finalDate, dateFormatterSingleton.value);
        }
        catch(Exception ex) {
            throw new Exceptions.BadRequestException("Error at 'GetSalesByInterval' - Both start and end dates must be a valid date form", ex);
        }

        if (startDate.isAfter(endDate)) {
            throw new Exceptions.BadRequestException("Error at 'GetSalesByInterval' - End date can't have a value before start date");
        }

        return saleRepository.findActivesByIntervalAndBusiness(startDate, endDate, businessID);
    }

    public Boolean ExistsSale(Sale sale, User user) {
        // Sale has ID
        if (sale.getId() == null) {
            throw new Exceptions.BadRequestException("Error at 'ExistsSale' - Sale not supplied");
        }

        // Business not null
        Business business = sale.getBusiness();
        if (business == null || business.getId() == null) {
            throw new Exceptions.BadRequestException("Error at 'ExistsSale' - Business not supplied");
        }

        return ExistsSale(sale.getId(), business.getId(), user);
    }

    public Boolean ExistsSale(Long saleID, Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        return saleRepository.existsByIdActiveAndBusiness(saleID, businessID);
    }

    public Sale GetOneSale(Long saleID, Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        Optional<Sale> sale = saleRepository.findByIdActiveAndBusiness(saleID, businessID);
        if (sale.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneSale' - Sale with ID: " + saleID + " doesn't exist or it's not associated with the business: " + businessID);
        }

        return sale.get();
    }

    public Sale CreateSale(Sale sale, User user) {
        // Validate associated business
        Business business = sale.getBusiness();
        if (business == null || business.getId() == null) {
            throw new Exceptions.BadRequestException("Error at 'CreateSale' - Business not supplied");
        }
        if (!businessService.ExistsBusiness(business.getId(), user, "collaborator")) {
            throw new Exceptions.BadRequestException("Error at 'CreateSale' - Business with ID: " + business.getId() + " doesn't exist or it's not associated with the user: " + user.getId());
        }

        /// Optional associated client - check it or create a new one (and update sale)
        Client client = sale.getClient();
        if (client != null) {
            CheckOrCreateClientForSale(sale, user);
        }

        // At least one saleLine
        List<SaleLine> lines = sale.getSaleLines();
        if (lines == null || lines.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'CreateSale' - No SaleLines supplied, business: " + business.getId());
        }

        // All saleLines must have positive subtotal and a valid product
        for (int i = 0; i < lines.size(); i++) {
            SaleLine line = lines.get(i);
            if (line.getSubtotal() <= 0) {
                throw new Exceptions.BadRequestException("Error at 'CreateSale' - The SaleLine in position: " + (i+1) + " had an invalid subtotal of $" + line.getPrice() + ", business: " + business.getId());
            }

            // Validate associated product for current line
            Product product = line.getProduct();
            if (product == null || product.getId() == null) {
                throw new Exceptions.BadRequestException("Error at 'CreateSale' - Product not supplied for the saleLine in position: " + (i+1) + ", business: " + business.getId());
            }
            if (!productService.ExistsProduct(product.getId(), business.getId(), user)) {
                throw new Exceptions.BadRequestException("Error at 'CreateSale' - Product with ID: " + product.getId() + " doesn't exist or it's not associated with the business: " + business.getId());
            }

            // Forced initial state for saleLine (sale will be set after 'sale save')
            line.setSale(null);
            line.setPosition(i+1);
        }

        // Set state if a valid value was provided
        Boolean result = sale.setStateByText(sale.getState().toString());
        if (!result) {
            throw new Exceptions.BadRequestException("Error at 'CreateSale' - Unexpected value for state: " + sale.getState() + ", business: " + business.getId());
        }
        if (sale.getState() == Sale.SaleState.Cancelled) {
            throw new Exceptions.BadRequestException("Error at 'CreateSale' - The sale can't have Cancelled state, business: " + business.getId());
        }

        // Partial payment (if it was given)
        Float partialPayment = sale.getPartialPayment();
        if (partialPayment != null) {
            if (partialPayment <= 0) {
                throw new Exceptions.BadRequestException("Error at 'CreateSale' - Can't set a partial payment of $" + partialPayment + ", business: " + business.getId());
            }

            if (sale.getState() != Sale.SaleState.PartialPayment) {
                throw new Exceptions.BadRequestException("Error at 'CreateSale' - Can't set a partial payment if the state supplied is not 'PartialPayment', business: " + business.getId());
            }
        }

        // Forced initial state for sale
        sale.setId(null);
        sale.setDate(LocalDateTime.now());

        // Calculate total price and save the sale before saleLines
        sale.calculateAndSetTotalPrice(lines);
        sale = saleRepository.save(sale);

        // Now save the saleLines with the now-loaded sale
        lines = saleLineRepository.saveAll(lines);

        // Set saved lines in the sale and return it
        sale.setSaleLines(lines);
        return sale;
    }

    public Sale UpdateSaleState(Long saleID, String state, Long businessID, User user) {
        Sale sale = GetOneSale(saleID, businessID, user);
        Boolean result = sale.setStateByText(state);
        if (!result) {
            throw new Exceptions.BadRequestException("Error at 'UpdateSaleState' - Unexpected value: " + state);
        }

        // Erase partial payment if it has a different state than 'partial payment'
        if (sale.getState() != Sale.SaleState.PartialPayment) {
            sale.setPartialPayment(0F);
        }

        return saleRepository.save(sale);
    }

    public Sale UpdateSalePartialPayment(Long saleID, Float partialPayment, Long businessID, User user) {
        Sale sale = GetOneSale(saleID, businessID, user);
        if (sale.getState() != Sale.SaleState.PendingPayment &&
            sale.getState() != Sale.SaleState.PartialPayment) {
            throw new Exceptions.BadRequestException("Error at 'UpdateSalePartialPayment' - Sale: " + saleID + " has the state: " + sale.getState());
        }

        if (partialPayment <= 0) {
            throw new Exceptions.BadRequestException("Error at 'UpdateSalePartialPayment' - Can't make a partial payment of $" + partialPayment + ", sale: " + saleID);
        }

        float updatedPartialPayment = sale.getPartialPayment() + partialPayment;
        if (updatedPartialPayment < sale.getTotalPrice()) {
            sale.setPartialPayment(updatedPartialPayment);
            sale.setState(Sale.SaleState.PendingPayment);
        } else {
            // Ignore value and set 'paid' state
            sale.setPartialPayment(0F);
            sale.setState(Sale.SaleState.Paid);
        }

        saleRepository.save(sale);

        return sale;
    }

    // Logic deletion (field: sale state)
    public Long CancelSale(Long saleID, Long businessID, User user) {
        Sale sale = GetOneSale(saleID, businessID, user);
        sale.setState(Sale.SaleState.Cancelled);
        saleRepository.save(sale);

        return saleID;
    }

    private void CheckOrCreateClientForSale(Sale sale, User user) {
        Client client = sale.getClient();

        // Product not supplied is OK
        if (client == null) return;

        // With ID: validate it
        if (client.getId() != null) {
            Business business = sale.getBusiness();
            boolean exists = clientService.ExistsClient(client.getId(), business.getId(), user);
            if (!exists) {
                throw new Exceptions.BadRequestException("Error at 'CheckOrCreateClientForSale' - Optional Client: " + client.getId() + " supplied it's not valid");
            }

            return;
        }

        // Without ID: create it, then set it updated in sale
        client = clientService.CreateClient(client, user, true);
        sale.setClient(client);
    }
}
