package main.java.org.Client;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.java.org.Tools.Point;


public class View extends Application {
    public View(){
        //launch();
    }
    private static double prevX, prevY, x, y;
    private static Scene menuScene;
    private static Scene gameScene;
    private static Text messageText;
    private static Controller controller;
    private static Stage stage;
    private static Canvas canvas;
    private static final int lineWidth = 3;
    public void setController(Controller c){
        controller = c;
    }
    public void startLaunch(){
        launch();
    }
    @Override
    public void start(Stage stage) throws Exception {
        View.stage = stage;
        createContent();
        stage.setMinHeight(400);
        stage.setMinWidth(400);
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
    private void initGameScene() {
        VBox game = new VBox();
        HBox tools = new HBox();
        gameID = new Text();

        Button returnToMenuButton = new Button("Return to menu");
        startGameButton = new Button("Start game");
        startGameButton.setOnMouseClicked(mouseEvent -> controller.startGameButton());
        returnToMenuButton.setOnMouseClicked(mouseEvent -> controller.returnToMenu("You asked me to return you to menu"));

        canvas = new Canvas(400, 400);
        initDraw(canvas.getGraphicsContext2D());

        tools.getChildren().addAll(returnToMenuButton, gameID, startGameButton);


        game.getChildren().addAll(tools, canvas);
        gameScene = new Scene(game, 400, 400);
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
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(lineWidth);
    }

    private void initMenuScene() {
        BorderPane pane = new BorderPane();
        VBox menu = new VBox();
        Button createNewGame = new Button("Create new game");
        Button connectToTheExistingGame = new Button("Connect to the existing game");
        TextField textField = new TextField("Enter your game ID here");
        CheckBox privateGame = new CheckBox("Private game");
        pane.setCenter(menu);

        messageText = new Text();
        createNewGame.setOnMouseClicked(mouseEvent -> controller.createNewGame(privateGame.isSelected()));
        //System.out.println(textField.getCharacters().toString());
        connectToTheExistingGame.setOnMouseClicked(mouseEvent ->
                controller.connectToTheExistingGame(textField.getCharacters().toString()));

        menu.getChildren().addAll(textField, privateGame, createNewGame, connectToTheExistingGame, messageText);
        menuScene = new Scene(pane, 400, 400);
    }
    private static void drawPoint(double x, double y){
        //System.out.println("draw Point");
        x = x - lineWidth / 2.0;
        y = y - lineWidth / 2.0;
        canvas.getGraphicsContext2D().setFill(Color.BLACK);
        canvas.getGraphicsContext2D().fillOval(x, y, lineWidth, lineWidth);
    }
    private static void drawLine(double x, double y){
        canvas.getGraphicsContext2D().setLineWidth(lineWidth);
        canvas.getGraphicsContext2D().setLineCap(StrokeLineCap.ROUND);
        canvas.getGraphicsContext2D().strokeLine(prevX, prevY, x, y);
        prevX = x;
        prevY = y;
    }

    public void newPoint(Object obj) {
        Point p = (Point)obj;
        if (p.single) {
            prevX = p.x;
            prevY = p.y;
            Platform.runLater(() -> drawPoint(p.x, p.y));
        } else {
            x = p.x;
            y = p.y;
            Platform.runLater(() -> drawLine(x, y));
        }
    }

    public void setGameScene() {
        Platform.runLater(() -> stage.setScene(gameScene));
    }
    public void setMenuScene() {
        Platform.runLater(() -> stage.setScene(menuScene));
    }

    public Canvas getCanvas() {
        return canvas;
    }

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
}
