package servlets;

import database.OwnConnectionPool;
import dto.ErrorResponse;
import dto.Route;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.IFinderService;
import services.ResponseService;
import services.RouteFinder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@WebServlet(name = "RoutesServlet", value = "/routes")
public class RoutesServlet extends HttpServlet {
    OwnConnectionPool connectionPool;
    IFinderService routesFinder;
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            connectionPool = (OwnConnectionPool) getServletContext().getAttribute("connPool");
            routesFinder = new RouteFinder(connectionPool);
        } catch (URISyntaxException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String fromAirportCode = request.getParameter("from_airport_code");
        String toAirportCode = request.getParameter("to_airport_code");
        String maxStops = request.getParameter("max_stops");

        if(!fromAirportCode.equals("") && !toAirportCode.equals("")) {
            try {
                List<Route> route = routesFinder.find(fromAirportCode, toAirportCode, maxStops);
                ResponseService.send(route, response);
            } catch (SQLException ex) {
                new ErrorResponse(SC_INTERNAL_SERVER_ERROR, "Database is not available").send(response);
            }
        }
    }
}
