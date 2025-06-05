package deutsche.currency_exchange.repository;

import deutsche.currency_exchange.model.ExchangeRate;
import java.util.Date;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CurrencyExchangeRepository extends CrudRepository<ExchangeRate, Long> {

    ExchangeRate findByAndEffectiveDateAndBaseCurrencyAndTargetCurrency(Date effectiveDate, String baseCurrency, String targetCurrency);

    List<ExchangeRate> findByBaseCurrencyAndTargetCurrency(String baseCurrency, String targetCurrency);
}
