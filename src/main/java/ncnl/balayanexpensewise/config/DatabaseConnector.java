package ncnl.balayanexpensewise.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {
    private static final Properties properties = new Properties();

    static{
        try(InputStream input = DatabaseConnector.class.getClassLoader().getResourceAsStream("application.properties")){
            if (input == null){
                System.out.println("[ERROR] The resource file is not found");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static Connection getUserConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");

        return DriverManager.getConnection(url,user,password);
    }
}

