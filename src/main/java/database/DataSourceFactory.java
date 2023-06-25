package database;

import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class DataSourceFactory {
    private static DataSourceFactory instance = null;
    private DataSourceFactory() {
    }

    public static DataSourceFactory getInstance() {
        if(instance==null){
            instance = new DataSourceFactory();
        }
        return instance;
    }

    public DataSource getDataSource() throws URISyntaxException {
        URL resource = DataSourceFactory.class.getClassLoader().getResource("Air.db");
        String path = null;
        if (resource != null) {
            path = new File(resource.toURI()).getAbsolutePath();
        }
        SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
        sqLiteDataSource.setUrl(String.format("jdbc:sqlite:%s", path));
        return sqLiteDataSource;
    }
}
