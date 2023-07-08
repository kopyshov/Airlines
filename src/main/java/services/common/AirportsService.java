package services.common;

import dao.AirportDao;
import database.OwnConnectionPool;
import model.Airport;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AirportsService {
    AirportDao airportDao;

    public AirportsService(OwnConnectionPool connectionPool) {
        airportDao = new AirportDao(connectionPool.getConnection());
    }

    public List<Airport> getAllAirports() throws SQLException {
        return airportDao.getAll();
    }

    public Optional<Airport> getAirportByCode(String code) throws SQLException {
        return airportDao.getByCode(code);
    }

    public void saveAirport(Airport insertingAirline) throws SQLException {
        airportDao.save(insertingAirline);
    }
}
