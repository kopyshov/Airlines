package servlets;

import com.google.gson.Gson;
import dao.AirlineDao;
import dao.AirportDao;
import dao.FlightDao;
import database.DataSourceFactory;
import dto.ErrorResponse;
import model.Flight;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dto.FlightDto;
import org.sqlite.SQLiteException;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "FlightServlet", value = "/flights/*")
public class FlightsServlet extends HttpServlet {

    private final FlightDao flightDao;
    private final AirportDao airportDao;
    private final AirlineDao airlineDao;

    public FlightsServlet() throws URISyntaxException, SQLException {
        DataSource dataSource = DataSourceFactory.getInstance().getDataSource();
        flightDao = new FlightDao(dataSource.getConnection());
        airportDao = new AirportDao(dataSource.getConnection());
        airlineDao = new AirlineDao(dataSource.getConnection());
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
            String requestURI = request.getRequestURI();
            int servPath = request.getServletPath().length();
            String fromAirportCode = requestURI.substring(servPath + 1, servPath + 4);
            String toAirportCode = requestURI.substring(servPath + 4, servPath + 7);
            String airline = requestURI.substring(servPath + 7, servPath + 9);
            BigDecimal price = new BigDecimal(request.getReader().readLine().replace("price=", "")).setScale(2, RoundingMode.HALF_UP);
            if (fromAirportCode.length() != 3 || toAirportCode.length() != 3 || airline.length() != 2) {
                new ErrorResponse(SC_BAD_REQUEST, "Required form field is incorrect or doesn't exist").send(response);
            } else {
                Long fromAirportId = airportDao.getByCode(fromAirportCode).orElseThrow().getId();
                Long toAirportId = airportDao.getByCode(toAirportCode).orElseThrow().getId();
                Long airlineId = airlineDao.getByCode(airline).orElseThrow().getId();
                flightDao.update(new Flight(fromAirportId, toAirportId, airlineId, price));
                FlightDto flightDto = flightDao.getById(fromAirportId, toAirportId, airlineId);
                Gson gson = new Gson();
                String answer = gson.toJson(flightDto);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.print(answer);
                out.flush();
            }
        } catch (SQLException e) {
            new ErrorResponse(SC_INTERNAL_SERVER_ERROR, "Database is not available").send(response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<FlightDto> flightDtos = flightDao.getAll();
            Gson gson = new Gson();
            String  answer = gson.toJson(flightDtos);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(answer);
            out.flush();
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
                Gson gson = new Gson();
                String answer = gson.toJson(flightDto);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.print(answer);
                out.flush();
            }
        } catch (SQLiteException e) {
            new ErrorResponse(SC_CONFLICT, "Flight exists").send(response);
        } catch (SQLException e) {
            new ErrorResponse(SC_INTERNAL_SERVER_ERROR, "Database is not available").send(response);
        }
    }

}
