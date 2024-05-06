package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.*;
import nicoAntonelli.managefy.repositories.SaleRepository;
import nicoAntonelli.managefy.repositories.SaleLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.core.support.FragmentNotImplementedException;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public SaleService(SaleRepository saleRepository,
                       SaleLineRepository saleLineRepository,
                       BusinessService businessService,
                       ClientService clientService,
                       ProductService productService,
                       ErrorLogService errorLogService) {
        this.saleRepository = saleRepository;
        this.saleLineRepository = saleLineRepository;
        this.businessService = businessService;
        this.clientService = clientService;
        this.productService = productService;
        this.errorLogService = errorLogService;
    }

    public List<Sale> GetSales() {
        return saleRepository.findAll();
    }

    public List<Sale> GetSalesByInterval(Date initialDate, Date finalDate) {
        return saleRepository.findByInterval(initialDate, finalDate);
    }

    public Boolean ExistsSale(Long saleID) {
        return saleRepository.existsById(saleID);
    }

    public Sale GetOneSale(Long saleID) {
        try {
            Optional<Sale> sale = saleRepository.findById(saleID);
            if (sale.isEmpty()) {
                throw new IllegalStateException("Error at 'GetOneSale' - Sale with ID: " + saleID + " doesn't exist");
            }

            return sale.get();
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Sale CreateSale(Sale sale) {
        try {
            throw new Exception("Not implemented");
        }
        catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Sale UpdateSaleState(Long saleID, String state) {
        try {
            Sale sale = GetOneSale(saleID);
            Boolean result = sale.setStateByText(state);
            if (!result) {
                throw new IllegalStateException("Error at 'UpdateNotificationState' - Unexpected value: " + state);
            }

            return sale;
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Sale UpdateSalePartialPayment(Long saleID, Float partialPayment) {
        try {
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
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    // Logic deletion (field: sale state)
    public Sale CancelSale(Long saleID) {
        try {
            Sale sale = GetOneSale(saleID);
            sale.setState(Sale.SaleState.Cancelled);
            saleRepository.save(sale);

            return sale;
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }
}
