package com.charades.client;

import com.charades.tools.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.nd4j.common.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;


public class View extends Application {

    public static Controller controller;

    private FXMLLoader gameSceneLoader;
    private static GameSceneFXMLController gameSceneController;
    private FXMLLoader loginSceneLoader;
    static public LoginSceneFXMLController loginSceneFXMLController;

    private static Stage stage;

    private static Scene menuScene;
    private static Scene gameScene;
    private static Scene loginScene;
    private static Scene singleplayerScene;

    private static double prevX, prevY, x, y;
    private static MyColor color = new MyColor(Color.BLACK);
    private static MyColor brushColor = new MyColor(Color.BLACK);
    private static int lineWidth = 3;

    private static Canvas canvas;
    private static ColorPicker colorPicker;
    private static ToggleButton eraser;
    private static ToggleButton brush;
    private static Button clearAllButton;
    private static TextArea gameChat;
    private static TextField enterMessage;
    private static Text gameTimer;
    private static Text messageText;
    private static ListView<String > waitingList;

    private static Label gameID;
    private static Text gameWord;
    private static Button startGameButton;

    private static Label gameEndMessage;
    private static  Label hiddenWord;
    private static Pane gameEndPanel;

    private static boolean isBrush = true;

    static FXMLLoader mainMenuLoader;
    static FxmlController fxmlController;

    static FXMLLoader singleplayerLoader;
    static SingleplayerFXMLController singleplayerController;
    private static Canvas canvasSP;

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

