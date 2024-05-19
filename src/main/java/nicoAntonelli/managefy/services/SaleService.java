package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.*;
import nicoAntonelli.managefy.entities.dto.ClientCU;
import nicoAntonelli.managefy.entities.dto.SaleC;
import nicoAntonelli.managefy.entities.dto.SaleLineC;
import nicoAntonelli.managefy.utils.DateFormatterSingleton;
import nicoAntonelli.managefy.repositories.SaleRepository;
import nicoAntonelli.managefy.repositories.SaleLineRepository;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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

    public List<Sale> GetSalesByClient(Long businessID, Long clientID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        return saleRepository.findActivesByBusinessAndClient(businessID, clientID);
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

    public Sale GetOneSale(Long saleID, Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        Optional<Sale> sale = saleRepository.findByIdActiveAndBusiness(saleID, businessID);
        if (sale.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneSale' - Sale with ID: " + saleID + " doesn't exist or it's not associated with the business: " + businessID);
        }

        return sale.get();
    }

    public Sale CreateSale(SaleC saleC, User user) {
        // Validate associated business
        Long businessID = saleC.getBusinessID();
        if (businessID == null) {
            throw new Exceptions.BadRequestException("Error at 'CreateSale' - Business not supplied");
        }
        if (!businessService.ExistsBusiness(businessID, user, "collaborator")) {
            throw new Exceptions.BadRequestException("Error at 'CreateSale' - Business with ID: " + businessID + " doesn't exist or it's not associated with the user: " + user.getId());
        }

        // Validate state
        String state = saleC.getState();
        if (state == null || state.isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'CreateSale' - State field was not supplied");
        }
        if (state.equalsIgnoreCase("cancelled")) {
            throw new Exceptions.BadRequestException("Error at 'CreateSale' - The sale can't have Cancelled state, business: " + businessID);
        }
        if (!List.of("PendingPayment", "PartialPayment", "Paid", "PaidAndBilled").contains(state)) {
            throw new Exceptions.BadRequestException("Error at 'CreateNotification' - Unexpected state value: " + state);
        }

        // Optional partial payment - must be positive if supplied
        BigDecimal partialPayment = saleC.getPartialPayment();
        if (partialPayment != null) {
            if (partialPayment.compareTo(BigDecimal.ZERO) <= 0) {
                throw new Exceptions.BadRequestException("Error at 'CreateSale' - Can't set a partial payment of $" + partialPayment + ", business: " + businessID);
            }

            if (!saleC.getState().equalsIgnoreCase("PartialPayment")) {
                throw new Exceptions.BadRequestException("Error at 'CreateSale' - Can't set a partial payment if the state supplied is not 'PartialPayment', business: " + businessID);
            }
        }

        // Optional associated client - check it or create a new one
        CheckOrCreateClientForSale(saleC, user);

        // At least one saleLine DTO
        List<SaleLineC> lines = saleC.getSaleLines();
        if (lines == null || lines.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'CreateSale' - No SaleLines supplied, business: " + businessID);
        }

        // New real "sale lines" array - update after saleLineC validations
        List<SaleLine> saleLines = new ArrayList<>();

        // Products to update stock later (A sale can have multiple lines with same product!)
        Map<Long, Integer> productsToUpdate = new HashMap<>();

        // All saleLines must have valid cost/price/amount & valid product
        for (int i = 0; i < lines.size(); i++) {
            SaleLineC line = lines.get(i);
            if (line.getCost().compareTo(BigDecimal.ZERO) < 0) {
                throw new Exceptions.BadRequestException("Error at 'CreateSale' - The SaleLine in position: " + (i+1) + " had an invalid cost of $" + line.getCost() + ", business: " + businessID);
            }
            if (line.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new Exceptions.BadRequestException("Error at 'CreateSale' - The SaleLine in position: " + (i+1) + " had an invalid price of $" + line.getPrice() + ", business: " + businessID);
            }
            if (line.getPrice().compareTo(line.getCost()) < 0) {
                throw new Exceptions.BadRequestException("Error at 'CreateSale' - The SaleLine in position: " + (i+1) + " had a price lower than the cost ($" + line.getPrice() + " VS $" + line.getCost() + "), business: " + businessID);
            }
            if (line.getAmount() <= 0) {
                throw new Exceptions.BadRequestException("Error at 'CreateSale' - The SaleLine in position: " + (i+1) + " had an invalid amount of " + line.getAmount() + " units, business: " + businessID);
            }

            // Optional discount - must be non-negative if supplied
            if (line.getDiscountSurcharge() != null && line.getDiscountSurcharge().compareTo(BigDecimal.ZERO) < 0) {
                throw new Exceptions.BadRequestException("Error at 'CreateSale' - The SaleLine in position: " + (i+1) + " had an invalid discount surcharge of " + line.getDiscountSurcharge() + ", business: " + businessID);
            }

            // Validate associated product for current line
            Long productID = line.getProductID();
            if (productID == null) {
                throw new Exceptions.BadRequestException("Error at 'CreateSale' - Product not supplied for the saleLine in position: " + (i+1) + ", business: " + businessID);
            }
            if (!productService.ExistsProduct(productID, businessID, user)) {
                throw new Exceptions.BadRequestException("Error at 'CreateSale' - Product with ID: " + productID + " doesn't exist or it's not associated with the business: " + businessID);
            }

            // Initial state for saleLine (sale will be set after 'sale save')
            SaleLine newLine = new SaleLine(line.getAmount(), line.getPrice(), line.getCost(), line.getDiscountSurcharge());
            newLine.setPosition(i+1);
            newLine.setProductByID(productID);

            saleLines.add(newLine);

            // Add product to update map - if already exists add the amount for this line
            Integer amount = line.getAmount();
            if (productsToUpdate.containsKey(productID)) {
                amount += productsToUpdate.get(productID);
            }
            productsToUpdate.put(productID, amount);
        }

        // Check available stock for products and save all
        productService.UpdateProductStockByMany(productsToUpdate, businessID, user);

        // New sale object with DTO info
        Sale sale = new Sale(saleC.getPartialPayment(), saleC.getState());

        // Set business
        sale.setBusinessByID(businessID);

        // Update sale with client
        if (saleC.getClient() != null) {
            sale.setClientByID(saleC.getClient().getId());
        }

        // Calculate total price and save the sale before saleLines
        sale.calculateAndSetTotalPrice(saleLines);
        sale = saleRepository.save(sale);

        // Now save the saleLines with the now-loaded sale
        for (SaleLine saleLine : saleLines) {
            saleLine.setSaleByID(sale.getId());
        }
        saleLines = saleLineRepository.saveAll(saleLines);

        // Set saved lines in the sale and return it
        sale.setSaleLines(saleLines);
        return sale;
    }

    public Sale UpdateSaleState(Long saleID, Long businessID, String state, User user) {
        Sale sale = GetOneSale(saleID, businessID, user);
        Boolean result = sale.setStateByText(state);
        if (!result) {
            throw new Exceptions.BadRequestException("Error at 'UpdateSaleState' - Unexpected value: " + state);
        }

        // Erase partial payment if it has a different state than 'partial payment'
        if (sale.getState() != Sale.SaleState.PartialPayment) {
            sale.setPartialPayment(BigDecimal.ZERO);
        }

        return saleRepository.save(sale);
    }

    public Sale UpdateSalePartialPayment(Long saleID, Long businessID, BigDecimal partialPayment, User user) {
        Sale sale = GetOneSale(saleID, businessID, user);
        if (sale.getState() != Sale.SaleState.PendingPayment &&
            sale.getState() != Sale.SaleState.PartialPayment) {
            throw new Exceptions.BadRequestException("Error at 'UpdateSalePartialPayment' - Sale: " + saleID + " has the state: " + sale.getState());
        }

        if (partialPayment.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exceptions.BadRequestException("Error at 'UpdateSalePartialPayment' - Can't make a partial payment of $" + partialPayment + ", sale: " + saleID);
        }

        BigDecimal updatedPartialPayment = sale.getPartialPayment().add(partialPayment);
        if (updatedPartialPayment.compareTo(sale.getTotalPrice()) < 0) {
            sale.setPartialPayment(updatedPartialPayment);
            sale.setState(Sale.SaleState.PendingPayment);
        } else {
            // Ignore value and set 'paid' state
            sale.setPartialPayment(BigDecimal.ZERO);
            sale.setState(Sale.SaleState.Paid);
        }

        saleRepository.save(sale);

        return sale;
    }

    public Sale UpdateOrAddClientForSale(Long saleID, Long businessID, Long clientID, User user) {
        if (!clientService.ExistsClient(clientID, businessID, user)) {
            throw new Exceptions.BadRequestException("Error at 'UpdateOrAddClientForSale' - Client with ID: " + clientID + " doesn't exist or it's not associated with the business: " + businessID);
        }

        Sale sale = GetOneSale(saleID, businessID, user);
        sale.setClientByID(clientID);

        return saleRepository.save(sale);
    }

    public Sale EraseClientForSale(Long saleID, Long businessID, User user) {
        Sale sale = GetOneSale(saleID, businessID, user);
        sale.setClient(null);

        return saleRepository.save(sale);
    }

    // Logic deletion (field: sale state)
    public Long CancelSale(Long saleID, Long businessID, User user) {
        // Validate admin role
        boolean exists = businessService.ExistsBusiness(businessID, user, "admin");
        if (!exists) {
            throw new Exceptions.BadRequestException("Error at 'CancelSale' - Business with ID: " + businessID + " doesn't exist or the user: " + user.getId() + " isn't an Admin or the Manager");
        }

        Sale sale = GetOneSale(saleID, businessID, user);

        // Sale's client (optional)
        Client client = sale.getClient();

        // Sale deletion (don't update stock)
        sale.setState(Sale.SaleState.Cancelled);
        sale.setClient(null);
        saleRepository.save(sale);

        // Delete client if it doesn't have more associated sales
        if (client != null) {
            List<Sale> salesByClient = saleRepository.findActivesByBusinessAndClient(businessID, client.getId());
            if (salesByClient.size() == 1) {
                clientService.DeleteClientAfterCancelSale(client.getId());
            }
        }

        return saleID;
    }

    private void CheckOrCreateClientForSale(SaleC saleC, User user) {
        ClientCU clientCU = saleC.getClient();

        // Client not supplied is OK
        if (clientCU == null) return;

        // With ID: validate it
        if (clientCU.getId() != null) {
            Long businessID = saleC.getBusinessID();
            boolean exists = clientService.ExistsClient(clientCU.getId(), businessID, user);
            if (!exists) {
                throw new Exceptions.BadRequestException("Error at 'CheckOrCreateClientForSale' - Optional Client: " + clientCU.getId() + " supplied it's not valid");
            }

            return;
        }

        // Without ID: create it, then set it updated in sale
        Client client = clientService.CreateClientForNewSale(clientCU);
        saleC.getClient().setId(client.getId());
    }
}
