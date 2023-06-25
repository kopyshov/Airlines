package dto;

import model.Flight;

import java.math.BigDecimal;
import java.util.List;

public class Route {

    private BigDecimal totalPrice;
    private int numStops;
    private List<Flight> route;
    public Route() {
    }

    public Route(BigDecimal totalPrice, int numStops, List<Flight> route) {
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

    public List<Flight> getRoute() {
        return route;
    }

    public void setRoute(List<Flight> route) {
        this.route = route;
    }
}
