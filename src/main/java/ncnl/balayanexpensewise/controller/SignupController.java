package ncnl.balayanexpensewise.controller;

import com.jfoenix.controls.JFXButton;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ncnl.balayanexpensewise.beans.User;
import ncnl.balayanexpensewise.beans.Admin;
import ncnl.balayanexpensewise.service.UserService;
import ncnl.balayanexpensewise.service.AdminService;
import ncnl.balayanexpensewise.utils.AlarmUtils;
import ncnl.balayanexpensewise.utils.Validator;
import ncnl.balayanexpensewise.utils.WindowConfig;
import ncnl.balayanexpensewise.views.FXMLPath;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SignupController extends GenericController implements Initializable {

    @FXML
    public TextField fnameTxt, mnameTxt, lnameTxt, roleTxt, departmentTxt, gsuiteTxt;

    @FXML
    public PasswordField passwordTxt;

    @FXML
    public Hyperlink toLoginLink;

    @FXML
    public ProgressBar progressbar = new ProgressBar(0);

    @FXML
    public JFXButton cancelBtn, signupBtn;

    private List<TextField> textFields;

    private final WindowConfig windowConfig = new WindowConfig();
    private final UserService userService = new UserService();
    private final AdminService adminService = new AdminService();


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
        fnameTxt.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                windowConfig.makeDraggable(fnameTxt);
            }
        });

        textFields = List.of(fnameTxt, mnameTxt, lnameTxt, roleTxt, departmentTxt, gsuiteTxt, passwordTxt);

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
        String dep = departmentTxt.getText();
        String role = roleTxt.getText();
        String gsuite = gsuiteTxt.getText();
        String pass = passwordTxt.getText();

        if (!Validator.isValidGsuiteEmail(gsuite)) {
            AlarmUtils.showErrorAlert("Invalid gsuite email format. It should follow the pattern Ex. '12-12345@g.batstate-u.edu.ph'");
            Validator.clearFields(fnameTxt, mnameTxt,lnameTxt, departmentTxt, roleTxt, gsuiteTxt, passwordTxt);
            return;
        }

        if (!Validator.isValidPassword(pass)) {
            AlarmUtils.showErrorAlert("Password must be at least 8 characters long, contain at least one uppercase letter, and one number.");
            Validator.clearFields(fnameTxt, mnameTxt,lnameTxt, departmentTxt, roleTxt, gsuiteTxt, passwordTxt);
            return;
        }

        String srcode = Validator.extractSrcode(gsuite);
        User user = new User(srcode,fname,mname,lname,dep,role,gsuite,pass);

        if ("SSC".equalsIgnoreCase(departmentTxt.getText())) {
            Admin admin = new Admin(srcode, fname, mname,lname,role,gsuite,false,null,pass);
            adminService.addAdmin(admin);
            AlarmUtils.showCustomSuccessAlert("SSC Admin account created successfully.");
        } else {
            userService.createUser(user);
            AlarmUtils.showCustomSuccessAlert("User account created successfully.");
        }

        Validator.clearFields(fnameTxt, mnameTxt,lnameTxt, departmentTxt, roleTxt, gsuiteTxt, passwordTxt);
        progressbar.setProgress(0);
        fxmlViewer.loadFXML(FXMLPath.LOGIN);
    }
}
