package ncnl.balayanexpensewise.beans;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class Inbox {
    private Integer id;
    private String description;
    private String department;
    private String category;
    private Integer amount;
    private Double price;
    private Double totalPrice;
    private String timestamp;
    private String remarks;
    private String control;


    public Inbox(){}

    public Inbox(Integer id, String description, String department, String category, Integer amount, Double price, Double totalPrice, String timestamp, String remarks) {
        this.id = id;
        this.description = description;
        this.department = department;
        this.category = category;
        this.amount = amount;
        this.price = price;
        this.totalPrice = totalPrice;
        this.timestamp = timestamp;
        this.remarks = remarks;
    }

    public Inbox(String description, String department , String category, Integer amount, Double price) {
        this.description = description;
        this.department = department;
        this.category = category;
        this.amount = amount;
        this.price = price;
    }

    public String getFormattedTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a");
        this.timestamp = timestamp.format(formatter);
    }
}

