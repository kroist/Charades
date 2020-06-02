package com.charades.client;

import com.charades.tools.*;
import com.charades.tools.Point;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.indexaccum.IAMax;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


public class Controller {
    private final View view;
    private final Model model;

    private MultiLayerNetwork net;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
    }

    public void setNet(String modelPath){
        try {
            net = KerasModelImport.importKerasSequentialModelAndWeights(modelPath);
        } catch (Exception e){
            System.out.println(e.getCause());
            e.printStackTrace();
        }
    }

    public void login(String username){
        if(!CheckUsername.check(username)){
            returnToLogin("Bad nickname");
            return;
        }
        int msg = model.connect(username);
        if(msg != 1){
            if(msg == -1) {
                returnToLogin("Busy nickname");
            }
            else{
                returnToLogin("Server offline");
            }
            return;
        }
        view.setMenuScene();
        askingThread = new AskingThread();
        askingThread.start();
    }

    public void clearCanvas() {
        view.clearCanvas();
    }

    public void clearCanvasSP() {
        view.clearCanvasSP();
    }

    public static class AskingThread extends Thread {
        @Override
        public void run(){
            boolean running = true;
            System.out.println("Starting to refresh lobbies");
            while(running){
                try {
                    View.fxmlController.refreshList();
                    Thread.sleep(2000);
                } catch (InterruptedException e){
                    System.out.println("Stopped refreshing lobbies");
                    running = false;
                }
            }
        }
    }
    AskingThread askingThread;


    public static WritableImage getWriteableImage(Canvas canvas){
        WritableImage writableImage = new WritableImage(256, 256);
        Platform.runLater(() -> {canvas.snapshot(null, writableImage);});
        //Image tmp = SwingFXUtils.fromFXImage(writableImage, null).getScaledInstance(28, 28, Image.SCALE_SMOOTH);
        BufferedImage tmp = SwingFXUtils.fromFXImage(writableImage, null);
        return SwingFXUtils.toFXImage(tmp, null);
        //return writableImage;
    }

    private static BufferedImage getScaledImage(Canvas canvas) {
        WritableImage writableImage = new WritableImage(256, 256);
        //Platform.runLater(() -> canvas.snapshot(null, writableImage));
        canvas.snapshot(null, writableImage);
        BufferedImage tmpp = SwingFXUtils.fromFXImage(writableImage, null);

        int minX = 255, maxX = 0;
        for (int i = 0; i < 256; i++){
            for (int j = 0; j < 256; j++){
                if (tmpp.getRGB(i, j) != -1){
                    minX = Math.min(minX, i);
                    minX = Math.min(minX, j);
                    maxX = Math.max(maxX, i);
                    maxX = Math.max(maxX, j);
                }
            }
        }

        if (minX > maxX){
            minX = 0;
            maxX = 255;
        }


        tmpp = tmpp.getSubimage(minX, minX, maxX-minX, maxX-minX);

        Image tmp = tmpp.getScaledInstance(28, 28, Image.SCALE_SMOOTH);

        BufferedImage scaledImg = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
        Graphics graphics = scaledImg.getGraphics();
        graphics.drawImage(tmp, 0, 0, null);
        graphics.dispose();

        LookupTable lookup = new LookupTable(0, 1){
            @Override
            public int[] lookupPixel(int[] src, int[] dest){
                dest[0] = (int)(255-src[0]);
                return dest;
            }
        };

        LookupOp op = new LookupOp(lookup, new RenderingHints(null));
        scaledImg = op.filter(scaledImg, null);

        return scaledImg;
    }


    public void makeGuess() {
        BufferedImage scaledImg = getScaledImage(View.getCanvasSP());
        View.singleplayerController.compressedImage.setImage(SwingFXUtils.toFXImage(scaledImg, null));

        NativeImageLoader loader = new NativeImageLoader(28, 28, 1, true);
        INDArray image;
        try {
            image = loader.asRowVector(scaledImg);
        } catch (IOException e){
            System.out.println("somtheing wrong in makeGuess()");
            return;
        }

        ImagePreProcessingScaler imageScaler = new ImagePreProcessingScaler();
        imageScaler.transform(image);
        image = image.reshape(1, 28, 28, 1);

        INDArray output = net.output(image);



        System.out.println(output.rank());
        long[] shp = output.shape();
        for (int i = 0; i < shp.length; i++)
            System.out.println(shp[i] + " ");
        System.out.println();

        int[] arr = net.predict(image);

        System.out.println(arr.length);


        int dig1 = Nd4j.getExecutioner().exec(new IAMax(output, 1)).getInt(0);
        double prob1 = output.getDouble(dig1)*100;
        output.putScalar(dig1, 0);

        int dig2 = Nd4j.getExecutioner().exec(new IAMax(output, 1)).getInt(0);
        double prob2 = output.getDouble(dig2)*100;
        output.putScalar(dig2, 0);


        int dig3 = Nd4j.getExecutioner().exec(new IAMax(output, 1)).getInt(0);
        double prob3 = output.getDouble(dig3)*100;
        output.putScalar(dig3, 0);


        int dig4 = Nd4j.getExecutioner().exec(new IAMax(output, 1)).getInt(0);
        double prob4 = output.getDouble(dig4)*100;
        output.putScalar(dig4, 0);

        int dig5 = Nd4j.getExecutioner().exec(new IAMax(output, 1)).getInt(0);
        double prob5 = output.getDouble(dig5)*100;
        output.putScalar(dig5, 0);


        View.singleplayerController.setNums(dig1, prob1, dig2, prob2, dig3, prob3, dig4, prob4, dig5, prob5);

    }



    public void createNewLobby(boolean isPrivate, int maxPlayers, String lobbyName, String difficulty){
        askingThread.interrupt();
        System.out.println(askingThread.isInterrupted());
        try {
            askingThread.join();
        } catch (Exception e){
            System.out.println(e);
        }
        // TODO: 14.05.2020
        //System.out.println(Thread.currentThread());
        /// maxPlayers * 2 + private
        String lobbyMessage = ((Integer)maxPlayers).toString() + ":" + ((Integer)(isPrivate ? 1 : 0)).toString() + ":" + lobbyName + ":" + difficulty;
        if (!model.sendObject(lobbyMessage)) {
            updateMenu("Cannot create new lobby");
            return;
        }
        Object o = model.getObject();
        System.out.println(o);
        if (!ConnectionMessage.CONNECTED_TO_LOBBY.equals(o)){
            updateMenu("Cannot connect to lobby");
            return;
        }
        o = model.getObject();
        if (o instanceof String){
            String ID = (String)o;
            System.out.println("Your lobby ID is: " + ID);
            view.setGameID(ID);
        }else {
            returnToMenu("cannot receive ID");
            return;
        }
        resetPlayer();
        model.setInLobby(true);
        view.setVisibleGameEndPanel(false);
        view.setLobbyScene();
        model.startReadingObjects();
    }
    public void connectToTheExistingLobby(String ID) {
        askingThread.interrupt();
        try {
            askingThread.join();
        } catch (Exception e){
            System.out.println(e);
        }

        if (!model.sendObject(ConnectionMessage.CONNECT_TO_LOBBY)){
            updateMenu("Cannot connect to server");
            return;
        }
        if (!model.sendObject(ID)){
            updateMenu("Cannot send ID");
            return;
        }
        Object o = model.getObject();
        System.out.println(o);
        System.out.println(o);
        if (ConnectionMessage.BAD_ID.equals(o)){
            updateMenu("You entered bad id");
            return;
        }
        if (ConnectionMessage.LOBBY_FULL.equals(o)){
            updateMenu("Lobby is full");
            return;
        }
        if (!ConnectionMessage.CONNECTED_TO_LOBBY.equals(o)){
            updateMenu("Cannot connect to lobby");
            return;
        }
        o = model.getObject();
        if (o instanceof String && ID.equals(o)){
            System.out.println("Your lobby ID is: " + ID);
            view.setGameID(ID);
        }else {
            returnToMenu("cannot receive ID");
            return;
        }
        resetPlayer();
        view.setGameID(ID);
        model.setInLobby(true);
        view.setVisibleGameEndPanel(false);
        view.setLobbyScene();
        model.startReadingObjects();
    }

    public void startSingleplayer(){
        view.setSingleplayerScene();
    }

    public void getReadyToWritePoints() {
        Canvas canvas = view.getCanvas();
        System.out.println(canvas);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                event -> {
                    //view.mousePressed(event);
                    System.out.println("hah");
                    model.sendObject(new Point(event.getX(), event.getY(), true));
                });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                event -> {
                    //view.mouseDragged(event);
                    if (model.isGameStarted()){
                        model.sendObject(new Point(event.getX(), event.getY(), false));
                    }
                });
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                event -> {
                    if (model.isGameStarted()){
                        model.sendObject(ConnectionMessage.MOUSE_RELEASED);
                    }
                });
    }

    public void getReadyToWritePointsSP() {
        Canvas canvas = View.getCanvasSP();
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        canvas.setOnMousePressed(e -> {
            ctx.setStroke(Color.BLACK);
            ctx.beginPath();
            ctx.moveTo(e.getX(), e.getY());
            ctx.stroke();
        });

        canvas.setOnMouseDragged(e -> {
            ctx.setStroke(Color.BLACK);
            ctx.lineTo(e.getX(), e.getY());
            ctx.stroke();
        });

        canvas.setOnMouseReleased(e -> {
            makeGuess();
        });

        canvas.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                makeGuess();
            }
        });

    }

    public void resetPlayer(){
        model.setIsDrawer(false);
        model.setGameStarted(false);
        view.setVisibleStartGameButton(false);
        view.setDefaultLineWidth();
        view.setDefaultPickerColor();
        view.getCanvas().setDisable(true);
        view.setColorPickerVisible(false);
        view.setEraserVisible(false);
        view.setClearAllButtonVisible(false);
        view.setBrushVisible(false);
        view.clearCanvas();
        view.setVisibleGameTimer(false);
        view.setEnterMessageVisible(true);
        view.setGameWordVisible(false);
        Platform.runLater(Sound::stopSound);
    }

    private void resetFromLobby(String message){
        System.out.println(message);
        model.stopReading();
        model.setInLobby(false);
        finishWritePoints();


        resetPlayer();

        view.clearChat();
        view.clearLeaderBoard();
        view.clearWhaitingList();

        //view.setMessageText(message);
    }

    public void returnToLogin(String message) {
        model.disconnect();
        view.setLoginScene();
        askingThread.interrupt();
        resetFromLobby(message + " returnToLogin");
        if (message != null){
            switch (message) {
                case "Busy nickname":
                    View.loginSceneFXMLController.nicknameTakenBox.setText("This username is already taken");
                    break;
                case "Bad nickname":
                    View.loginSceneFXMLController.nicknameTakenBox.setText(
                            "Username must have length from 1 to 16, consist of Latin letters, digits, spaces, characters '_' and '-'"
                    );
                    break;
                case "Server offline":
                    View.loginSceneFXMLController.nicknameTakenBox.setText("Server is offline");
                    break;
            }
        }
        else {
            View.loginSceneFXMLController.nicknameTakenBox.setText("");
        }
    }

    public void returnToLoginSP(){
        view.setLoginScene();
    }

    public void returnToMenu(String message) {
        // TODO: 17.05.2020 comments
        if (model.inLobby())model.sendObject(ConnectionMessage.RETURN_TO_MENU);
        model.setInLobby(false);

        view.setMenuScene();
        resetFromLobby(message + " returnToMenu");
        askingThread = new AskingThread();
        askingThread.start();
        view.clearGameIdField();
        view.setMessageText("");
    }
    public void updateMenu(String message){
        askingThread = new AskingThread();
        askingThread.start();

        System.out.println("Updated menu with: " + message);
        view.setMessageText(message);

        view.clearGameIdField();
    }

    private void finishWritePoints() {
        Canvas canvas = view.getCanvas();
        canvas.setDisable(true);
    }

    public void newPoint(Object obj) {
        view.newPoint(obj);
        Sound.startSound();

    }

    public void startGameButton() {
        model.sendObject(ConnectionMessage.START_GAME);
    }

    public void startGame() {
        view.setGameScene();
        model.setGameStarted(true);
        Sound.setSound("pencil_try.aiff");
        if (model.isDrawer()){
            //getReadyToWritePoints();
            view.getCanvas().setDisable(false);
            view.setVisibleStartGameButton(false);
            view.setColorPickerVisible(true);
            view.setEraserVisible(true);
            view.setClearAllButtonVisible(true);
            view.setBrushVisible(true);
            view.setEnterMessageVisible(false);
        }
        //view.setVisibleGameTimer(true);
    }
    public void setColor(MyColor color){
        model.sendObject(color);
    }
    public void newColor(Object obj){
        view.newColor(obj);
    }
    public void setLineWidth(Integer lineWidth){
        model.sendObject(lineWidth);
    }
    public void newLineWidth(Object obj){
        view.newLineWidth(obj);
    }
    public void sendChatMessage(ChatMessage msg){
        model.sendObject(msg);
    }
    public void newChatMessage(Object msg){
        view.newChatMessage(msg);
    }
    public void newTime(Object obj) {
        view.setNewTime(obj);
    }
    public void clearAllButton() {
        model.sendObject(ConnectionMessage.CLEAR_CANVAS);
    }

    public void newLeaderBoard(Object obj) {
        view.newLeaderBoard(obj);
    }

    public void setIsBrash(boolean b) {
        view.setIsBrash(b);
        if (b){
            setColor(view.getBrushColor());
            setLineWidth(3);
        }else {
            setColor(new MyColor(Color.web("#f4f4f4")));
            setLineWidth(25);
        }
    }

    public void setBrushColor(MyColor myColor) {
        view.setBrushColor(myColor);
        if (view.isBrash())setColor(view.getBrushColor());
    }

    public void newWaitingList(Object obj) {
        @SuppressWarnings("unchecked")
        ObservableList<String> arr = FXCollections.observableArrayList((HashSet<String>)obj);
        view.setWhaitingList(arr);
    }

    public void setDrawer() {
        /*view.getCanvas().setDisable(false);
        view.setColorPickerVisible(true);
        view.setEraserVisible(true);
        view.setBrushVisible(true);*/
        view.setVisibleStartGameButton(true);
    }
    public void returnToLobby() {
        resetPlayer();
        view.setLobbyScene();
        view.setVisibleGameTimer(false);
    }


    public synchronized ArrayList<String> askForLobbies(){
        model.sendObject(ConnectionMessage.LOBBY_LIST);
        Object o = model.getObject();
        if (o instanceof Integer) {
            int lobbiesNumber = (Integer) o;
            ArrayList<String> arr = new ArrayList<>();
            for (int i = 0; i < lobbiesNumber; i++) {
                Object ostr = model.getObject();
                if (ostr instanceof String) {
                    String str = (String) ostr;
                    arr.add(str);
                } else {
                    System.out.println("CANNOT GET LOBBY");
                    break;
                }
            }
            return arr;
        } else {
            System.out.println("CANNOT GET NUMBER OF LOBBIES");
            System.out.println(o);
            return new ArrayList<>();
        }
    }

    public void newWord(Object obj) {
        view.setNewWord(obj);
    }
    public void newGameResult(Object obj){
        view.newGameResult((GameResult)obj);
    }
}
