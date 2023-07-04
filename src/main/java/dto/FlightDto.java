package dto;

import model.Airline;
import model.Airport;

import java.math.BigDecimal;

public class FlightDto {
    private Long id;
    private Airport from_airport;
    private Airport to_airport;
    private Airline airline;
    private BigDecimal price;

    public FlightDto() {
    }

    public FlightDto(Long id, Airport from_airport, Airport to_airport, Airline airline, BigDecimal price) {
        this.id = id;
        this.from_airport = from_airport;
        this.to_airport = to_airport;
        this.airline = airline;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Airport getFrom_airport() {
        return from_airport;
    }

    public void setFrom_airport(Airport from_airport) {
        this.from_airport = from_airport;
    }

    public Airport getTo_airport() {
        return to_airport;
    }

    public void setTo_airport(Airport to_airport) {
        this.to_airport = to_airport;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
