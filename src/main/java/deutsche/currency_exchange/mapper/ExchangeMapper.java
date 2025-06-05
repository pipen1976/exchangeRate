package deutsche.currency_exchange.mapper;

import deutsche.currency_exchange.beans.ExchangeDTO;
import deutsche.currency_exchange.model.ExchangeRate;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExchangeMapper{

    ExchangeRate toEntity(ExchangeDTO exchangeDTO);
    ExchangeDTO toDTO(ExchangeRate exchangeRate);

    List<ExchangeRate> toEntityList(List<ExchangeDTO> exchangeDTOs);
    List<ExchangeDTO> toDTOList(List<ExchangeRate> exchangeRates);


}
