package com.charades.client;

import com.charades.tools.ChatMessage;
import com.charades.tools.MyColor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.util.Pair;

public class GameSceneFXMLController {

    @FXML public Canvas canvas;
    @FXML public TextArea gameChat;
    @FXML public TextField enterMessage;
    @FXML public ListView<OwnHBox> leaderBoard;
    @FXML public Button returnToMenuButton;
    @FXML public Button startGameButton;
    @FXML public Label gameID;
    @FXML public ColorPicker colorPicker;
    @FXML public ToggleButton brush;
    @FXML public ToggleButton eraser;
    @FXML public ListView<String> waitingList;
    @FXML public Text gameTimer;
    @FXML public Text gameWord;

    @FXML public Label gameEndMessage;
    @FXML public Label hiddenWord;
    @FXML public Pane gameEndPanel;
    @FXML public Button waitingListLabel;
    @FXML public ToggleGroup toggleKek;


    public Controller controller;
    @FXML public Button clearAllButton;

    @FXML
    public void returnToMenuHandler(MouseEvent mouseEvent) {
        controller.returnToMenu("");
    }

    @FXML
    public void startGameHandler(MouseEvent mouseEvent) {
        controller.startGameButton();
    }

    @FXML
    public void colorPickerHandler(ActionEvent actionEvent) {
        System.out.println("handler received new color");
        controller.setBrushColor(new MyColor(colorPicker.getValue()));
        //controller.setColor(new com.charades.tools.MyColor(colorPicker.getValue()));
        //controller.setLineWidth(3);
    }

    @FXML
    public void enterMessageHandler(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            controller.sendChatMessage(new ChatMessage(enterMessage.getText() + "\n"));
            enterMessage.clear();
        }
    }

    @FXML
    public void brushHandler(MouseEvent actionEven){
        //controller.setIsBrush(true);
    }

    public static class OwnHBox extends HBox {
        Label name = new Label();
        Label score = new Label();
        OwnHBox(String playerName, Integer playerScore){
            super();
            this.setMaxWidth(180);
            name.setText(playerName);
            name.setAlignment(Pos.CENTER);
            name.setPrefWidth(120);
            name.setPadding(new Insets(0, 10, 0, 0));
            score.setText(playerScore.toString());
            score.setAlignment(Pos.CENTER);
            score.setPrefWidth(70);
            Separator separator = new Separator();
            separator.setOrientation(Orientation.VERTICAL);
            this.getChildren().addAll(name, separator, score);
        }
    }

    @FXML
    public void eraserHandler(MouseEvent actionEvent) {
        //controller.setIsBrush(false);
    }

    public void closeRoundEndPanel(ActionEvent actionEvent) {
        gameEndPanel.setVisible(false);
    }

    public void clearAllHandler(MouseEvent mouseEvent) {
        controller.clearAllButton();
    }

    public void setLeaderboard(ObservableList<Pair<String, Integer>> arr){
        ObservableList<OwnHBox> lst = FXCollections.observableArrayList();
        for (Pair<String, Integer> pair : arr){
            lst.add(new OwnHBox(pair.getKey(), pair.getValue()));
        }
        Platform.runLater(() -> leaderBoard.setItems(lst));
    }
}