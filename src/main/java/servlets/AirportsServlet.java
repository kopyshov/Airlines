package servlets;

import com.google.gson.Gson;
import dao.AirportDao;
import database.DataSourceFactory;
import dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Airport;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@WebServlet(name = "AirportsServlet", value = "/airports")
public class AirportsServlet extends HttpServlet {
    private final AirportDao airportDao;
    public AirportsServlet() throws SQLException, URISyntaxException {
        DataSource dataSource = DataSourceFactory.getInstance().getDataSource();
        airportDao = new AirportDao(dataSource.getConnection());
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Airport> airports = airportDao.getAll();
            Gson gson = new Gson();
            String answer = gson.toJson(airports);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

    }
}
