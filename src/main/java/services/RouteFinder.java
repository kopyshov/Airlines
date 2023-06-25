package services;

import dao.AirportDao;
import dao.FlightDao;
import database.DataSourceFactory;
import dto.Route;
import model.Flight;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class RouteFinder {
    private final FlightDao flightDao;
    private final AirportDao airportDao;

    Long startAirportId;
    Long finishAirportId;
    int maxNumStops;
    List<LinkedList<Flight>> routes = new ArrayList<>();
    List<Route> readyRoutes = new ArrayList<>();
    public RouteFinder() throws URISyntaxException, SQLException {
        DataSource dataSource = DataSourceFactory.getInstance().getDataSource();
        flightDao = new FlightDao(dataSource.getConnection());
        airportDao = new AirportDao(dataSource.getConnection());
    }

    public List<Route> find(String startAirportCode, String finishAirportCode, String maxStops) throws SQLException {
        startAirportId = airportDao.getByCode(startAirportCode).orElseThrow().getId();
        finishAirportId = airportDao.getByCode(finishAirportCode).orElseThrow().getId();
        if(maxStops == null) {
            maxNumStops = 2;
        } else {
            maxNumStops = Integer.parseInt(maxStops);
            if(maxNumStops > 5) {
                maxNumStops = 5;
            }
        }
        List<Flight> flights = flightDao.getNeighborsById(startAirportId);
        for(Flight flight : flights) {
            LinkedList<Flight> anotherRoute = new LinkedList<>();
            if (flight.getTo_airport().getId().equals(finishAirportId)) {
                anotherRoute.add(flight);
                int numStops = 0;
                Route readyRoute = new Route(calculatePrice(anotherRoute), numStops, anotherRoute);
                readyRoutes.add(readyRoute);
            } else {
                anotherRoute.add(flight);
                routes.add(anotherRoute);
            }
        }
        findRoutes();
        return readyRoutes;
    }

    private void findRoutes() {
        Iterator<LinkedList<Flight>> routesIterator = routes.listIterator();
        LinkedList<Flight> fls = routesIterator.next();
        Long toAirportId = fls.getLast().getTo_airport().getId();

        List<Flight> nextFlights = flightDao.getNeighborsById(toAirportId);

        for (Flight flight : nextFlights) {
            if (flight.getTo_airport().getId().equals(finishAirportId)) {
                LinkedList<Flight> anotherRoute = new LinkedList<>(fls);
                anotherRoute.add(flight);
                int numStops = anotherRoute.size() - 1;
                if(numStops < maxNumStops || numStops == maxNumStops) {
                    Route readyRoute = new Route(calculatePrice(anotherRoute), numStops, anotherRoute);
                    readyRoutes.add(readyRoute);
                }
            } else {
                LinkedList<Flight> anotherRoute = new LinkedList<>(fls);
                anotherRoute.add(flight);
                if(anotherRoute.size() < maxNumStops || anotherRoute.size() == maxNumStops) {
                    routes.add(anotherRoute);
                }
            }
        }
        routes.remove(fls);
        if(!routes.isEmpty()) {
            findRoutes();
        }
    }

    private BigDecimal calculatePrice(LinkedList<Flight> route) {
        BigDecimal totalPrice = new BigDecimal(0);
        for(Flight flight : route) {
            totalPrice = totalPrice.add(flight.getPrice());
        }
        return totalPrice;
    }
}
