module com.chatapp.peertopeerchatapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.chatapp.peertopeerchatapp to javafx.fxml;
    exports com.chatapp.peertopeerchatapp;
}