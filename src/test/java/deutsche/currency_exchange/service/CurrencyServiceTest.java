package deutsche.currency_exchange.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CurrencyServiceTest {
    /*
    Create Exchange Rate
 Avoid duplication for the same currency pair and effective date.
 Retrieve Exchange Rate by ID
 Retrieve Exchange Rate for Specific Date and Currency Pair
 Update Exchange Rate (only future effective dates allowed)
 Delete Exchange Rate (only if effective date is in the future)
     */
    @Autowired
    CurrencyExchangeService currencyExchangeService;

    @Test
    public void testCreateExchangeRate() {
        // Implement test logic for creating an exchange rate

    }

}
