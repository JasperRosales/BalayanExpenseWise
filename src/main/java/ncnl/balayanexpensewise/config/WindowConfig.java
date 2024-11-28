package ncnl.balayanexpensewise.config;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class WindowConfig {

    private double xOffset = 0;
    private double yOffset = 0;

    public void makeDraggable(Node node) {
        node.setOnMousePressed(this::handleMousePressed);
        node.setOnMouseDragged(this::handleMouseDragged);
    }

    private void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    private void handleMouseDragged(MouseEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }
}
