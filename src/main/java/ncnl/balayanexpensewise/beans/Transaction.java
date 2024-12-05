package ncnl.balayanexpensewise.beans;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Transaction {
    private Integer id;
    private String name;
    private Integer amount;
    private Double price;
    private Double totalPrice;
    private String category;
    private LocalDateTime transactionDate;

    public Transaction(String name, Double price, Integer amount,String category) {
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.category = category;
    }
}
