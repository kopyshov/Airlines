package dto;

import java.math.BigDecimal;
import java.util.List;

public record Route(BigDecimal totalPrice, int numStops, List<FlightDto> route) {
}
