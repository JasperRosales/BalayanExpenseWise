package ncnl.balayanexpensewise.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlarmUtils {

    public static void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Successful");
        alert.setHeaderText(null);
        alert.setContentText("User registered successfully!");
        alert.showAndWait();
    }

    public static void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Product registered successfully!");
        alert.showAndWait();
    }

    public static void showCustomSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    public static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Registration Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean showSignoutConfirmationAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Sign Out Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to sign out?");


        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    public static boolean showRejectConfirmationAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reject Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to reject this?");


        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    public static boolean showExitConfirmationAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit the app?");


        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }


}
