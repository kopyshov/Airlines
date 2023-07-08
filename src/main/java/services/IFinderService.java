package services;

import dto.Route;

import java.sql.SQLException;
import java.util.List;

public interface IFinderService {
    List<Route> find(String startAirportCode, String finishAirportCode, String maxStops) throws SQLException;
}
