package dto;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public record ErrorResponse(int code, String message) {
    public void send(HttpServletResponse response) throws IOException {
        response.setStatus(code);
        Gson gson = new Gson();
        String answer = gson.toJson(this);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(answer);
        out.flush();
    }
}
