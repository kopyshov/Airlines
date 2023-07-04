package model;

import java.math.BigDecimal;

public record Flight(Long fromAirportId, Long toAirportId, Long airline, BigDecimal price) {

}
