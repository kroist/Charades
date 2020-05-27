package com.charades.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class LoginSceneFXMLController {
    @FXML public Button loginButton;
    @FXML public TextField nicknameField;
    @FXML public Text nicknameTakenBox;
    public Controller controller;

    @FXML
    public void loginButtonHandler(ActionEvent actionEvent) {
        controller.login(nicknameField.getCharacters().toString());
    }
}
