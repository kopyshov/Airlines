package dto;

import java.math.BigDecimal;
import java.util.List;

public class Route {

    private BigDecimal totalPrice;
    private int numStops;
    private List<FlightDto> route;
    public Route() {
    }

    public Route(BigDecimal totalPrice, int numStops, List<FlightDto> route) {
        this.totalPrice = totalPrice;
        this.numStops = numStops;
        this.route = route;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getNumStops() {
        return numStops;
    }

    public void setNumStops(int numStops) {
        this.numStops = numStops;
    }

    public List<FlightDto> getRoute() {
        return route;
    }

    public void setRoute(List<FlightDto> route) {
        this.route = route;
    }
}
