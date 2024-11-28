package ncnl.balayanexpensewise.beans;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Schedule {
    private int id;
    private String eventName;
    private LocalDate eventDate;
    private String description;

    public Schedule() {}

    public Schedule(String eventName, LocalDate eventDate, String description) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.description = description;
    }

    public void setDate(LocalDate date){
        this.eventDate = date;
    }

    public LocalDate getDate(){
        return this.eventDate;
    }

}