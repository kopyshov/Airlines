package dto;

import model.Airline;
import model.Airport;

import java.math.BigDecimal;

public record FlightDto(Long id, Airport from_airport, Airport to_airport, Airline airline, BigDecimal price) {
}
