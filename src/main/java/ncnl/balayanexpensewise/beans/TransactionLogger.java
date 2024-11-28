package ncnl.balayanexpensewise.beans;


import javafx.scene.control.Label;
import lombok.Data;

@Data
public class TransactionLogger {
    private String description;
    private Integer amount;
    private Double price;
    private Double Budget;
    private String category;


    public String getAmountText() {
        // Calculate total price for the current transaction
        Double totalPrice = this.amount * this.price;

        return this.category.equalsIgnoreCase("service")
                ? "+ " + totalPrice
                : "- " + totalPrice;
    }

    public void updateLabels(Label nameLabel, Label amountLabel, Label priceLabel, Label budgetLabel) {
        nameLabel.setText(this.description);
        amountLabel.setText(this.amount.toString());
        priceLabel.setText(this.price.toString());
        budgetLabel.setText(getAmountText());
    }
}
