package main.java.org.Client;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.ArrayList;

public class FxmlController {

    @FXML private Button connectButton;
    @FXML private TextField ipAndPortField;
    @FXML private Label connectionOutputLabel;
    @FXML private Button connectToLobbyButton;
    @FXML private Button createNewLobbyButton;
    @FXML private TextField gameIdField;
    @FXML private ListView<HBoxButton> listOfLobbies;

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
    public void connectToExistingLobby() {
        controller.connectToTheExistingLobby(gameIdField.getCharacters().toString());
    }

    public static class HBoxButton extends HBox {
        Label label = new Label();
        Button button = new Button();
        HBoxButton(String text, Controller controller){
            ///TODO don't forget to ban sign ':' from the nickname!
            super();
            String[] split = text.split(":");
            label.setText(text);
            label.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(label, Priority.ALWAYS);
            this.setAlignment(Pos.CENTER);
            button.setText("Connect");
            //button.getStylesheets().add("/main/resources/boostrap3.css");
            button.getStyleClass().add("info");
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    System.out.println(split[0]);
                    controller.connectToTheExistingLobby(split[3]);
                }
            });
            this.getChildren().addAll(label, button);
        }
    }

    @FXML
    public void refreshListClick(){
        ObservableList<HBoxButton> items = FXCollections.observableArrayList();
        ArrayList<String> arr = controller.askForLobbies();
        for (String string : arr){
            items.add(new HBoxButton(string, controller));
        }
        listOfLobbies.setItems(items);
    }

}