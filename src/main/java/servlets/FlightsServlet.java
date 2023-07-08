package servlets;

import dao.AirlineDao;
import dao.AirportDao;
import dao.FlightDao;
import database.OwnConnectionPool;
import dto.ErrorResponse;
import dto.FlightDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Flight;
import org.sqlite.SQLiteException;
import services.ResponseService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "FlightServlet", value = "/flights/*")
public class FlightsServlet extends HttpServlet {
    private FlightDao flightDao;
    private AirportDao airportDao;
    private AirlineDao airlineDao;
    OwnConnectionPool connectionPool;
    @Override
    public void init() throws ServletException {
        super.init();
        connectionPool = (OwnConnectionPool) getServletContext().getAttribute("connPool");
        flightDao = new FlightDao(connectionPool.getConnection());
        airportDao = new AirportDao(connectionPool.getConnection());
        airlineDao = new AirlineDao(connectionPool.getConnection());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getMethod();
        if (!method.equals("PATCH")) {
            super.service(request, response);
            return;
        }
        this.doPatch(request, response);
    }

    private void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.setCharacterEncoding("UTF-8");
            String fromAirportCode = request.getParameter("from_airport_code");
            String toAirportCode = request.getParameter("to_airport_code");
            String airline = request.getParameter("airline");
            BigDecimal price = new BigDecimal(request.getReader().readLine().replace("price=", "")).setScale(2, RoundingMode.HALF_UP);
            if (fromAirportCode.length() != 3 || toAirportCode.length() != 3 || airline.length() != 2) {
                new ErrorResponse(SC_BAD_REQUEST, "Required form field is incorrect or doesn't exist").send(response);
                return;
            }
            Long fromAirportId = airportDao.getByCode(fromAirportCode).orElseThrow().getId();
            Long toAirportId = airportDao.getByCode(toAirportCode).orElseThrow().getId();
            Long airlineId = airlineDao.getByCode(airline).orElseThrow().getId();
            if(fromAirportId == null || toAirportId == null || airlineId == null) {
                new ErrorResponse(SC_BAD_REQUEST, "Required form field doesn't exist").send(response);
                return;
            }
            flightDao.update(new Flight(fromAirportId, toAirportId, airlineId, price));
            FlightDto flight = flightDao.getById(fromAirportId, toAirportId, airlineId);
            ResponseService.send(flight, response);
        } catch (SQLException e) {
            new ErrorResponse(SC_INTERNAL_SERVER_ERROR, "Database is not available").send(response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<FlightDto> flights = flightDao.getAll();
            ResponseService.send(flights, response);
        } catch (SQLException e) {
            new ErrorResponse(SC_INTERNAL_SERVER_ERROR, "Database is not available").send(response);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.setCharacterEncoding("UTF-8");
            String fromAirportCode = request.getParameter("from_airport_code");
            String toAirportCode = request.getParameter("to_airport_code");
            String airline = request.getParameter("airline");
            BigDecimal price = new BigDecimal(request.getParameter("price")).setScale(2, RoundingMode.HALF_UP);

            if (fromAirportCode.length() != 3 || toAirportCode.length() != 3 || airline.length() != 2) {
                new ErrorResponse(SC_BAD_REQUEST, "Required form field is incorrect or doesn't exist").send(response);
            } else {
                Long fromAirportId = airportDao.getByCode(fromAirportCode).orElseThrow().getId();
                Long toAirportId = airportDao.getByCode(toAirportCode).orElseThrow().getId();
                Long airlineId = airlineDao.getByCode(airline).orElseThrow().getId();
                flightDao.save(new Flight(fromAirportId, toAirportId, airlineId, price));
                FlightDto flightDto = flightDao.getById(fromAirportId, toAirportId, airlineId);
                ResponseService.send(flightDto, response);
            }
        } catch (SQLiteException e) {
            new ErrorResponse(SC_CONFLICT, "Flight exists").send(response);
        } catch (SQLException e) {
            new ErrorResponse(SC_INTERNAL_SERVER_ERROR, "Database is not available").send(response);
        }
    }
}
