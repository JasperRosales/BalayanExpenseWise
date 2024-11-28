package ncnl.balayanexpensewise.beans;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
class UserTest {

    String f = "Jasper";
    String m = "Briones";
    String l = "Rosales";

    @Test
    void getInitialMiddleName() {
        String r = m.substring(0,1).toUpperCase() + ".";
        System.out.println("[TEST] Middle Initial: " + r);

        assertEquals("B.", r);
    }


    @Test
    void getFullname() {
        String c = "%s %s %s".formatted(f,m,l);
        System.out.println("[TEST] Fullname: " + c);

        assertEquals("Jasper Briones Rosales",c);


    }
}