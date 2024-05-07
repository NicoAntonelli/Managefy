package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.*;
import nicoAntonelli.managefy.repositories.SaleRepository;
import nicoAntonelli.managefy.repositories.SaleLineRepository;
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
    }

    public List<Sale> GetSales() {
        return saleRepository.findAll();
    }

    public List<Sale> GetSalesByInterval(LocalDateTime initialDate, LocalDateTime finalDate) {
        return saleRepository.findByInterval(initialDate, finalDate);
    }

    public Boolean ExistsSale(Long saleID) {
        return saleRepository.existsById(saleID);
    }

    public Sale GetOneSale(Long saleID) {
        Optional<Sale> sale = saleRepository.findById(saleID);
        if (sale.isEmpty()) {
            throw new IllegalStateException("Error at 'GetOneSale' - Sale with ID: " + saleID + " doesn't exist");
        }

        return sale.get();
    }

    public Sale CreateSale(Sale sale) {
        // Validate associated business
        Business business = sale.getBusiness();
        if (business == null || business.getId() == null) {
            throw new IllegalStateException("Error at 'CreateSale' - Business not supplied");
        }
        if (!businessService.ExistsBusiness(business.getId())) {
            throw new IllegalStateException("Error at 'CreateSale' - Business with ID: " + business.getId() + " doesn't exist");
        }

        // Optional associated client: validate if it was supplied
        Client client = sale.getClient();
        if (client != null) {
            if (client.getId() == null) {
                throw new IllegalStateException("Error at 'CreateSale' - Optional client was supplied but without an ID, business: " + business.getId());
            }
            if (!clientService.ExistsClient(client.getId())) {
                throw new IllegalStateException("Error at 'CreateSale' - Client with ID: " + client.getId() + " doesn't exist, business: " + business.getId());
            }
        }

        // At least one saleLine
        List<SaleLine> lines = sale.getSaleLines();
        if (lines == null || lines.isEmpty()) {
            throw new IllegalStateException("Error at 'CreateSale' - No SaleLines supplied, business: " + business.getId());
        }

        // All saleLines must have positive subtotal and a valid product
        for (int i = 0; i < lines.size(); i++) {
            SaleLine line = lines.get(i);
            if (line.getSubtotal() <= 0) {
                throw new IllegalStateException("Error at 'CreateSale' - The SaleLine in position: " + (i+1) + " had an invalid subtotal of $" + line.getPrice() + ", business: " + business.getId());
            }

            // Validate associated product for current line
            Product product = line.getProduct();
            if (product == null || product.getId() == null) {
                throw new IllegalStateException("Error at 'CreateSale' - Product not supplied for the saleLine in position: " + (i+1) + ", business: " + business.getId());
            }
            if (!productService.ExistsProduct(product.getId())) {
                throw new IllegalStateException("Error at 'CreateSale' - Product with ID: " + product.getId() + " doesn't exist, business: " + business.getId());
            }

            line.setSale(null);
            line.setPosition(i+1);
        }

        // Set state if a valid value was provided
        Boolean result = sale.setStateByText(sale.getState().toString());
        if (!result) {
            throw new IllegalStateException("Error at 'CreateSale' - Unexpected value for state: " + sale.getState() + ", business: " + business.getId());
        }
        if (sale.getState() == Sale.SaleState.Cancelled) {
            throw new IllegalStateException("Error at 'CreateSale' - The sale can't have Cancelled state, business: " + business.getId());
        }

        // Partial payment (if it was given)
        Float partialPayment = sale.getPartialPayment();
        if (partialPayment != null && partialPayment <= 0) {
            throw new IllegalStateException("Error at 'CreateSale' - Can't make a partial payment of $" + partialPayment + ", business: " + business.getId());
        }

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

    public Sale UpdateSaleState(Long saleID, String state) {
        Sale sale = GetOneSale(saleID);
        Boolean result = sale.setStateByText(state);
        if (!result) {
            throw new IllegalStateException("Error at 'UpdateNotificationState' - Unexpected value: " + state);
        }

        return sale;
    }

    public Sale UpdateSalePartialPayment(Long saleID, Float partialPayment) {
        Sale sale = GetOneSale(saleID);
        if (sale.getState() != Sale.SaleState.PendingPayment &&
            sale.getState() != Sale.SaleState.PartialPayment) {
            throw new IllegalStateException("Error at 'UpdateSalePartialPayment' - Sale: " + saleID + " has the state: " + sale.getState());
        }

        if (partialPayment <= 0) {
            throw new IllegalStateException("Error at 'UpdateSalePartialPayment' - Can't make a partial payment of $" + partialPayment + ", sale: " + saleID);
        }

        float updatedPartialPayment = sale.getPartialPayment() + partialPayment;
        if (updatedPartialPayment < sale.getTotalPrice()) {
            sale.setPartialPayment(updatedPartialPayment);
            sale.setState(Sale.SaleState.PendingPayment);
        } else {
            sale.setPartialPayment(sale.getTotalPrice());
            sale.setState(Sale.SaleState.Payed);
        }

        saleRepository.save(sale);

        return sale;
    }

    // Logic deletion (field: sale state)
    public Sale CancelSale(Long saleID) {
        Sale sale = GetOneSale(saleID);
        sale.setState(Sale.SaleState.Cancelled);
        saleRepository.save(sale);

        return sale;
    }
}
