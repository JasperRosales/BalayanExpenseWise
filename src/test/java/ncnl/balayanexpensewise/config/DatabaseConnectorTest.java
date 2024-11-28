package ncnl.balayanexpensewise.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectorTest {

    private static final Properties property = new Properties();

    static{
        try(InputStream input = DatabaseConnectorTest.class.getClassLoader().getResourceAsStream("application.properties")){
            if(input == null){
                System.out.println("[Error] Properties are not found");
            }
            property.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void checkProperties(){
        String url = property.getProperty("db.url");
        String u = property.getProperty("db.user");
        String p = property.getProperty("db.password");
        assertEquals("jdbc:mysql//localhost:3660/userlist",url);
        assertEquals("root", u);
        assertEquals("Skurohoshi26", p);
    }

    @Test
    void getUserConnection() {

    }
}