package services.finder;

import dao.impl.AirportDao;
import dao.impl.FlightDao;
import database.impl.OwnConnectionPool;
import dto.FlightDto;
import dto.Route;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class RouteFinder implements IFinderService {
    private final FlightDao flightDao;
    private final AirportDao airportDao;
    Long startAirportId;
    Long finishAirportId;
    int maxNumStops;
    List<LinkedList<FlightDto>> routes = new ArrayList<>();
    List<Route> readyRoutes = new ArrayList<>();
    private static final int MAX_STOP_LIMIT = 5;
    private static final int MAX_STOP_DEFAULT = 2;

    public RouteFinder(OwnConnectionPool connectionPool) throws URISyntaxException, SQLException {
        flightDao = new FlightDao(connectionPool.getConnection());
        airportDao = new AirportDao(connectionPool.getConnection());
    }

    public List<Route> find(String startAirportCode, String finishAirportCode, String maxStops) throws SQLException {
        startAirportId = airportDao.getByCode(startAirportCode).orElseThrow().getId();
        finishAirportId = airportDao.getByCode(finishAirportCode).orElseThrow().getId();
        if(maxStops == null) {
            maxNumStops = MAX_STOP_DEFAULT;
        } else {
            maxNumStops = Integer.parseInt(maxStops);
            if(maxNumStops > MAX_STOP_LIMIT) {
                maxNumStops = MAX_STOP_LIMIT;
            }
        }
        List<FlightDto> flights = flightDao.getNeighborsById(startAirportId);
        for(FlightDto flightDto : flights) {
            LinkedList<FlightDto> anotherRoute = new LinkedList<>();
            if (flightDto.to_airport().getId().equals(finishAirportId)) {
                anotherRoute.add(flightDto);
                int numStops = 0;
                Route readyRoute = new Route(calculatePrice(anotherRoute), numStops, anotherRoute);
                readyRoutes.add(readyRoute);
            } else {
                anotherRoute.add(flightDto);
                routes.add(anotherRoute);
            }
        }
        findRoutes();
        return readyRoutes;
    }

    private void findRoutes() throws SQLException {
        Iterator<LinkedList<FlightDto>> routesIterator = routes.listIterator();
        LinkedList<FlightDto> fls = routesIterator.next();
        Long toAirportId = fls.getLast().to_airport().getId();

        List<FlightDto> neighborsFlights = flightDao.getNeighborsById(toAirportId);

        for (FlightDto flightDto : neighborsFlights) {
            if (flightDto.to_airport().getId().equals(finishAirportId)) {
                LinkedList<FlightDto> anotherRoute = new LinkedList<>(fls);
                anotherRoute.add(flightDto);
                int numStops = anotherRoute.size() - 1;
                if(numStops < maxNumStops || numStops == maxNumStops) {
                    Route readyRoute = new Route(calculatePrice(anotherRoute), numStops, anotherRoute);
                    readyRoutes.add(readyRoute);
                }
            } else {
                LinkedList<FlightDto> anotherRoute = new LinkedList<>(fls);
                anotherRoute.add(flightDto);
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

    private BigDecimal calculatePrice(LinkedList<FlightDto> route) {
        BigDecimal totalPrice = new BigDecimal(0);
        for(FlightDto flightDto : route) {
            totalPrice = totalPrice.add(flightDto.price());
        }
        return totalPrice;
    }
}
