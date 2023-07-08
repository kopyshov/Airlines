package servlets;

import dao.AirlineDao;
import database.OwnConnectionPool;
import dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Airline;
import org.sqlite.SQLiteException;
import services.ResponseService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "AirlineServlet", value = "/airlines/*")
public class AirlineServlet extends HttpServlet {
    private AirlineDao airlineDao;
    OwnConnectionPool connectionPool;
    @Override
    public void init() throws ServletException {
        super.init();
        connectionPool = (OwnConnectionPool) getServletContext().getAttribute("connPool");
        airlineDao = new AirlineDao(connectionPool.getConnection());
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestURI = request.getRequestURI();
        String code = requestURI.substring(requestURI.lastIndexOf('/') + 1);
        if(code.length() != 2) {
            new ErrorResponse(SC_BAD_REQUEST, "Airline code is incorrect or doesn't exist").send(response);
            return;
        }
        try {
            Optional<Airline> airline = airlineDao.getByCode(code);
            if (airline.isEmpty()){
                new ErrorResponse(SC_NOT_FOUND, "Airline is not founded").send(response);
                return;
            }
            ResponseService.send(airline, response);
        } catch (SQLException e) {
            new ErrorResponse(SC_INTERNAL_SERVER_ERROR, "Database is not available").send(response);
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.setCharacterEncoding("UTF-8");
            String code = request.getParameter("code");
            String name = request.getParameter("name");

            if (code.length() != 2 || name.length() < 1) {
                new ErrorResponse(SC_BAD_REQUEST, "Required form field is incorrect or doesn't exist").send(response);
            } else {
                Airline insertingAirline = new Airline(code, name);
                airlineDao.save(insertingAirline);
                Airline airline = airlineDao.getByCode(code).orElseThrow();
                ResponseService.send(airline, response);
            }
        } catch (SQLiteException e) {
            new ErrorResponse(SC_CONFLICT, "Airline exists").send(response);
        } catch (SQLException ex) {
            new ErrorResponse(SC_INTERNAL_SERVER_ERROR, "Database is not available").send(response);
        }
    }
}
