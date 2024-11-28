package ncnl.balayanexpensewise.beans;

import java.util.List;
import javafx.scene.control.ComboBox;

public class DropBox {

    /**
     * Populates a list of ComboBox controls with the given values.
     *
     * @param comboBoxes The list of ComboBox controls to populate.
     * @param values     The values to add to each ComboBox.
     */
    public static void populateComboBoxes(List<ComboBox<String>> comboBoxes, List<String> values) {
        for (ComboBox<String> comboBox : comboBoxes) {
            comboBox.getItems().clear();
            comboBox.getItems().addAll(values);
        }
    }


}
