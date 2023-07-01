package servlets;

import com.google.gson.Gson;
import dao.AirportDao;
import database.DataSourceFactory;
import dto.ErrorResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Airport;
import org.sqlite.SQLiteException;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "AirportServlet", value = "/airports/*")
public class AirportServlet extends HttpServlet {
    private final AirportDao airportDao;
    public AirportServlet() throws URISyntaxException, SQLException {
        DataSource dataSource = DataSourceFactory.getInstance().getDataSource();
        airportDao = new AirportDao(dataSource.getConnection());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestURI = request.getRequestURI();
        String code = (requestURI).substring(requestURI.lastIndexOf('/') + 1);
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
            Gson gson = new Gson();
            String answer = gson.toJson(airport);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(answer);
            out.flush();
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
                Gson gson = new Gson();
                String  answer = gson.toJson(insertedAirport);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.print(answer);
                out.flush();
            }
        } catch (SQLiteException e) {
            new ErrorResponse(SC_CONFLICT, "Airport exists").send(response);
        } catch (SQLException ex) {
            new ErrorResponse(SC_INTERNAL_SERVER_ERROR, "Database is not available").send(response);
        }
    }
}
