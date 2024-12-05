package ncnl.balayanexpensewise.controller;

import com.jfoenix.controls.JFXButton;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ncnl.balayanexpensewise.beans.*;
import ncnl.balayanexpensewise.service.RequestService;
import ncnl.balayanexpensewise.service.UserInboxService;
import ncnl.balayanexpensewise.service.UserService;
import ncnl.balayanexpensewise.service.AdminService;
import ncnl.balayanexpensewise.utils.AlarmUtils;
import ncnl.balayanexpensewise.utils.Validator;
import ncnl.balayanexpensewise.views.FXMLPath;

import java.net.URL;
import java.util.*;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SignupController extends GenericController implements Initializable {

    @FXML
    private TextField fnameTxt, mnameTxt, lnameTxt, gsuiteTxt;

    @FXML
    private PasswordField passwordTxt;

    @FXML
    private Hyperlink toLoginLink;

    @FXML
    private ProgressBar progressbar = new ProgressBar(0);

    @FXML
    private JFXButton cancelBtn, signupBtn;

    @FXML
    private ComboBox<String> roleBox, departmentBox;

    private List<TextField> textFields;

    private List<String> userRoles = UserRoles.getRoleNames(); // Predefined roles

    private final UserService userService = new UserService();
    private final AdminService adminService = new AdminService();
    private final RequestService requestService = new RequestService();
    private final UserInboxService userInboxService = new UserInboxService();

    /**
     * Checks the fields' input if there is a text, unless all fields are not null it will disable the submit button
     *
     */
    private void updateProgress() {
        long filledFields = textFields.stream().filter(field -> !field.getText().isEmpty()).count();
        double progress = (double) filledFields / textFields.size();
        progressbar.setProgress(progress);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        departmentBox.getItems().addAll("CICS", "CET");
        departmentBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("CICS".equals(newValue) || "CET".equals(newValue)) {
                List<String> assignedRoles = userService.getAssignedRolesByDepartment(newValue);
                DropBox.populateComboBox(roleBox, filterRoles(userRoles, assignedRoles));
            }
        });

        DropBox.populateComboBox(roleBox, filterRoles(userRoles, new ArrayList<>()));

        roleBox.disableProperty().bind(departmentBox.valueProperty().isNull());

        textFields = List.of(fnameTxt, mnameTxt, lnameTxt, gsuiteTxt, passwordTxt);

        BooleanBinding allFieldsFilled = Bindings.createBooleanBinding(
                () -> textFields.stream().allMatch(field -> !field.getText().isEmpty()),
                textFields.stream().map(TextField::textProperty).toArray(Observable[]::new)
        );
        signupBtn.disableProperty().bind(allFieldsFilled.not());

        textFields.forEach(field -> field.textProperty().addListener((observable, oldVal, newVal) -> updateProgress()));

        progressbar.setProgress(0);
    }

    public void goToLoginPage(ActionEvent actionEvent) throws Exception {
        fxmlViewer.loadFXML(FXMLPath.LOGIN);
    }

    public void userExit(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    public void signupUser(ActionEvent actionEvent) throws Exception {

        String fname = fnameTxt.getText();
        String mname = mnameTxt.getText();
        String lname = lnameTxt.getText();
        String dep = departmentBox.getValue();
        String role = roleBox.getValue();
        String gsuite = gsuiteTxt.getText();
        String pass = passwordTxt.getText();

        if (!Validator.isValidGsuiteEmail(gsuite)) {
            AlarmUtils.showErrorAlert("Invalid gsuite email format. It should follow the pattern Ex. '12-12345@g.batstate-u.edu.ph'");
            Validator.clearFields(fnameTxt, mnameTxt, lnameTxt, departmentBox, roleBox, gsuiteTxt, passwordTxt);
            return;
        }

        if (!Validator.isValidPassword(pass)) {
            AlarmUtils.showErrorAlert("Password must be at least 8 characters long, contain at least one uppercase letter, and one number.");
            Validator.clearFields(fnameTxt, mnameTxt, lnameTxt, departmentBox, roleBox, gsuiteTxt, passwordTxt);
            return;
        }

        String srcode = Validator.extractSrcode(gsuite);
        User user = new User(srcode, fname, mname, lname, dep, role, gsuite, pass);

        if (user != null) {
            requestService.addRequest("Add request",user, null);
            UserInbox addInbox = new UserInbox(user, "Adding new member to the organization");
            userInboxService.addUserInboxRecord(addInbox);
            AlarmUtils.showCustomSuccessAlert("Successfully sent to inbox. Please wait a while until admin confirms.");

        } else {
            AlarmUtils.showErrorAlert("Failed to register user!");
        }

        Validator.clearFields(fnameTxt, mnameTxt, lnameTxt, departmentBox, roleBox, gsuiteTxt, passwordTxt);
        progressbar.setProgress(0);
        fxmlViewer.loadFXML(FXMLPath.LOGIN);
    }
    /**
     * Filters out the roles that are already assigned to the department.
     *
     * @param allRoles     The list of all available roles.
     * @param assignedRoles The list of roles already assigned to the department.
     * @return The filtered list of roles that can be assigned.
     */
    private List<String> filterRoles(List<String> allRoles, List<String> assignedRoles) {
        return allRoles.stream()
                .filter(role -> !assignedRoles.contains(role))
                .collect(Collectors.toList());
    }
}
