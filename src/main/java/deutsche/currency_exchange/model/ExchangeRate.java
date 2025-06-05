package deutsche.currency_exchange.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.sql.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
public class ExchangeRate {
    @Id
    private Long id;

    private String baseCurrency;
    private String targetCurrency;
    private Double rate;
    private Date effectiveDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
