package ncnl.balayanexpensewise.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeConfig {

    public DateTimeConfig() {
    }

    public String formatDateTime(LocalDateTime input){
        DateTimeFormatter dateFormt = DateTimeFormatter.ofPattern("MM-dd-yyyy hh-ss a");
        return input.format(dateFormt);
    }
}
