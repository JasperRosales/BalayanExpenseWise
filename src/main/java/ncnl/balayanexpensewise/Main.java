package ncnl.balayanexpensewise;

import javafx.application.Application;
import javafx.stage.Stage;
import ncnl.balayanexpensewise.views.FXMLPath;
import ncnl.balayanexpensewise.views.FXMLViewer;

public class Main extends Application{

    @Override
    public void start(Stage primarysStage) throws Exception {
        FXMLViewer fxmlViewer = new FXMLViewer(primarysStage);

        fxmlViewer.loadFXML(FXMLPath.MAIN);

    }

    public static void main(String[] args) {
        launch(args);
    }

}
