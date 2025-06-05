package deutsche.currency_exchange.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import deutsche.currency_exchange.beans.ExchangeDTO;
import deutsche.currency_exchange.model.ExchangeRate;
import deutsche.currency_exchange.repository.CurrencyExchangeRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExchangeControllerRestTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    CurrencyExchangeRepository currencyExchangeRepository;

    @BeforeEach
    public void setup() {
        ExchangeRateController controller = new ExchangeRateController(null); // Pass a mock or null service
    }
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Order(1)
    public void testCreate() throws Exception {
        ExchangeDTO exchangeDTO = new ExchangeDTO();
        exchangeDTO.setBaseCurrency("USD");
        exchangeDTO.setTargetCurrency("EUR");
        exchangeDTO.setRate(1.2);

        // Convert the object to JSON
        String json = objectMapper.writeValueAsString(exchangeDTO);
        when(currencyExchangeRepository.save(any(ExchangeRate.class)))
                .thenReturn(new ExchangeRate()); // Mock the save method to return a new ExchangeRate
        when(currencyExchangeRepository.existsById(anyLong())).thenReturn(false);
        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    public void testRetrieveById() throws Exception {
        Long id = 1L; // Example ID
        when(currencyExchangeRepository.findById(anyLong()))
                .thenReturn(Optional.of(new ExchangeRate()));
        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .param("id", String.valueOf(id)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    public void testRetrieveByDateAndCurrencies() throws Exception {
        ExchangeDTO exchangeDTO = new ExchangeDTO();
        exchangeDTO.setEffectiveDate(new java.util.Date());
        exchangeDTO.setBaseCurrency("USD");
        exchangeDTO.setTargetCurrency("EUR");

        // Convert the object to JSON
        String json = objectMapper.writeValueAsString(exchangeDTO);
        when(currencyExchangeRepository.findByAndEffectiveDateAndBaseCurrencyAndTargetCurrency(
                exchangeDTO.getEffectiveDate(), exchangeDTO.getBaseCurrency(), exchangeDTO.getTargetCurrency()))
                .thenReturn(new ExchangeRate()); // Mock the repository call
        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/by-date-and-currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    public void testUpdate() throws Exception {
        ExchangeDTO exchangeDTO = new ExchangeDTO();
        exchangeDTO.setId(1L); // Example ID
        exchangeDTO.setBaseCurrency("USD");
        exchangeDTO.setTargetCurrency("EUR");
        exchangeDTO.setRate(1.3);
        exchangeDTO.setEffectiveDate(DateUtils.addMonths(new java.util.Date(),1));

        // Convert the object to JSON
         String json = objectMapper.writeValueAsString(exchangeDTO);
        when(currencyExchangeRepository.findById(anyLong()))
                .thenReturn(Optional.of(new ExchangeRate())); // Mock the repository call
        when(currencyExchangeRepository.save(any(ExchangeRate.class)))
                .thenReturn(new ExchangeRate()); // Mock the save method to return a new ExchangeRate
        when(currencyExchangeRepository.existsById(anyLong())).thenReturn(true);

        // Perform the PUT request
        mockMvc.perform(MockMvcRequestBuilders.put("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))// Example new rate
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    public void testDelete() throws Exception {
        ExchangeDTO exchangeDTO = new ExchangeDTO();
        exchangeDTO.setId(1L); // Example ID
        exchangeDTO.setBaseCurrency("USD");
        exchangeDTO.setTargetCurrency("EUR");
        exchangeDTO.setRate(1.3);
        exchangeDTO.setEffectiveDate(DateUtils.addMonths(new java.util.Date(),1));

        String json = objectMapper.writeValueAsString(exchangeDTO);

        when(currencyExchangeRepository.findById(anyLong()))
                .thenReturn(Optional.of(new ExchangeRate()));

        mockMvc.perform(MockMvcRequestBuilders.delete("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isOk());
    }

    @Test
    public void calculateFutureExchangeRate() throws Exception {
        String baseCurrency = "USD";
        String targetCurrency = "EUR";

        // Mock the repository call
        when(currencyExchangeRepository.findByBaseCurrencyAndTargetCurrency(baseCurrency, targetCurrency))
                .thenReturn(getSetOfExchangeRates());
        //assert the response is the double result

        mockMvc.perform(MockMvcRequestBuilders.get("/predictive-rate")
                        .param("baseCurrency", baseCurrency)
                        .param("targetCurrency", targetCurrency))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    Double predictedRate = Double.parseDouble(content);
                    // You can add assertions here to check the predicted rate
                    // For example, assert that the predicted rate is greater than 0
                    assert predictedRate > 0.8 && predictedRate < 1.2;
                });
    }
    private List<ExchangeRate> getSetOfExchangeRates() {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrency("USD");
        exchangeRate.setTargetCurrency("EUR");
        exchangeRate.setRate(1.2);
        exchangeRate.setId(1L);
        exchangeRate.setEffectiveDate(new java.sql.Date(new Date().getTime() - 86400000L)); // 1 day ago
        ExchangeRate exchangeRate2 = new ExchangeRate();
        exchangeRate2.setBaseCurrency("USD");
        exchangeRate2.setTargetCurrency("EUR");
        exchangeRate2.setRate(1.4);
        exchangeRate2.setId(2L);
        exchangeRate2.setEffectiveDate(new java.sql.Date(new Date().getTime() - 86400000L -86400000L)); // 2 days ago
        ExchangeRate exchangeRate3 = new ExchangeRate();
        exchangeRate3.setBaseCurrency("USD");
        exchangeRate3.setTargetCurrency("EUR");
        exchangeRate3.setRate(1.6);
        exchangeRate3.setId(3L);
        exchangeRate3.setEffectiveDate(new java.sql.Date(new Date().getTime() - 86400000L -86400000L -86400000L)); // 3 days ago

        ExchangeRate exchangeRate4 = new ExchangeRate();
        exchangeRate4.setBaseCurrency("USD");
        exchangeRate4.setTargetCurrency("EUR");
        exchangeRate4.setRate(1.8);
        exchangeRate4.setId(4L);
        exchangeRate4.setEffectiveDate(new java.sql.Date(new Date().getTime() - 86400000L -86400000L -86400000L -86400000L)); // 4 days ago

        ExchangeRate exchangeRate5 = new ExchangeRate();
        exchangeRate5.setBaseCurrency("USD");
        exchangeRate5.setTargetCurrency("EUR");
        exchangeRate5.setRate(2.0);
        exchangeRate5.setId(5L);
        exchangeRate5.setEffectiveDate(new java.sql.Date(new Date().getTime() - 86400000L -86400000L -86400000L -86400000L -86400000L));
        ExchangeRate exchangeRate6 = new ExchangeRate();
        exchangeRate6.setBaseCurrency("USD");
        exchangeRate6.setTargetCurrency("EUR");
        exchangeRate6.setRate(2.2);
        exchangeRate6.setId(6L);
        exchangeRate6.setEffectiveDate(new java.sql.Date(new Date().getTime() - 86400000L -86400000L -86400000L -86400000L -86400000L)); // 5 days ago

        ExchangeRate exchangeRate7 = new ExchangeRate();
        exchangeRate7.setBaseCurrency("USD");
        exchangeRate7.setTargetCurrency("EUR");
        exchangeRate7.setRate(2.2);
        exchangeRate7.setId(7L);
        exchangeRate7.setEffectiveDate(new java.sql.Date(new Date().getTime() - 86400000L -86400000L -86400000L -86400000L -86400000L -86400000L)); // 6 days ago


        return List.of(exchangeRate, exchangeRate2, exchangeRate3, exchangeRate4, exchangeRate5, exchangeRate6, exchangeRate7);
    }

}

