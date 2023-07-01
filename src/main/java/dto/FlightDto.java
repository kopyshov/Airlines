package dto;

import java.math.BigDecimal;

public record FlightDto(Long fromAirportId, Long toAirportId, Long airline, BigDecimal price) {

}