        stage.setMaxWidth(800);
        stage.setMinWidth(800);
        stage.setMaxHeight(800);
        stage.setMinHeight(800);
        stage.setWidth(800);
        stage.setHeight(800);
        stage.setResizable(false);
        stage.setScene(loginScene);
        stage.setOnCloseRequest(windowEvent -> System.exit(0));
        stage.show();
    }
    private void createContent(){
        initLoginScene();
        initMenuScene();
        initGameScene();
        initSingleplayerScene();
    }
    private void initLoginScene(){
        loginSceneLoader = new FXMLLoader(getClass().getResource("/fxml/loginScene.fxml"));
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
        loginSceneFXMLController.nicknameField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                controller.login(loginSceneFXMLController.nicknameField.getCharacters().toString());
            }
        });
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
            mainMenuLoader = new FXMLLoader(getClass().getResource("/fxml/mainMenu.fxml"));
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
        gameSceneLoader = new FXMLLoader(getClass().getResource("/fxml/gameScene.fxml"));
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
        enterMessage.setMinWidth(600);
        enterMessage.setMaxWidth(600);
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

        //leaderBoard = gameSceneController.leaderBoard;
        waitingList = gameSceneController.waitingList;

        gameTimer.setVisible(false);

        gameEndMessage = gameSceneController.gameEndMessage;
        gameEndMessage.setWrapText(true);
        hiddenWord = gameSceneController.hiddenWord;
        gameEndPanel = gameSceneController.gameEndPanel;
        gameEndPanel.setVisible(false);

        gameID = gameSceneController.gameID;

        startGameButton = gameSceneController.startGameButton;

        eraser = gameSceneController.eraser;
        ImageView eraserIcon = new ImageView(new Image("1200px-Eraser_icon.svg.png"));
        eraserIcon.setFitHeight(50);
        eraserIcon.setFitWidth(50);
        eraser.setGraphic(eraserIcon);
        //eraser.setVisible(false);


        brush = gameSceneController.brush;
        ImageView brushIcon = new ImageView(new Image("brush.png"));
        brushIcon.setFitHeight(50);
        brushIcon.setFitWidth(50);
        brush.setGraphic(brushIcon);

        gameSceneController.toggleKek.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null){
                controller.setIsBrush((oldValue == brush));
                oldValue.setSelected(true);
            }
            else {
                controller.setIsBrush((newValue == brush));
            }
        }));

        clearAllButton = gameSceneController.clearAllButton;
        ImageView clearAllIcon = new ImageView(new Image("clearall-icon.png"));
        clearAllIcon.setFitHeight(50);
        clearAllIcon.setFitWidth(50);
        clearAllButton.setGraphic(clearAllIcon);

        stage.setTitle("Charades");
    }

    private void initSingleplayerScene(){
        singleplayerLoader = new FXMLLoader(getClass().getResource("/fxml/singleplayerScene.fxml"));
        Pane spPane;
        try {
            spPane = singleplayerLoader.load();
        } catch(Exception e){
            System.out.println("Something wrong with login scene");
            e.printStackTrace();
            return;
        }
        singleplayerController = singleplayerLoader.getController();
        if (singleplayerController != null)
            singleplayerController.controller = controller;
        singleplayerScene = new Scene(spPane);

        canvasSP = singleplayerController.microCanvas;
        initDrawSP(canvasSP.getGraphicsContext2D());
        controller.getReadyToWritePointsSP();
        try {
            controller.setNet(new ClassPathResource("model/keras.h5").getFile().getPath());
        } catch (Exception e){
            e.printStackTrace();
        }
        singleplayerController.loadWords();


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
        gc.setFill(Color.WHITE);
        gc.setStroke(color.getColor());
        gc.setLineWidth(lineWidth);
    }

    private void initDrawSP(GraphicsContext gc) {

        gc.setFill(Color.WHITE);
        gc.setLineWidth(8);

    }

    private static void drawPoint(double x, double y){

        System.out.println("draw com.charades.tools.Point");
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

    public void newPoint(Point p) {
        Sound.startSound();
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
    public void newColor(Object obj){
        MyColor cl = (MyColor)obj;
        Platform.runLater(() -> color = cl);
    }

    public void updateMenuScene(){
        ///TODO update list of lobbies
        ArrayList<String> lobbies = controller.askForLobbies();
    }
    public void setMenuScene() {
        Sound.stopSound();
        Platform.runLater(() -> {
            fxmlController.resetCreateLobbyPanel();
            fxmlController.lobbyMessage.setText("");
            stage.setScene(menuScene);
            stage.setMaxWidth(800);
            stage.setMinWidth(800);
            stage.setMaxHeight(800);
            stage.setMinHeight(800);
            stage.setWidth(800);
            stage.setHeight(800);
            stage.sizeToScene();
        });
    }
    public void setLoginScene() {
        Platform.runLater(()->{
            stage.setScene(loginScene);
            stage.setMaxWidth(800);
            stage.setMinWidth(800);
            stage.setMaxHeight(800);
            stage.setMinHeight(800);
            stage.setWidth(800);
            stage.setHeight(800);
            stage.sizeToScene();
        });
    }
    public void setGameScene() {
        Platform.runLater(() -> {
            stage.setScene(gameScene);
            stage.setMaxWidth(800);
            stage.setMinWidth(800);
            stage.setMaxHeight(800);
            stage.setMinHeight(800);
            stage.setWidth(800);
            stage.setHeight(800);
            stage.sizeToScene();
            gameChat.setMaxWidth(400);
            gameChat.setMinWidth(400);
            gameChat.prefWidth(400);
            gameChat.setMaxHeight(150);
            gameChat.setMinHeight(150);
            gameChat.prefHeight(150);
            gameChat.setLayoutX(200);
            gameChat.setLayoutY(0);
            gameChat.clear();
            gameChat.setFont(Font.font(15));

            enterMessage.setFont(Font.font(15));
            enterMessage.setMinWidth(400);
            enterMessage.setMaxWidth(400);
            enterMessage.setPrefWidth(400);

            enterMessage.setMinHeight(35);
            enterMessage.setMaxHeight(35);
            enterMessage.setPrefHeight(35);

            enterMessage.setLayoutX(200);
            enterMessage.setLayoutY(160);
            enterMessage.clear();

            gameEndPanel.setVisible(false);

            //gameTimer.setVisible(true);

            waitingList.setVisible(false);
            gameSceneController.waitingListLabel.setVisible(false);
            brush.setSelected(true);
            controller.setIsBrush(true);
        });


        // TODO: 14.05.2020
    }
    public void setLobbyScene() {
        Platform.runLater(() -> {
            stage.setScene(gameScene);
            stage.setMaxWidth(800);
            stage.setMinWidth(800);
            stage.setMaxHeight(800);
            stage.setMinHeight(800);
            stage.setWidth(800);
            stage.setHeight(800);
            stage.sizeToScene();
            gameChat.setMaxWidth(600);
            gameChat.setMinWidth(600);
            gameChat.setPrefWidth(600);
            gameChat.setMaxHeight(530);
            gameChat.setMinHeight(530);
            gameChat.setPrefHeight(530);
            gameChat.setLayoutX(0);
            gameChat.setLayoutY(200);
            gameChat.clear();
            gameChat.setFont(Font.font(20));

            enterMessage.setMaxWidth(600);
            enterMessage.setMinWidth(600);
            enterMessage.setPrefWidth(600);

            enterMessage.setMinHeight(40);
            enterMessage.setMaxHeight(40);
            enterMessage.setPrefHeight(40);

            enterMessage.setFont(Font.font(20));
            enterMessage.setLayoutX(0);
            enterMessage.setLayoutY(740);
            enterMessage.clear();

            //gameTimer.setVisible(false);

            waitingList.setVisible(true);
            gameSceneController.waitingListLabel.setVisible(true);
        });

        // TODO: 14.05.2020
    }

    public void setSingleplayerScene(){
        System.out.println("Setting singleplayer scene");
        Platform.runLater(() -> {
            stage.setScene(singleplayerScene);
            stage.setMaxWidth(800);
            stage.setMinWidth(800);
            stage.setMaxHeight(800);
            stage.setMinHeight(800);
            stage.setWidth(800);
            stage.setHeight(800);
            stage.sizeToScene();
            singleplayerController.startTimer();
            singleplayerController.guessCounter = 0;
            singleplayerController.counterLabel.setText("0");
            clearCanvasSP();
        });
    }

    public Canvas getCanvas() { return canvas; }

    public static Canvas getCanvasSP() { return canvasSP; }

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
        fxmlController.updateLobbyMessage(message);
    }

    public void clearCanvasGame() {
        Platform.runLater(() -> {
            canvas.getGraphicsContext2D().setFill(Color.WHITE);
            canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        });
        canvas.getGraphicsContext2D().beginPath();
    }

    public void clearCanvas(){
        Platform.runLater(() -> canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight()));
        canvas.getGraphicsContext2D().beginPath();
    }

    public void clearCanvasSP() {
        canvasSP.getGraphicsContext2D().setFill(Color.WHITE);
        canvasSP.getGraphicsContext2D().fillRect(0, 0, 256, 256);
        canvasSP.getGraphicsContext2D().beginPath();
        //Platform.runLater(() -> canvasSP.getGraphicsContext2D().clearRect(0, 0, canvasSP.getWidth(), canvasSP.getHeight()));
        //canvasSP.getGraphicsContext2D().beginPath();
    }

    public void setGameID(String s){
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
    public void setClearAllButtonVisible(boolean b){
        Platform.runLater(() -> clearAllButton.setVisible(b));
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
    public void newLineWidth(Integer lineWidth) {
        Platform.runLater(() -> View.lineWidth = lineWidth);
    }
    public void newChatMessage(Object obj) {
        ChatMessage msg = (ChatMessage)obj;
        Platform.runLater(() -> gameChat.appendText(msg.getText()));
    }

    public void clearChat() {
        Platform.runLater(() -> gameChat.clear());
    }

    private static void addLeaderBoard(ObservableList<Pair<String, Integer>> arr) {
        gameSceneController.setLeaderboard(arr);
    }
    public void newLeaderBoard(Object obj) {
        @SuppressWarnings("unchecked")
        ObservableList<Pair<String, Integer>> arr = FXCollections.observableArrayList((ArrayList<Pair<String, Integer>>)obj);
        Platform.runLater(() -> addLeaderBoard(arr));
    }
    public void clearLeaderBoard(){
        Platform.runLater(() -> gameSceneController.leaderBoard.getItems().clear());
    }

    public void setBrushVisible(boolean b) {
        Platform.runLater(() -> brush.setVisible(b));
    }

    public void setIsBrush(boolean b) {
        isBrush = b;
        if(b) {
            canvas.setCursor(new ImageCursor(new Image("BrushCursor.png")));
        }
        else {
            Circle circle = new Circle(16, null);
            circle.setFill(Color.YELLOW);
            circle.setStroke(Color.YELLOW);
            SnapshotParameters sp = new SnapshotParameters();
            sp.setFill(Color.TRANSPARENT);
            Image eraserCursor = circle.snapshot(sp, null);

            canvas.setCursor(new ImageCursor(eraserCursor, 16, 16));
        }
    }
    public boolean isBrush(){
        return isBrush;
    }

    public void setBrushColor(MyColor myColor) {
        brushColor = myColor;
    }

    public MyColor getBrushColor() {
        return brushColor;
    }

    public void setWaitingList(ObservableList<String> arr) {
        Platform.runLater(() -> waitingList.setItems(arr));
    }

    public void clearWaitingList() {
        Platform.runLater(() -> waitingList.getItems().clear());
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

    public void setVisibleGameEndPanel(boolean b){
        Platform.runLater(() -> gameEndPanel.setVisible(b));
    }

    public void clearGameIdField() {
        fxmlController.clearGameIdField();
    }

    public String getIpString(){
        return loginSceneFXMLController.ipField.getText();
    }

}
