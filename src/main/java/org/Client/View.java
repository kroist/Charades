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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import main.java.org.Tools.*;

import java.io.IOException;
import java.util.ArrayList;


public class View extends Application {

    public static Controller controller;

    private FXMLLoader gameSceneLoader;
    private GameSceneFXMLController gameSceneController;
    private FXMLLoader loginSceneLoader;
    static public LoginSceneFXMLController loginSceneFXMLController;

    private static Stage stage;

    private static Scene menuScene;
    private static Scene gameScene;
    private static Scene loginScene;

    private static double prevX, prevY, x, y;
    private static MyColor color = new MyColor(Color.BLACK);
    private static MyColor brushColor = new MyColor(Color.BLACK);
    private static int lineWidth = 3;

    private static Canvas canvas;
    private static ColorPicker colorPicker;
    private static Button eraser;
    private static Button brush;
    private static TextArea gameChat;
    private static TextField enterMessage;
    private static Text gameTimer;
    private static Text messageText;
    private static ListView<Pair<String, Integer>> leaderBoard;
    private static ListView<String > whaitingList;

    private static Text gameID;
    private static Text gameWord;
    private static Button startGameButton;

    private static Label gameEndMessage;
    private static  Label hiddenWord;
    private static Pane gameEndPanel;

    private static boolean isBrush = true;

