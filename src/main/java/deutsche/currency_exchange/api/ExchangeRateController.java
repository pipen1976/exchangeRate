package deutsche.currency_exchange.api;

import deutsche.currency_exchange.beans.ExchangeDTO;
import deutsche.currency_exchange.service.CurrencyExchangeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing currency exchange rates.
 */
@RestController
public class ExchangeRateController {

    private final CurrencyExchangeService currencyExchangeService;

    public ExchangeRateController(final CurrencyExchangeService currencyExchangeService) {
        this.currencyExchangeService = currencyExchangeService;
    }

    /**
     * Creates a new exchange rate.
     * @param exchangeDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<?> createExchangeRate(@RequestBody ExchangeDTO exchangeDTO) {
        ExchangeDTO exchangeDTOReturned = null;
        try {
            exchangeDTOReturned = currencyExchangeService.createExchangeRate(exchangeDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(exchangeDTOReturned);
    }

    /**
     * Retrieves an exchange rate by its ID.
     * @param id
     * @return
     */
    @GetMapping()
    public ResponseEntity<?> retrieveExchangeRateById(@RequestParam("id") Long id) {
        ExchangeDTO exchangeDTOReturned;
        try {
            exchangeDTOReturned = currencyExchangeService.retrieveExchangeRateById(id);
            return ResponseEntity.ok(exchangeDTOReturned);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    /**
     * Retrieves an exchange rate for a specific date and currency pair.
     * @param exchangeDTO
     * @return
     */
    @GetMapping("by-date-and-currencies")
    public ResponseEntity<?> retrieveExchangeRateForSpecificDateAndCurrencies(
            @RequestBody ExchangeDTO exchangeDTO) {
        ExchangeDTO exchangeDTOReturned = currencyExchangeService.retrieveExchangeRateForSpecificDateAnBaseCurrencyAndTargetCurrency(exchangeDTO);
        return ResponseEntity.ok(exchangeDTOReturned);
    }

    /**
     * Updates an existing exchange rate.
     * @param exchangeDTO
     * @return
     */
    @PutMapping
    public ResponseEntity<?> updateExchangeRate(@RequestBody ExchangeDTO exchangeDTO) {
        ExchangeDTO exchangeDTOReturned;
        try {
            exchangeDTOReturned = currencyExchangeService.updateExchangeRate(exchangeDTO);
            return ResponseEntity.ok(exchangeDTOReturned);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Deletes an existing exchange rate.
     * @param exchangeDTO
     * @return
     * @throws Exception
     */
    @DeleteMapping
    public ResponseEntity<?> deleteExchangeRate(@RequestBody ExchangeDTO exchangeDTO) throws Exception {

        currencyExchangeService.deleteExchangeRate(exchangeDTO);
        return ResponseEntity.ok("Exchange rate deleted successfully.");
    }

    /**
     * Predicts the future exchange rate based on historical data.
     * @param baseCurrency
     * @param targetCurrency
     * @return
     */
    @GetMapping("/predictive-rate")
    public Double getPredictiveRate(@RequestParam("baseCurrency") String baseCurrency,
            @RequestParam("targetCurrency") String targetCurrency) {
        return currencyExchangeService.calculateFutureExchangeRate(baseCurrency, targetCurrency);
    }

}
