package services;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class ResponseService {
    private static final Gson gson = new Gson();
    public static void send(Object object, HttpServletResponse response) throws IOException {
        String  answer = gson.toJson(object);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(answer);
        out.flush();
    }
}
