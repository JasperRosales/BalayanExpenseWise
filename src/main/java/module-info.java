module ncnl.balayanexpensewise {
    requires javafx.fxml;
    requires javafx.web;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires org.apache.poi.ooxml;
    requires java.sql;
    requires static lombok;
    requires com.jfoenix;
    requires java.desktop;
    requires net.sf.jasperreports.core;

    opens ncnl.balayanexpensewise to javafx.fxml;
    opens ncnl.balayanexpensewise.beans to javafx.base;
    exports ncnl.balayanexpensewise;
    opens fxml to javafx.fxml;
    exports ncnl.balayanexpensewise.views;
    exports ncnl.balayanexpensewise.controller;
    opens ncnl.balayanexpensewise.controller to javafx.fxml;
}
