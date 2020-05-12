package main.java.org.Client;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class FxmlController {

    @FXML private TextField nicknameField;
    @FXML private Button connectButton;
    @FXML private TextField ipAndPortField;
    @FXML private Label connectionOutputLabel;
    @FXML private Button connectToAnotherGameButton;
    @FXML private Button createNewGameButton;
    @FXML private TextField gameIdField;

    @FXML
    public void onClickMethod(){
        connectionOutputLabel.setText("TI CHE DALBAEB?)))))");
    }
    @FXML
    public void createNewGameFXML() {
        ///SHOULD LOOK LIKE THIS BUT I HAVEN'T ADDED PRIVATEGAME
        //View.controller.createNewGame(privateGame.isSelected()));
        View.controller.createNewGame(false, nicknameField.getCharacters().toString());
        //System.out.println(textField.getCharacters().toString());
    }
    public void connectToExistingGameFXML() {
        View.controller.connectToTheExistingGame(gameIdField.getCharacters().toString(), nicknameField.getCharacters().toString());
    }

}