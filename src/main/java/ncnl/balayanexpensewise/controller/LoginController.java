package ncnl.balayanexpensewise.controller;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ncnl.balayanexpensewise.beans.Admin;
import ncnl.balayanexpensewise.beans.User;
import ncnl.balayanexpensewise.beans.UserLogger;
import ncnl.balayanexpensewise.service.AdminService;
import ncnl.balayanexpensewise.service.UserLoggerService;
import ncnl.balayanexpensewise.service.UserService;
import ncnl.balayanexpensewise.utils.AlarmUtils;
import ncnl.balayanexpensewise.views.FXMLPath;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends GenericController implements Initializable {

    @FXML
    private TextField srcodeTxt;

    @FXML
    private PasswordField passwordTxt;

    @FXML
    private JFXButton loginBtn, cancelBtn;

    @FXML
    private Label loginLabel;

    @FXML
    private Hyperlink goToSignupLink;

    private final UserService userService = new UserService();
    private final AdminService adminService = new AdminService();
    private final UserLoggerService userLoggerService = new UserLoggerService();
    MainDashboardController dashboardController = new MainDashboardController();


    private final BooleanProperty isAdminMode = new SimpleBooleanProperty(false);
    private final BooleanProperty allFieldsValid = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        srcodeTxt.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        passwordTxt.textProperty().addListener((observable, oldValue, newValue) -> validateFields());

        loginBtn.disableProperty().bind(allFieldsValid.not());

        initializeAdminModeListener();

        loginLabel.setText("Login");
    }

    private void validateFields() {
        boolean fieldsAreValid = !srcodeTxt.getText().isEmpty() && !passwordTxt.getText().isEmpty();
        allFieldsValid.set(fieldsAreValid);
    }

    private void initializeAdminModeListener() {
        isAdminMode.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                loginLabel.setText("Admin");
                showAdminModeAlert();
                resetLoginFields();
            } else {
                loginLabel.setText("Login");
            }
        });
    }

    public void loginUser(ActionEvent actionEvent) {
        String srcode = srcodeTxt.getText();
        String password = passwordTxt.getText();



        if (isAdminMode.get()) {
            userLoggerService.setAdminMode(true);

            String adminFullName = userLoggerService.getFullName(srcode);
            String adminDepartment = userLoggerService.getDepartment(srcode);
            String adminRole = userLoggerService.getRole(srcode);
            UserLogger loginAdminRecord = new UserLogger(srcode,adminFullName,adminDepartment,adminRole, "LOGIN");
            handleAdminLogin(srcode, password, loginAdminRecord);

        } else {
            userLoggerService.setAdminMode(false);

            String userFullName = userLoggerService.getFullName(srcode);
            String userDepartment = userLoggerService.getDepartment(srcode);
            String userRole = userLoggerService.getRole(srcode);
            UserLogger loginUserRecord = new UserLogger(srcode, userFullName, userDepartment, userRole, "LOGIN");

            handleUserLogin(srcode, password, loginUserRecord);

        }
    }

    private boolean isAdmin(String srcode, String password) {
        return "admin".equalsIgnoreCase(srcode) && "admin".equalsIgnoreCase(password);
    }

    private void activateAdminMode() {
        isAdminMode.set(true);
    }

    private void handleAdminLogin(String srcode, String password, UserLogger adminCreds) {
        Admin admin = adminService.validateAdmin(srcode, password);
        if (admin != null) {
            loadAdminView();
            userLoggerService.logUserAction(adminCreds);


        } else {
            AlarmUtils.showErrorAlert("[ADMIN] Invalid credentials.");
            resetLoginFields();
        }
    }

    private void handleUserLogin(String srcode, String password, UserLogger loginCreds) {
        if (isAdmin(srcode, password)) {
            activateAdminMode();
        } else {
            User user = userService.validateUser(srcode, password);
            if (user != null) {
                String userDepartment = userLoggerService.getDepartment(srcode);
                loadUserView(userDepartment);
                userLoggerService.logUserAction(loginCreds);


            } else {
                AlarmUtils.showErrorAlert("[USER] Invalid credentials.");
                resetLoginFields();
            }
        }
    }

    private void loadUserView(String department) {
        loadViewWithDepartment(FXMLPath.MAIN, department);
    }

    private void loadAdminView() {
        loadViewWithDepartment(FXMLPath.MAIN, "SSC");
    }


    private void loadViewWithDepartment(FXMLPath path, String department) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path.getPath()));
            Parent root = loader.load();

            MainDashboardController dashboardController = loader.getController();
            String fullName = userLoggerService.getFullName(srcodeTxt.getText());

            dashboardController.setSrcode(srcodeTxt.getText());
            dashboardController.setFullname(fullName);
            dashboardController.setDepartment(department);
            dashboardController.setRole(userLoggerService.getRole(srcodeTxt.getText()));

            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load view: " + path, e);
        }
    }

    private void showAdminModeAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Admin Mode Activated");
        alert.setHeaderText(null);
        alert.setContentText("Admin mode activated. Please re-enter your credentials.");
        alert.showAndWait();
    }

    private void resetLoginFields() {
        srcodeTxt.clear();
        passwordTxt.clear();
    }

    public void goToSignup(ActionEvent actionEvent) throws Exception {
        fxmlViewer.loadFXML(FXMLPath.SIGNUP);
    }

    public void userExit(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}
