package main.java.org.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginSceneFXMLController {
    @FXML public Button loginButton;
    @FXML public TextField nicknameField;
    public Controller controller;

    @FXML
    public void loginButtonHandler(ActionEvent actionEvent) {
        controller.login(nicknameField.getCharacters().toString());
    }
}
