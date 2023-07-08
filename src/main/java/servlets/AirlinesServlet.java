package servlets;

import database.OwnConnectionPool;
import dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Airline;
import services.ResponseService;
import services.common.AirlinesService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@WebServlet(name = "AirlinesServlet", value = "/airlines")
public class AirlinesServlet extends HttpServlet {
    OwnConnectionPool connectionPool;
    AirlinesService airlinesService;
    @Override
    public void init() throws ServletException {
        super.init();
        connectionPool = (OwnConnectionPool) getServletContext().getAttribute("connPool");
        airlinesService = new AirlinesService(connectionPool);
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Airline> airlines = airlinesService.getAllAirlines();
            ResponseService.send(airlines, response);
        } catch (SQLException e) {
            new ErrorResponse(SC_INTERNAL_SERVER_ERROR, "Database is not available").send(response);
        }
    }
}
