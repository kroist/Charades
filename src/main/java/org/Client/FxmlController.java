package main.java.org.Client;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class FxmlController {

    @FXML private Button connectButton;
    @FXML private TextField ipAndPortField;
    @FXML private Label connectionOutputLabel;
    @FXML private Button connectToAnotherGameButton;
    @FXML private Button createNewGameButton;
    @FXML private TextField gameIdField;

    Controller controller;
    View view;
    public void setVars(Controller controller, View view){
        this.controller = controller;
        this.view = view;
    }

    @FXML
    public void onClickMethod(){
        connectionOutputLabel.setText("TI CHE DALBAEB?)))))");
    }
    @FXML
    public void createNewGameFXML() {
        ///SHOULD LOOK LIKE THIS BUT I HAVEN'T ADDED PRIVATEGAME
        //View.controller.createNewGame(privateGame.isSelected()));
        controller.createNewLobby(false);
        //System.out.println(textField.getCharacters().toString());
    }
    @FXML
    public void connectToExistingGameFXML() {
        controller.connectToTheExistingLobby(gameIdField.getCharacters().toString());
    }
    @FXML
    public void browseGamesClick(){
        ArrayList<String> arr = controller.askForLobbies();
        for (String string : arr){
            System.out.println(string);
        }
    }

}