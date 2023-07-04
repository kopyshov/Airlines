package servlets;

import com.google.gson.Gson;
import dto.ErrorResponse;
import dto.Route;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.RouteFinder;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@WebServlet(name = "RoutesServlet", value = "/routes/*")
public class RoutesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestURI = request.getRequestURI();
        int servPath = request.getServletPath().length();
        String fromAirportCode = requestURI.substring(servPath + 1, servPath + 4);
        String toAirportCode = requestURI.substring(servPath + 5, servPath + 8);
        String maxStops = request.getParameter("max_stops");

        if(!fromAirportCode.equals("") && !toAirportCode.equals("")) {
            try {
                RouteFinder routeFinder = new RouteFinder();
                List<Route> route = routeFinder.find(fromAirportCode, toAirportCode, maxStops);
                Gson gson = new Gson();
                String  answer = gson.toJson(route);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.print(answer);
                out.flush();
            } catch (SQLException ex) {
                new ErrorResponse(SC_INTERNAL_SERVER_ERROR, "Database is not available").send(response);
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }
    }
}
