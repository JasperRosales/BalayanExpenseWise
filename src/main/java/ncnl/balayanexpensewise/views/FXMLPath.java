package ncnl.balayanexpensewise.views;

public enum FXMLPath {
    LOGIN("/fxml/LoginPage.fxml"),
    SIGNUP("/fxml/SignupPage.fxml"),
    MAIN("/fxml/MainDashboard.fxml");


    private final String path;

    FXMLPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return path;
    }
}
