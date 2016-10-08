package models.entities.adxbasedexchanges;

import com.kritter.constants.AdxBasedExchangesStates;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class MaterialUploadExchangeFormEntity {
	@Getter@Setter
    private String exchange = null;
	@Getter@Setter
    private Integer adxBasedExchangesStates = AdxBasedExchangesStates.BRINGINQUEUE.getCode();
}
