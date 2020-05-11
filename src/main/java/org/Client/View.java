package main.java.org.Client;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import main.java.org.Tools.ChatMessage;
import main.java.org.Tools.MyColor;
import main.java.org.Tools.Point;

import java.util.ArrayList;


public class View extends Application {

    public static final int size = 300;

    public View(){
        //launch();
    }
    private static double prevX, prevY, x, y;
    private static MyColor color = new MyColor(Color.BLACK);
    private static Scene menuScene;
    private static Scene gameScene;
    private static Text messageText;
    public static Controller controller;
    private static Stage stage;
    private static Canvas canvas;
    private static TextArea chat;
    private static ListView<Pair<String, Integer>> leaderBoard;
    private static final int lineWidth = 3;
    public void setController(Controller c){
        controller = c;
    }
    public void startLaunch(){
        launch();
    }
    @Override
    public void start(Stage stage){
        View.stage = stage;
        createContent();

        stage.setMinHeight(800);
        stage.setMinWidth(800);
        stage.setResizable(false);
        stage.setScene(menuScene);
        stage.setOnCloseRequest(windowEvent -> System.exit(0));
        stage.show();
    }
    private void createContent(){
        initMenuScene();
        initGameScene();
    }
    private static Text gameID;
    private static Button startGameButton;
    private static ColorPicker colorPicker;
    private void initGameScene() {
        VBox game = new VBox();
        HBox tools = new HBox();
        gameID = new Text();

        Button returnToMenuButton = new Button("Return to menu");
        startGameButton = new Button("Start game");
        startGameButton.setOnMouseClicked(mouseEvent -> controller.startGameButton());
        returnToMenuButton.setOnMouseClicked(mouseEvent -> controller.returnToMenu("You asked me to return you to menu"));

        canvas = new Canvas(size, size);
        initDraw(canvas.getGraphicsContext2D());
        controller.getReadyToWritePoints();

        colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setStyle("-fx-color-label-visible: false;");
        colorPicker.setOnAction(ActionEvent -> controller.setColor(new MyColor(colorPicker.getValue())));
        colorPicker.setVisible(false);

        chat = new TextArea();
        chat.setEditable(false);
        chat.setWrapText(true);

        leaderBoard = new ListView<>();
        leaderBoard.setMinHeight(50);
        leaderBoard.setMaxHeight(50);
        leaderBoard.setMinWidth(50);
        leaderBoard.setMaxWidth(50);

        TextField enterMessage = new TextField();
        enterMessage.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){
                controller.sendChatMessage(new ChatMessage(enterMessage.getText() + "\n"));
                enterMessage.clear();
            }
        });

        tools.getChildren().addAll(returnToMenuButton, gameID, startGameButton, colorPicker, chat, enterMessage, leaderBoard);
        game.getChildren().addAll(tools, canvas);
        gameScene = new Scene(game, 800, 800);
        gameScene.getStylesheets().add("main/resources/fxml/chat.css");

        stage.setTitle("Charades");
    }

    private void initDraw(GraphicsContext gc) {
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        gc.setFill(Color.WHITE);

        gc.fill();
        gc.strokeRect(
                0,              //x of the upper left corner
                0,              //y of the upper left corner
                canvasWidth,    //width of the rectangle
                canvasHeight);  //height of the rectangle
        gc.setFill(Color.RED);
        gc.setStroke(color.getColor());
        gc.setLineWidth(lineWidth);
    }

    private void initMenuScene() {
        messageText = new Text();
        try {
            Pane mainMenu = FXMLLoader.load(getClass().getResource("/main/resources/fxml/mainMenu.fxml"));
            menuScene = new Scene(mainMenu);
        }
        catch (Exception e){
            System.out.println("SOMETHING WRONG WITH initMenuScene()");
        }

    }
    private static void drawPoint(double x, double y){
        //System.out.println("draw Point");
        x = x - lineWidth / 2.0;
        y = y - lineWidth / 2.0;
        System.out.println("COLOR IS " + color.getColor());
        canvas.getGraphicsContext2D().setFill(color.getColor());
        canvas.getGraphicsContext2D().fillOval(x, y, lineWidth, lineWidth);
    }
    private static void drawLine(double x, double y){
        canvas.getGraphicsContext2D().setLineWidth(lineWidth);
        canvas.getGraphicsContext2D().setLineCap(StrokeLineCap.ROUND);
        canvas.getGraphicsContext2D().setStroke(color.getColor());
        canvas.getGraphicsContext2D().strokeLine(prevX, prevY, x, y);
        prevX = x;
        prevY = y;
    }

    public void newPoint(Object obj) {
        Point p = (Point)obj;
        if (p.getSingle()) {
            prevX = p.getX();
            prevY = p.getY();
            Platform.runLater(() -> drawPoint(p.getX(), p.getY()));
        } else {
            x = p.getX();
            y = p.getY();
            Platform.runLater(() -> drawLine(x, y));
        }
    }
    private static void changeColor(MyColor cl){
        color = cl;
    }
    public void newColor(Object obj){
        MyColor cl = (MyColor)obj;
        Platform.runLater(() -> changeColor(cl));
    }

    public void setGameScene() {
        Platform.runLater(() -> stage.setScene(gameScene));
    }
    public void setMenuScene() {
        Platform.runLater(() -> stage.setScene(menuScene));
    }

    public Canvas getCanvas() { return canvas; }

    public void mousePressed(MouseEvent event) {
        prevX = event.getX();
        prevY = event.getY();
        drawPoint(prevX, prevY);
        canvas.getGraphicsContext2D().moveTo(prevX, prevY);
        canvas.getGraphicsContext2D().stroke();
    }

    public void mouseDragged(MouseEvent event) {
        x = event.getX();
        y = event.getY();
        canvas.getGraphicsContext2D().lineTo(x, y);
        canvas.getGraphicsContext2D().stroke();
    }

    public void setMessageText(String message) {
        messageText.setText(message);
    }

    public void clearCanvas() {
        Platform.runLater(() -> canvas.getGraphicsContext2D().clearRect(1, 2, canvas.getWidth() - 2, canvas.getHeight() - 3));
        canvas.getGraphicsContext2D().beginPath();
    }
    public void setGameID(String s){
            //System.out.println(s + "i am here");
            //if (gameID == null) System.out.println("tou loh");
        Platform.runLater(() -> gameID.setText("Your game ID is: " + s));
    }

    public void setVisibleStartGameButton(boolean b) {
        Platform.runLater(() -> startGameButton.setVisible(b));
    }

    public void setColorPickerVisible(boolean b) {
        Platform.runLater(() -> colorPicker.setVisible(b));
    }
    public void setDefaultPickerColor(){
        color = new MyColor(Color.BLACK);
        colorPicker.setValue(Color.BLACK);
    }
    private static void addMessage(ChatMessage msg){
        chat.appendText(msg.getText());
    }
    public void newChatMessage(Object obj) {
        ChatMessage msg = (ChatMessage)obj;
        Platform.runLater(() -> chat.appendText(msg.getText()));
    }

    public void clearChat() {
        Platform.runLater(() -> chat.clear());
    }

    private static void addLeaderBoard(ObservableList<Pair<String, Integer>> arr) {
        leaderBoard.setItems(arr);
    }
    public void newLeaderBoard(Object obj) {
        @SuppressWarnings("unchecked")
        ObservableList<Pair<String, Integer>> arr = FXCollections.observableArrayList((ArrayList<Pair<String, Integer>>)obj);
        Platform.runLater(() -> addLeaderBoard(arr));
    }
    public void clearLeaderBoard(){
        Platform.runLater(() -> leaderBoard.getItems().clear());
    }
}
