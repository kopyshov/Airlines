package servlets;

import dao.AirportDao;
import database.OwnConnectionPool;
import dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Airport;
import org.sqlite.SQLiteException;
import services.ResponseService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "AirportServlet", value = "/airports/*")
public class AirportServlet extends HttpServlet {
    private AirportDao airportDao;
    OwnConnectionPool connectionPool;
    @Override
    public void init() throws ServletException {
        super.init();
        connectionPool = (OwnConnectionPool) getServletContext().getAttribute("connPool");
        airportDao = new AirportDao(connectionPool.getConnection());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestURI = request.getRequestURI();
        String code = requestURI.substring(requestURI.lastIndexOf('/') + 1);
        if(code.length() !=3) {
            new ErrorResponse(SC_BAD_REQUEST, "Airport code is incorrect or doesn't exist").send(response);
            return;
        }
        try {
            Optional<Airport> airport = airportDao.getByCode(code);
            if (airport.isEmpty()){
                new ErrorResponse(SC_NOT_FOUND, "Airport is not founded").send(response);
                return;
            }
            ResponseService.send(airport, response);
        } catch (SQLException sqlException) {
            new ErrorResponse(SC_INTERNAL_SERVER_ERROR, "Database is not available").send(response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.setCharacterEncoding("UTF-8");
            String code = request.getParameter("code");
            String name = request.getParameter("name");

            if (code.length() != 3 || name.length() < 1) {
                new ErrorResponse(SC_BAD_REQUEST, "Required form field is incorrect or doesn't exist").send(response);
            } else {
                Airport insertingAirport = new Airport(code, name);
                airportDao.save(insertingAirport);
                Optional<Airport> insertedAirport = airportDao.getByCode(code);
                ResponseService.send(insertedAirport, response);
            }
        } catch (SQLiteException e) {
            new ErrorResponse(SC_CONFLICT, "Airport exists").send(response);
        } catch (SQLException ex) {
            new ErrorResponse(SC_INTERNAL_SERVER_ERROR, "Database is not available").send(response);
        }
    }
}
