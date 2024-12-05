package ncnl.balayanexpensewise.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class ReportController {


    @FXML
    private JFXButton openOutputFolder, openFolderTemplate,
            generatePrintingReport, generateReportBtn;

    @FXML
    private ComboBox<String> OgranizationBox, periodBox, categoryBox, fileTypeBox;


    public void openfolderOutput(ActionEvent actionEvent) {
    }

    public void openTemplateFolder(ActionEvent actionEvent) {
    }

    public void generateTransReport(ActionEvent actionEvent) {
    }
}
