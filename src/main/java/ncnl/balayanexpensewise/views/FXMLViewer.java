package ncnl.balayanexpensewise.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ncnl.balayanexpensewise.controller.GenericController;

import java.io.IOException;

/**
 * A Class specifically used for loading fxml files for the UI
 */

public class FXMLViewer {
    private final Stage primaryStage;

    public FXMLViewer(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);
    }


    /**
     * This loads a fxml file using FXMLLoader. Maintain the main stage as it stage using the constructor
     * @param view retries a parameter of path from resource
     */

    public void loadFXML(FXMLPath view) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(FXMLViewer.class.getResource(view.getPath()));
            Parent root = loader.load();

            if (loader.getController() instanceof GenericController controller) {
                controller.setFxmlManager(this);
            }

            primaryStage.setScene(new Scene(root));
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Failed to load FXML file: " + view.getPath(), e);
        }
    }


}

