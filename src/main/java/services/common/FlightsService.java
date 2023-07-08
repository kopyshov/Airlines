package services.common;

import dao.FlightDao;
import database.OwnConnectionPool;
import dto.FlightDto;
import model.Flight;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class FlightsService {
    private final FlightDao flightDao;

    public FlightsService(OwnConnectionPool connectionPool) {
        flightDao = new FlightDao(connectionPool.getConnection());
    }

    public List<FlightDto> getAllFlights() throws SQLException {
        return flightDao.getAll();
    }

    public FlightDto getFlightById(Long fromAirportId, Long toAirportId, Long airlineId) throws SQLException {
        return flightDao.getById(fromAirportId, toAirportId, airlineId);
    }

    public void saveFlight(Long fromAirportId, Long toAirportId, Long airlineId, BigDecimal price) throws SQLException {
        flightDao.save(new Flight(fromAirportId, toAirportId, airlineId, price));
    }

    public void updatePrice(Long fromAirportId, Long toAirportId, Long airlineId, BigDecimal price) throws SQLException  {
        flightDao.update(new Flight(fromAirportId, toAirportId, airlineId, price));
    }
}
