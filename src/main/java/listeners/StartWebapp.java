package listeners;

import database.impl.OwnConnectionPool;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionListener;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;

@WebListener
public class StartWebapp implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener {
    private final URL resource = StartWebapp.class.getClassLoader().getResource("Air.db");
    private OwnConnectionPool connPool;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
        String path = null;
        if (resource != null) {
            path = new File(resource.toURI()).getAbsolutePath();
        }

        String user = "";
        String password = "";
        connPool = OwnConnectionPool.create(String.format("jdbc:sqlite:%s", path), user, password);

        }catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
        }

        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute("connPool", connPool);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            connPool.shutdown();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
