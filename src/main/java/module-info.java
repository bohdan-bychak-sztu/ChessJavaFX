module com.bbm.chessjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;

    opens com.bbm.chessjavafx to javafx.fxml;
    opens com.bbm.chessjavafx.controller to javafx.fxml;

    exports com.bbm.chessjavafx;
    exports com.bbm.chessjavafx.controller;
}