package dao;

import dto.FlightDto;
import model.Flight;

import java.sql.SQLException;

public interface IFlightsDAO {
    void update(Flight flight);
    FlightDto getById(Long fromAirportId, Long toAirportId, Long airlineId) throws SQLException;

}
