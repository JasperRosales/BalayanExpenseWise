package ncnl.balayanexpensewise.beans;

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Overloaded version of populateComboBox when there are no assigned roles.
     * @param comboBox The ComboBox to populate.
     * @param roles The list of roles to display in the ComboBox.
     */
    public static void populateComboBox(ComboBox<String> comboBox, List<String> roles) {
        comboBox.getItems().setAll(roles);
    }

}