    static FXMLLoader mainMenuLoader;
    static FxmlController fxmlController;

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
        stage.setScene(loginScene);
        stage.setOnCloseRequest(windowEvent -> System.exit(0));
        stage.show();
    }
    private void createContent(){
        initLoginScene();
        initMenuScene();
        initGameScene();
    }
    private void initLoginScene(){
        loginSceneLoader = new FXMLLoader(getClass().getResource("/main/resources/fxml/loginScene.fxml"));
        Pane loginPane;
        try {
            loginPane = loginSceneLoader.load();
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("SOMETHING WRONG WITH initLoginScene()");
            return;
        }
        loginSceneFXMLController = loginSceneLoader.getController();
        loginScene = new Scene(loginPane);
        loginSceneFXMLController.controller = controller;
        loginSceneFXMLController.nicknameField.setOnKeyTyped(event -> {
            String string = loginSceneFXMLController.nicknameField.getText();

            if (string.length() > 16) {
                loginSceneFXMLController.nicknameField.setText(string.substring(0, 16));
                loginSceneFXMLController.nicknameField.positionCaret(string.length());
            }
        });
    }
    private void initMenuScene() {
        messageText = new Text();
        try {
            mainMenuLoader = new FXMLLoader(getClass().getResource("/main/resources/fxml/mainMenu.fxml"));
            Pane mainMenu = mainMenuLoader.load();
            fxmlController = mainMenuLoader.getController();
            //if (fxmlController != null){
                System.out.println("FXMLCONTROLLER IS NOT NULL");
                fxmlController.setVars(controller, this);
            //}
            menuScene = new Scene(mainMenu);
        }
        catch (Exception e){
            System.out.println("SOMETHING WRONG WITH initMenuScene()");
            e.printStackTrace();
        }

    }
    private void initGameScene() {
        gameSceneLoader = new FXMLLoader(getClass().getResource("/main/resources/fxml/gameScene.fxml"));
        Pane gamePane;
        try {
            gamePane = gameSceneLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("SOMETHING WRONG WITH initGameScene()");
            return;
        }
        gameSceneController = gameSceneLoader.getController();
        gameSceneController.controller = controller;
        //System.out.println(gameSceneController);
        gameScene = new Scene(gamePane);
        canvas = gameSceneController.canvas;
        initDraw(canvas.getGraphicsContext2D());
        controller.getReadyToWritePoints();

        colorPicker = gameSceneController.colorPicker;
        colorPicker.setVisible(false);
        setDefaultPickerColor();
        setDefaultLineWidth();

        gameChat = gameSceneController.gameChat;
        gameChat.setEditable(false);
        enterMessage = gameSceneController.enterMessage;
        enterMessage.setEditable(true);
        enterMessage.setOnKeyTyped(event -> {
            String string = enterMessage.getText();

            if (string.length() > 70) {
                enterMessage.setText(string.substring(0, 70));
                enterMessage.positionCaret(string.length());
            }
        });

        gameTimer = gameSceneController.gameTimer;
        gameTimer.setVisible(false);

        gameWord = gameSceneController.gameWord;
        gameWord.setVisible(false);


        leaderBoard = gameSceneController.leaderBoard;
        whaitingList = gameSceneController.whaitingList;

        eraser = gameSceneController.eraser;
        gameTimer.setVisible(false);

        gameEndMessage = gameSceneController.gameEndMessage;
        gameEndMessage.setWrapText(true);
        hiddenWord = gameSceneController.hiddenWord;
        gameEndPanel = gameSceneController.gameEndPanel;
        gameEndPanel.setVisible(false);

        gameID = gameSceneController.gameID;

        startGameButton = gameSceneController.startGameButton;
        ImageView eraserIcon = new ImageView(new Image("main/resources/1200px-Eraser_icon.svg.png"));
        eraserIcon.setFitHeight(50);
        eraserIcon.setFitWidth(50);
        eraser.setGraphic(eraserIcon);
        //eraser.setVisible(false);


        brush = gameSceneController.brush;
        ImageView brushIcon = new ImageView(new Image("/main/resources/brush.png"));
        brushIcon.setFitHeight(50);
        brushIcon.setFitWidth(50);
        brush.setGraphic(brushIcon);

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
    private static void drawPoint(double x, double y){

        System.out.println("draw Point");
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
    /*private static void changeColor(MyColor cl){
        color = cl;
    }*/
    public void newColor(Object obj){
        MyColor cl = (MyColor)obj;
        Platform.runLater(() -> color = cl);
    }

    public void updateMenuScene(){
        ///TODO update list of lobbies
        ArrayList<String> lobbies = controller.askForLobbies();
    }
    public void setMenuScene() {
        //updateMenuScene();
        Platform.runLater(() -> {
            fxmlController.resetCreateLobbyPanel();
            stage.setScene(menuScene);
        });
    }
    public void setLoginScene() {
        Platform.runLater(()->stage.setScene(loginScene));
    }
    public void setGameScene() {
        Platform.runLater(() -> {
            stage.setScene(gameScene);
            gameChat.setMaxWidth(400);
            gameChat.setMinWidth(400);
            gameChat.prefWidth(400);
            gameChat.setMaxHeight(170);
            gameChat.setMinHeight(170);
            gameChat.prefHeight(170);
            gameChat.setLayoutX(200);
            gameChat.setLayoutY(0);
            gameChat.clear();

            enterMessage.maxWidth(400);
            enterMessage.minWidth(400);
            enterMessage.prefWidth(400);
            enterMessage.setLayoutX(200);
            enterMessage.setLayoutY(170);
            enterMessage.clear();

            gameEndPanel.setVisible(false);

            //gameTimer.setVisible(true);

            whaitingList.setVisible(false);
        });


        // TODO: 14.05.2020
    }
    public void setLobbyScene() {
        Platform.runLater(() -> {
            stage.setScene(gameScene);
            gameChat.setMaxWidth(600);
            gameChat.setMinWidth(600);
            gameChat.prefWidth(600);
            gameChat.setMaxHeight(570);
            gameChat.setMinHeight(570);
            gameChat.prefHeight(570);
            gameChat.setLayoutX(0);
            gameChat.setLayoutY(200);
            gameChat.clear();

            enterMessage.maxWidth(600);
            enterMessage.minWidth(600);
            enterMessage.prefWidth(600);
            enterMessage.setLayoutX(0);
            enterMessage.setLayoutY(770);
            enterMessage.clear();

            //gameTimer.setVisible(false);

            whaitingList.setVisible(true);
        });

        // TODO: 14.05.2020
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
        Platform.runLater(() -> canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight()));
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
    public void setEraserVisible(boolean b){
        Platform.runLater(() -> eraser.setVisible(b));
    }
    public void setDefaultPickerColor(){
        color = new MyColor(Color.BLACK);
        colorPicker.setValue(Color.BLACK);
        brushColor = color;
        isBrush = true;
    }
    public void setDefaultLineWidth(){
        lineWidth = 3;
    }
    private static void addMessage(ChatMessage msg){
        gameChat.appendText(msg.getText());
    }
    public void newChatMessage(Object obj) {
        ChatMessage msg = (ChatMessage)obj;
        Platform.runLater(() -> gameChat.appendText(msg.getText()));
    }

    public void clearChat() {
        Platform.runLater(() -> gameChat.clear());
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

    public void changeLineWidth(Integer lineWidth){
        View.lineWidth = lineWidth;
    }
    public void newLineWidth(Object obj) {
        Integer lineWidth = (Integer)obj;
        Platform.runLater(() -> View.lineWidth = lineWidth);
    }

    public void setBrushVisible(boolean b) {
        Platform.runLater(() -> brush.setVisible(b));
    }

    public void setIsBrash(boolean b) {
        isBrush = b;
    }
    public boolean isBrash(){
        return isBrush;
    }

    public void setBrushColor(MyColor myColor) {
        brushColor = myColor;
    }

    public MyColor getBrushColor() {
        return brushColor;
    }

    public void setWhaitingList(ObservableList<String> arr) {
        Platform.runLater(() -> whaitingList.setItems(arr));
    }
    public void clearWhaitingList() {
        Platform.runLater(() -> whaitingList.getItems().clear());
    }

    public void setNewTime(Object obj) {
        int time = ((GameTime)obj).getTime();
        Platform.runLater(() -> {
            gameTimer.setText(String.valueOf(time));
            gameTimer.setVisible(true);
        });
    }

    public void setVisibleGameTimer(boolean b) {
        System.out.println("set visible game timer " + b);
        Platform.runLater(() -> gameTimer.setVisible(b));
    }

    public void setEnterMessageVisible(boolean b) {
        enterMessage.setVisible(b);
    }

    public void setGameWordVisible(boolean b){
        gameWord.setVisible(b);
    }

    public void setNewWord(Object obj) {
        String msg = ((GameWord)obj).getWord();
        Platform.runLater(() -> {
            gameWord.setText(msg);
            gameWord.setVisible(true);
        });
    }

    public void newGameResult(GameResult result) {
        String msg;
        if (result.getWinnerNickname() == null){
            msg = "The round is over";
        }else {
            msg = "The winner is: " + result.getWinnerNickname();
        }
        Platform.runLater(() -> {
            gameEndMessage.setText(msg);
            hiddenWord.setText("Hidden word: " + result.getHiddenWord());
            gameEndPanel.setVisible(true);
        });
    }
}