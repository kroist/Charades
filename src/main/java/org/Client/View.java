package main.java.org.Client;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import main.java.org.Tools.ChatMessage;
import main.java.org.Tools.MyColor;
import main.java.org.Tools.Point;

import java.io.IOException;
import java.util.ArrayList;


public class View extends Application {
    private static double prevX, prevY, x, y;
    private static MyColor color = new MyColor(Color.BLACK);
    private static MyColor brushColor = new MyColor(Color.BLACK);
    private static boolean isBrush = true;
    private static Scene menuScene;
    private static Scene gameScene;
    private static Scene loginScene;
    private static Text messageText;
    public static Controller controller;
    private static Stage stage;
    private static Canvas canvas;
    private static TextArea chat;
    private static ListView<Pair<String, Integer>> leaderBoard;
    private static int lineWidth = 3;
    private static Text gameID;
    private static Button startGameButton;
    private static ColorPicker colorPicker;
    private static Button eraser;
    private static Button brush;
    private FXMLLoader gameSceneLoader;
    private GameSceneFXMLController gameSceneController;
    private FXMLLoader loginSceneLoader;
    private LoginSceneFXMLController loginSceneFXMLController;

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
    }
    private void initMenuScene() {
        messageText = new Text();
        try {
            mainMenuLoader = new FXMLLoader(getClass().getResource("/main/resources/fxml/mainMenu.fxml"));
            Pane mainMenu = mainMenuLoader.load();
            fxmlController = mainMenuLoader.getController();
            menuScene = new Scene(mainMenu);
        }
        catch (Exception e){
            System.out.println("SOMETHING WRONG WITH initMenuScene()");
        }

    }
    private void initGameScene() {
        gameSceneLoader = new FXMLLoader(getClass().getResource("/main/resources/fxml/gameScene.fxml"));
        Pane gamePane;
        try {
            gamePane = gameSceneLoader.load();
        } catch (IOException e) {
            System.out.println(e);
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

        chat = gameSceneController.chat;

        leaderBoard = gameSceneController.leaderBoard;

        eraser = gameSceneController.eraser;

        gameID = gameSceneController.gameID;

        startGameButton = gameSceneController.startGameButton;
        ImageView eraserIcon = new ImageView(new Image("main/resources/1200px-Eraser_icon.svg.png"));
        eraserIcon.setFitHeight(50);
        eraserIcon.setFitWidth(50);
        eraser.setGraphic(eraserIcon);
        //eraser.setVisible(false);


        brush = gameSceneController.brush;
        ImageView brushIcon = new ImageView(new Image("brush.png"));
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
    FXMLLoader mainMenuLoader;
    FxmlController fxmlController;
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

    public void setGameScene() {
        Platform.runLater(() -> stage.setScene(gameScene));
    }
    public void setMenuScene() {
        Platform.runLater(() -> stage.setScene(menuScene));
    }
    public void setLoginScene() {
        Platform.runLater(()->stage.setScene(loginScene));
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
}