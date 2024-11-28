package ncnl.balayanexpensewise.beans;
import javafx.scene.control.Label;
import lombok.Data;

@Data
public class LabelRow {
    private Label nameLabel;
    private Label amountLabel;
    private Label priceLabel;
    private Label budgetLabel;

    private Label roleLabel;
    private Label eventLabel;

    public LabelRow(Label nameLabel, Label amountLabel, Label priceLabel, Label budgetLabel) {
        this.nameLabel = nameLabel;
        this.amountLabel = amountLabel;
        this.priceLabel = priceLabel;
        this.budgetLabel = budgetLabel;
    }

    public LabelRow(Label nameLabel, Label roleLabel, Label eventLabel) {
        this.nameLabel = nameLabel;
        this.roleLabel = roleLabel;
        this.eventLabel = eventLabel;
    }
}