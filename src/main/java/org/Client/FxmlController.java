package main.java.org.Client;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.util.ArrayList;

public class FxmlController {

    @FXML private TextField gameIdField;
    @FXML private ListView<HBoxButton> listOfLobbies;
    @FXML private Pane createLobbyPanel;
    @FXML private CheckBox privateLobbyCheckbox;
    @FXML private TextField maxPlayersInLobby;
    @FXML private Label numOfPlayersAlarm;

    Controller controller;
    View view;
    public void setVars(Controller controller, View view){
        this.controller = controller;
        this.view = view;
    }

    @FXML
    public void createNewLobbyButton() {
        createLobbyPanel.setVisible(true);
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

    public void refreshList(){
        ObservableList<HBoxButton> items = FXCollections.observableArrayList();
        ArrayList<String> arr = controller.askForLobbies();
        for (String string : arr){
            items.add(new HBoxButton(string, controller));
        }
        Platform.runLater(() -> listOfLobbies.setItems(items));
    }

    @FXML
    public void refreshListClick(){
        refreshList();
    }

    public void resetCreateLobbyPanel(){
        createLobbyPanel.setVisible(false);
        privateLobbyCheckbox.setSelected(false);
        maxPlayersInLobby.setText("10");
        numOfPlayersAlarm.setText("");
    }

    @FXML
    public void createLobbyPanelConfirm(){
        int maxPlayers = Integer.parseInt(maxPlayersInLobby.getText());
        if (2 <= maxPlayers && maxPlayers <= 99){
            controller.createNewLobby(privateLobbyCheckbox.isSelected(), maxPlayers);
        }
        else {
            numOfPlayersAlarm.setText("Enter correct number of players!");
        }
    }
    @FXML
    public void closeCreateLobbyPanel(){
        resetCreateLobbyPanel();
    }

}