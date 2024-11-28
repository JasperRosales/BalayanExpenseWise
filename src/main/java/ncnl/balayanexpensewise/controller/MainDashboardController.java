package ncnl.balayanexpensewise.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import ncnl.balayanexpensewise.beans.UserLogger;
import ncnl.balayanexpensewise.service.UserLoggerService;
import ncnl.balayanexpensewise.utils.AlarmUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class MainDashboardController extends GenericController implements Initializable {

    private String srcode;
    private String fullname;
    private String department;
    private String role;

    @FXML
    private TabPane MainDashboard;

    @FXML
    private Tab tabSSC, tabCET, tabCICS, tabAdmin;

    @FXML
    private Button exitBtn;

    private final UserLoggerService userLoggerService = new UserLoggerService();

    /**
     * This method is called automatically after the FXML elements have been loaded and injected.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Ensure department and other fields are initialized properly
        System.out.println("Initialization: Department = " + department);

        if (department != null) {
            applyDepartmentVisibility();
        } else {
            System.err.println("Warning: Department is null during initialization.");
        }
    }

    /**
     * Sets the SR code and ensures it's used when needed.
     */
    public void setSrcode(String srcode) {
        this.srcode = srcode;
    }

    /**
     * Sets the department and applies visibility settings for tabs.
     */
    public void setDepartment(String department) {
        this.department = department;

        // If the view is already initialized, apply visibility immediately
        if (MainDashboard != null) {
            applyDepartmentVisibility();
        }
    }

    /**
     * Sets the full name of the user.
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    /**
     * Sets the user's role.
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Applies tab visibility based on the user's department.
     */
    private void applyDepartmentVisibility() {
        switch (department) {
            case "CICS" -> removeTabs(tabAdmin, tabSSC, tabCET);
            case "CET" -> removeTabs(tabAdmin, tabSSC, tabCICS);
            default -> System.out.println("No tabs removed for department: " + department);
        }
    }

    /**
     * Removes specific tabs from the dashboard.
     */
    private void removeTabs(Tab... tabs) {
        for (Tab tab : tabs) {
            if (MainDashboard.getTabs().contains(tab)) {
                MainDashboard.getTabs().remove(tab);
            }
        }
    }

    /**
     * Handles the exit action, logs the signout, and exits the application.
     */
    public void exit(ActionEvent actionEvent) {
        if (AlarmUtils.showExitConfirmationAlert()) {
            UserLogger userLogger = new UserLogger(srcode, fullname, department, role, "SIGNOUT");
            userLoggerService.logUserAction(userLogger);
            System.exit(0);
        }
    }
}
