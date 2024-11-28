package ncnl.balayanexpensewise.controller;


import ncnl.balayanexpensewise.views.FXMLViewer;

public abstract class GenericController {
    protected FXMLViewer fxmlViewer;

    public void setFxmlManager(FXMLViewer fxmlManager) {
        this.fxmlViewer = fxmlManager;
    }

}
