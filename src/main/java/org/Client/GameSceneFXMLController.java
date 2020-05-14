package main.java.org.Client;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Pair;
import main.java.org.Tools.ChatMessage;
import main.java.org.Tools.MyColor;

public class GameSceneFXMLController {

    @FXML public Canvas canvas;
    @FXML public TextArea gameChat;
    @FXML public TextField enterMessage;
    @FXML public ListView<Pair<String, Integer>> leaderBoard;
    @FXML public Button returnToMenuButton;
    @FXML public Button startGameButton;
    @FXML public Text gameID;
    @FXML public ColorPicker colorPicker;
    @FXML public Button brush;
    @FXML public Button eraser;
    public Controller controller;
    @FXML public ListView<String> whaitingList;

    @FXML
    public void returnToMenuHandler(MouseEvent mouseEvent) {
        controller.returnToMenu("You asked me to return you to menu");
    }

    @FXML
    public void startGameHandler(MouseEvent mouseEvent) {
        controller.startGameButton();
    }

    @FXML
    public void colorPickerHandler(ActionEvent actionEvent) {
        System.out.println("handler received new color");
        controller.setBrushColor(new MyColor(colorPicker.getValue()));
        //controller.setColor(new MyColor(colorPicker.getValue()));
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
        controller.setIsBrash(true);
        //controller.();
    }

    @FXML
    public void eraserHandler(MouseEvent actionEvent) {
        controller.setIsBrash(false);
        //controller.setColor(new MyColor(Color.web("#f4f4f4")));
        //controller.setLineWidth(10);
    }

}