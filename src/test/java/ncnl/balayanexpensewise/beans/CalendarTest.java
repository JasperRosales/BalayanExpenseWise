package ncnl.balayanexpensewise.beans;

import org.junit.jupiter.api.Test;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalendarTest {

    @Test
    void getAllMonths() {
        // Expected month names
        List<String> expectedMonths = new ArrayList<>();
        for (Month m : Month.values()) {
            // Convert to title case for better readability
            String monthName = m.name().charAt(0) + m.name().substring(1).toLowerCase();
            expectedMonths.add(monthName);
        }

        // Create an instance of Calendar
        Calendar calendar = new Calendar();

        // Compare expected and actual results
        assertEquals(expectedMonths, calendar.getAllMonths());

        // Print the months (optional, for debugging purposes)
        calendar.getAllMonths().forEach(System.out::println);
    }
}
