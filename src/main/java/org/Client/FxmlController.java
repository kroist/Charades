package main.java.org.Client;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
    @FXML private TextField nameOfLobby;
    @FXML private ChoiceBox<String> selectDifficulty;

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
        Label name = new Label();
        Label players = new Label();
        Label difficulty = new Label();
        Button button = new Button();
        HBoxButton(String text, Controller controller){
            ///TODO don't forget to ban sign ':' from the nickname!
            super();
            String[] split = text.split(":");
            name.setText(split[2]);
            name.setAlignment(Pos.CENTER);
            name.setMaxWidth(130);
            name.setPrefWidth(130);
            players.setPadding(new Insets(0, 0, 0, 35));
            players.setText(split[0]);
            difficulty.setPadding(new Insets(0, 0, 0, 50));
            difficulty.setText(split[1]);
            difficulty.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(difficulty, Priority.ALWAYS);
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
            this.getChildren().addAll(name, players, difficulty, button);
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
        nameOfLobby.setText("");
        selectDifficulty.getItems().setAll("easy", "medium", "hard", "objects", "verbs");
        selectDifficulty.setValue("easy");
    }

    @FXML
    public void createLobbyPanelConfirm(){
        int maxPlayers = Integer.parseInt(maxPlayersInLobby.getText());
        String lobbyName = nameOfLobby.getText();
        if (lobbyName.equals(""))
            lobbyName = "Lobby";
        if (2 <= maxPlayers && maxPlayers <= 99){
            System.out.println(selectDifficulty.getValue());
            controller.createNewLobby(privateLobbyCheckbox.isSelected(), maxPlayers, lobbyName, selectDifficulty.getValue());
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