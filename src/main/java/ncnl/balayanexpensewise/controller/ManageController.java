package ncnl.balayanexpensewise.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;
import ncnl.balayanexpensewise.beans.*;
import ncnl.balayanexpensewise.service.AdminService;
import ncnl.balayanexpensewise.service.RequestService;
import ncnl.balayanexpensewise.service.UserInboxService;
import ncnl.balayanexpensewise.service.UserService;
import ncnl.balayanexpensewise.utils.AlarmUtils;
import ncnl.balayanexpensewise.utils.Validator;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ManageController implements Initializable {

    @Getter
    @Setter
    private String currentDepartment;

    @Getter
    @Setter
    private String currentRole;


    @FXML
    private Button makeChangesBtn, findBtn, clearBtn;

    @FXML
    private TextField firstNameField, middleNameField, lastNameField, srcodeField, passwordField,
            findMemberFirstField, findMemberLastField,
            nameDisplay, srcodeDisplay, roleDisplay, otherRoleDisplay, gsuiteDisplay, departmentDisplay;


    @FXML
    private ComboBox<String> departmentBox, roleBox, dualRoleBox, filterBox;

    @FXML
    private ToggleGroup modeGroup;

    @FXML
    private RadioButton updateMode, addMode, deleteMode, resignationMode;

    private final RequestService requestService = new RequestService();
    private final UserInboxService userInboxService = new UserInboxService();

    private final UserService userService = new UserService();
    private final AdminService adminService = new AdminService();
    private final MainDashboardController controller = new MainDashboardController();




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filterBox.getItems().addAll("SSC", "CICS", "CET");

        validateAuthButtons();

        departmentBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("SSC".equals(newValue)) {
                dualRoleBox.setDisable(false);
                roleBox.getItems().setAll(AdminRoles.getRoleNames());
                dualRoleBox.getItems().setAll(UserRoles.getRoleNames());
            } else {
                roleBox.getItems().setAll(UserRoles.getRoleNames());
                dualRoleBox.setDisable(true);
                dualRoleBox.getItems().clear();
            }
        });

        dualRoleBox.setDisable(true);

        modeGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle == updateMode) {
                enableAllFields();
            } else if (newToggle == addMode) {
                enableAllFields();
            } else if (newToggle == deleteMode) {
                configureDeleteMode();
            } else if (newToggle == resignationMode) {
                configureResignationMode();
            }
        });
        enableAllFields();
    }

    private void enableAllFields() {
        firstNameField.setDisable(false);
        middleNameField.setDisable(false);
        lastNameField.setDisable(false);
        srcodeField.setDisable(false);
        passwordField.setDisable(false);
        departmentBox.setDisable(false);
        roleBox.setDisable(false);
        dualRoleBox.setDisable(false);
    }

    private void configureDeleteMode() {
        disableAllFieldsExcept(srcodeField, firstNameField, middleNameField, lastNameField, departmentBox);
    }

    private void configureResignationMode() {
        disableAllFieldsExcept(srcodeField, firstNameField, middleNameField, lastNameField);
    }

    private void disableAllFieldsExcept(Control... exceptions) {
        List<Control> allFields = List.of(firstNameField, middleNameField, lastNameField, srcodeField, passwordField, departmentBox, roleBox, dualRoleBox);
        allFields.forEach(field -> field.setDisable(true));
        for (Control exception : exceptions) {
            exception.setDisable(false);
        }
    }


    public void validateAuthButtons(){
        System.out.println(currentDepartment + currentDepartment);

        if (Objects.equals(currentDepartment, "SSC")) {
            departmentBox.getItems().addAll("CICS", "CET");
        } else {
            departmentBox.getItems().addAll("SSC", "CICS", "CET");
        }

        if (Objects.equals(currentRole, "President") || Objects.equals(currentRole, "Executive Vice President") || Objects.equals(currentDepartment, "SSC")){
            deleteMode.setDisable(true);
        } else {
            deleteMode.setDisable(false);
        }
    }


    public void handleChange(ActionEvent actionEvent) {
        String firstName = firstNameField.getText();
        String middleName = middleNameField.getText();
        String lastName = lastNameField.getText();
        String srcode = srcodeField.getText();
        String password = passwordField.getText();
        String department = departmentBox.getValue();
        String role = roleBox.getValue();
        String dualRole = dualRoleBox.getValue();
        String gsuite = Validator.generateGSuiteEmail(srcode);

        User user = new User(srcode, firstName, middleName, lastName, department, role, gsuite, password);

        Toggle selectedToggle = modeGroup.getSelectedToggle();
        if (selectedToggle == null) {
            AlarmUtils.showErrorAlert("Please select a mode to proceed.");
            return;
        }

        String requestType = getRequestType(selectedToggle);
        if (requestType == null) {
            AlarmUtils.showErrorAlert("Invalid request type selected.");
            return;
        }

        handleRequest(requestType, user, dualRole);
        clearFieldsField();
    }

    private String getRequestType(Toggle selectedToggle) {
        if (selectedToggle == addMode) {
            return "Add Request";
        } else if (selectedToggle == updateMode) {
            return "Update Request";
        } else if (selectedToggle == deleteMode) {
            return "Delete Request";
        } else if (selectedToggle == resignationMode) {
            return "Resignation Request";
        }
        return null;
    }

    private void handleRequest(String requestType, User user, String dualRole) {
        try {
            boolean isRequestAdded = requestService.addRequest(requestType, user,
                    "Delete Request".equals(requestType) || "Resignation Request".equals(requestType) ? null : dualRole);

            String inboxMessage = switch (requestType) {
                case "Add Request" -> "Adding new member to the organization";
                case "Update Request" -> "Updating member details in the organization";
                case "Delete Request" -> "Removing member from the organization";
                case "Resignation Request" -> "Resignation request from the organization";
                default -> "Processing request";
            };

            UserInbox userInbox = new UserInbox(user, inboxMessage);
            userInboxService.addUserInboxRecord(userInbox);

            if (isRequestAdded) {
                AlarmUtils.showCustomSuccessAlert(requestType + " sent successfully.");
            } else {
                AlarmUtils.showErrorAlert("Failed to send " + requestType.toLowerCase() + ".");
            }
        } catch (Exception e) {
            AlarmUtils.showErrorAlert("Failed to send " + requestType.toLowerCase() + ": " + e.getMessage());
        }
    }



    public void clearFieldsField() {
        firstNameField.clear();
        middleNameField.clear();
        lastNameField.clear();
        srcodeField.clear();
        passwordField.clear();
        departmentBox.setValue(null);
        roleBox.setValue(null);
        dualRoleBox.setValue(null);
    }

    public void clearDisplay(){
        nameDisplay.clear();
        srcodeDisplay.clear();
        departmentDisplay.clear();
        gsuiteDisplay.clear();
        roleDisplay.clear();
        otherRoleDisplay.clear();
    }

    public void clearDisplay(ActionEvent actionEvent){
        clearDisplay();
    }

    public void handleFindMember(ActionEvent actionEvent) {
        String firstName = findMemberFirstField.getText();
        String lastName = findMemberLastField.getText();
        String department = filterBox.getValue();

        if (firstName.isBlank() || lastName.isBlank()) {
            AlarmUtils.showErrorAlert("Please enter both first and last names to search.");
            return;
        }

        if (department == null || department.isBlank()) {
            AlarmUtils.showErrorAlert("Please select a department to filter.");
            return;
        }

        try {
            User user = null;
            Admin admin = null;

            if ("SSC".equals(department)) {
                admin = adminService.findByFirstLastName(firstName, lastName);
            } else if ("CICS".equals(department) || "CET".equals(department)) {
                user = userService.findByFirstLastName(firstName, lastName);
            } else {
                AlarmUtils.showErrorAlert("Invalid department selected.");
                return;
            }

            if (admin != null) {
                nameDisplay.setText(admin.getFirstName() + " " + admin.getMiddleName() + " " + admin.getLastName());
                srcodeDisplay.setText(admin.getSrcode());
                departmentDisplay.setText(admin.getDepartment());
                gsuiteDisplay.setText(admin.getGsuite());
                roleDisplay.setText(admin.getRole());
                otherRoleDisplay.setText(admin.getOtherRole());
            } else if (user != null) {
                nameDisplay.setText(user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName());
                srcodeDisplay.setText(user.getSrcode());
                departmentDisplay.setText(user.getDepartment());
                gsuiteDisplay.setText(user.getGsuite());
                roleDisplay.setText(user.getRole());
                otherRoleDisplay.setText("");
            } else {
                AlarmUtils.showErrorAlert("No user found with the provided details in the selected department.");
                return;
            }

            AlarmUtils.showCustomSuccessAlert("Member found and display fields populated.");
        } catch (Exception e) {
            AlarmUtils.showErrorAlert("Failed to find member: " + e.getMessage());
        }
    }



}
