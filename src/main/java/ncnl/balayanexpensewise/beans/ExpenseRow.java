package ncnl.balayanexpensewise.beans;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lombok.Data;

@Data
public class ExpenseRow {
    private final TextField nameField;
    private final TextField quantityField;
    private final TextField priceField;
    private final ComboBox<String> categoryComboBox;
    private final ComboBox<String> departmentComboBox;

    public ExpenseRow(TextField nameField, TextField quantityField, TextField priceField, ComboBox<String> categoryComboBox, ComboBox<String> departmentComboBox) {
        this.nameField = nameField;
        this.quantityField = quantityField;
        this.priceField = priceField;
        this.categoryComboBox = categoryComboBox;
        this.departmentComboBox = departmentComboBox;
    }

    public ExpenseRow(TextField nameField, TextField quantityField, TextField priceField, ComboBox<String> categoryComboBox, String department) {
        this.nameField = nameField;
        this.quantityField = quantityField;
        this.priceField = priceField;
        this.categoryComboBox = categoryComboBox;
        this.departmentComboBox = new ComboBox<>();
        this.getDepartmentComboBox().setValue(department);
    }

    /**
     * Checks if all fields are filled out, including category and department.
     */
    public boolean isComplete() {
        return !nameField.getText().isEmpty() &&
                !quantityField.getText().isEmpty() &&
                !priceField.getText().isEmpty() &&
                categoryComboBox.getValue() != null &&
                departmentComboBox.getValue() != null;
    }


    public int getQuantity() {
        try {
            return Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public double getPrice() {
        try {
            return Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

}
