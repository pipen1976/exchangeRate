package deutsche.currency_exchange.service;

import deutsche.currency_exchange.beans.ExchangeDTO;
import deutsche.currency_exchange.mapper.ExchangeMapper;
import deutsche.currency_exchange.model.ExchangeRate;
import deutsche.currency_exchange.repository.CurrencyExchangeRepository;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.data.vector.DoubleVector;
import smile.data.vector.IntVector;
import smile.regression.LinearModel;
import smile.regression.OLS;

@Service
public class CurrencyExchangeService {

    Logger log = LoggerFactory.getLogger(CurrencyExchangeService.class);

    private final CurrencyExchangeRepository currencyExchangeRepository;
    private final ExchangeMapper exchangeMapper;

    public CurrencyExchangeService(CurrencyExchangeRepository currencyExchangeRepository, final ExchangeMapper exchangeMapper) {
        this.currencyExchangeRepository = currencyExchangeRepository;
        this.exchangeMapper = exchangeMapper;
    }

    public ExchangeDTO createExchangeRate(ExchangeDTO exchangeDTO) throws Exception {
        // Logic to create an exchange rate
        // Check for duplication based on currency pair and effective date
        if(currencyExchangeRepository.existsById(exchangeDTO.getId())) {
            throw new Exception("Exchange rate for this currency pair and effective date already exists.");
        }
        return exchangeMapper.toDTO(currencyExchangeRepository.save(exchangeMapper.toEntity(exchangeDTO)));

    }

    public ExchangeDTO retrieveExchangeRateById(Long id) throws Exception {
        // Logic to retrieve an exchange rate by ID
        return exchangeMapper.toDTO(currencyExchangeRepository.findById(id)
            .orElseThrow(() -> new Exception("Exchange rate not found for ID: " + id)));
    }

    public ExchangeDTO retrieveExchangeRateForSpecificDateAnBaseCurrencyAndTargetCurrency(ExchangeDTO exchangeDTO) {
        // Logic to retrieve an exchange rate for a specific date and currency pair
        return exchangeMapper.toDTO(currencyExchangeRepository.findByAndEffectiveDateAndBaseCurrencyAndTargetCurrency(
            exchangeDTO.getEffectiveDate(), exchangeDTO.getBaseCurrency(), exchangeDTO.getTargetCurrency()));
    }

    public ExchangeDTO updateExchangeRate(ExchangeDTO exchangeDTO) throws Exception {
        // Logic to update an exchange rate
        // Ensure that only future effective dates are allowed for updates
        if (!checkEffectiveDate(exchangeDTO)) {
            throw new Exception("Cannot update exchange rate for past effective dates.");
        }
        if( !currencyExchangeRepository.existsById(exchangeDTO.getId())) {
            throw new Exception("Exchange rate not found for ID: " + exchangeDTO.getId());
        }
        return exchangeMapper.toDTO(currencyExchangeRepository.save(exchangeMapper.toEntity(exchangeDTO)));

    }

    public void deleteExchangeRate(ExchangeDTO exchangeDTO) throws Exception {
        if(!checkEffectiveDate(exchangeDTO)) {
            throw new BadRequestException("Cannot delete exchange rate for past effective dates.");
        }
       currencyExchangeRepository.delete(currencyExchangeRepository.findById(exchangeDTO.getId())
            .orElseThrow(() -> new Exception("Exchange rate not found for ID: " + exchangeDTO.getId())));
    }
    boolean checkEffectiveDate(ExchangeDTO exchangeDTO) {
        // Logic to check if the effective date is in the future
        return exchangeDTO.getEffectiveDate().after(new java.util.Date());

    }

    public Double calculateFutureExchangeRate(String baseCurrency, String targetCurrency) {
        List<ExchangeDTO> exchangeDTOs = currencyExchangeRepository.findByBaseCurrencyAndTargetCurrency(baseCurrency, targetCurrency)
                .stream()
                .map(exchangeMapper::toDTO)
                .collect(Collectors.toList());

        if (!exchangeDTOs.isEmpty()) {
            return calculateFutureExchangeRate(exchangeDTOs, 7);
        }else{
            throw new IllegalArgumentException("No historical data available for the specified currency pair.");
        }
    }
    /**
     * Implement a basic predictive calculation of future exchange rates using a simple
     * moving average (SMA) based on historical data from the past 7 days.
     * @param exchangeDTOs
     * @return
     */
    public Double calculateFutureExchangeRate(List<ExchangeDTO> exchangeDTOs, int days) {
        // Calculate the simple moving average (SMA) for the past 7 days
        List<ExchangeRate> exchangeRates = exchangeDTOs.stream()
                .map(exchangeMapper::toEntity)
                .collect(Collectors.toList());

        if (exchangeRates.size() < days) {
            throw new IllegalArgumentException("Not enough historical data to calculate future rates.");
        }

        double[] sma = exchangeRates.stream()
                .mapToDouble(exchangeRate -> exchangeRate.getEffectiveDate().getTime())
                .toArray();

        DataFrame df = DataFrame.of(
                DoubleVector.of("Time", sma),
                DoubleVector.of("Value", exchangeRates.stream().mapToDouble(exchangeRate -> exchangeRate.getRate()).toArray())
        );
        LinearModel model = OLS.fit(Formula.lhs("Value"), df);
        double[] valuesToPredict = new double[1];
        valuesToPredict[0] = new Date().getTime();
        double[] predictedValues = model.predict(DataFrame.of(
                DoubleVector.of("Time", valuesToPredict)
        ));
        log.info("Predicted value at t=6: " + predictedValues[0]);
        return predictedValues[0];


    }


}
