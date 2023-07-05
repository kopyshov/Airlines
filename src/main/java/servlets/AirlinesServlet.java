package servlets;

import com.google.gson.Gson;
import dao.AirlineDao;
import database.DataSourceFactory;
import dto.ErrorResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Airline;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@WebServlet(name = "AirlinesServlet", value = "/airlines")
public class AirlinesServlet extends HttpServlet {

    private final AirlineDao airlineDao;

    public AirlinesServlet() throws URISyntaxException, SQLException {
        DataSource dataSource = DataSourceFactory.getInstance().getDataSource();
        airlineDao = new AirlineDao(dataSource.getConnection());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Airline> airlines = airlineDao.getAll();
            Gson gson = new Gson();
            String  answer = gson.toJson(airlines);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(answer);
            out.flush();
        } catch (SQLException e) {
            new ErrorResponse(SC_INTERNAL_SERVER_ERROR, "Database is not available").send(response);
        }
    }
}
