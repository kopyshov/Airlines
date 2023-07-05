package services;

import dao.AirportDao;
import dao.FlightDao;
import database.DataSourceFactory;
import dto.Route;
import dto.FlightDto;

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
    List<LinkedList<FlightDto>> routes = new ArrayList<>();
    List<Route> readyRoutes = new ArrayList<>();
    private int MAX_STOP_LIMIT = 5;
    private int MAX_STOP_DEFAULT = 2;
    public RouteFinder() throws URISyntaxException, SQLException {
        DataSource dataSource = DataSourceFactory.getInstance().getDataSource();
        flightDao = new FlightDao(dataSource.getConnection());
        airportDao = new AirportDao(dataSource.getConnection());
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
        List<FlightDto> flightDtos = flightDao.getNeighborsById(startAirportId);
        for(FlightDto flightDto : flightDtos) {
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

    private void findRoutes() {
        Iterator<LinkedList<FlightDto>> routesIterator = routes.listIterator();
        LinkedList<FlightDto> fls = routesIterator.next();
        Long toAirportId = fls.getLast().to_airport().getId();

        List<FlightDto> nextFlightDtos = flightDao.getNeighborsById(toAirportId);

        for (FlightDto flightDto : nextFlightDtos) {
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
