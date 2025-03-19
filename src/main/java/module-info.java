module com.chatapp.peertopeerchatapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.chatapp.peertopeerchatapp to javafx.fxml;
    exports com.chatapp.peertopeerchatapp;
    exports com.chatapp.peertopeerchatapp.client;
    opens com.chatapp.peertopeerchatapp.client to javafx.fxml;
    exports com.chatapp.peertopeerchatapp.server;
    opens com.chatapp.peertopeerchatapp.server to javafx.fxml;
    exports com.chatapp.peertopeerchatapp.utils;
    opens com.chatapp.peertopeerchatapp.utils to javafx.fxml;
}