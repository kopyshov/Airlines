package services.common;

import dao.AirlineDao;
import database.OwnConnectionPool;
import model.Airline;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AirlinesService {

    AirlineDao airlineDao;

    public AirlinesService(OwnConnectionPool connectionPool) {
        airlineDao = new AirlineDao(connectionPool.getConnection());
    }

    public List<Airline> getAllAirlines() throws SQLException {
        return airlineDao.getAll();
    }

    public Optional<Airline> getAirlineByCode(String code) throws SQLException {
        return airlineDao.getByCode(code);
    }

    public void saveAirline(Airline insertingAirline) throws SQLException {
        airlineDao.save(insertingAirline);
    }
}
