package com.charades.client;

import com.charades.tools.GameTime;
import com.charades.tools.WordGenerator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SingleplayerFXMLController {
    @FXML public Canvas microCanvas;
    @FXML public Label drawthing;
    @FXML public PieChart piechart;
    @FXML public ImageView compressedImage;
    @FXML public Label timerLabel;
    @FXML public Label counterLabel;

    public Controller controller;
    public ArrayList<String> words = new ArrayList<>();
    public Random randomGenerator = new Random();
    public Timer timer;
    public int guessCounter = 0;

    public String getRandomWord(){
        return words.get(randomGenerator.nextInt(words.size()));
    }

    public void loadWords(){
        try {
            InputStream in = getClass().getResourceAsStream("/model/class_names.txt");
            try(BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                String word;
                while ((word = br.readLine()) != null) {
                    words.add(word);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch(Exception e){
            System.out.println("File not found");
            e.printStackTrace();
        }
    }


    public void startTimer() {
        if (timer != null)timer.cancel();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            private int counter = 0;
            @Override
            public void run() {
                System.out.println("counter = " + counter);
                if (counter > 0){
                    Platform.runLater(() ->
                    timerLabel.setText(Integer.toString(counter)));
                    --counter;
                }
                else {
                    ///HERE SHOULD CHANGE THE WORD
                    Platform.runLater(() ->
                    drawthing.setText(getRandomWord().replace('_', ' ')));
                    counter = 30;
                }
            }
        }, 0,1000);
    }

    @FXML
    public void retToMenuSp(){
        timer.cancel();
        controller.returnToLoginSP();
    }

    public void setNums(int g1, double p1, int g2, double p2, int g3, double p3, int g4, double p4, int g5, double p5){

        ObservableList<PieChart.Data> observableList = FXCollections.observableArrayList(
                new PieChart.Data(words.get(g1), p1),
                new PieChart.Data(words.get(g2), p2),
                new PieChart.Data(words.get(g3), p3),
                new PieChart.Data(words.get(g4), p4),
                new PieChart.Data(words.get(g5), p5)
        );

        System.out.println(p1 + " " + p2 + " " + p3 + " " + p4 + " " + p5);

        double sum = (p1+p2+p3+p4+p5);

        if (words.get(g1).replace('_', ' ').equals(drawthing.getText()) && p1 >= sum/3){
            System.out.println("YOU WON");
            timer.cancel();
            startTimer();
            controller.clearCanvasSP();
            ++guessCounter;
            counterLabel.setText(Integer.toString(guessCounter));
        }
        if (words.get(g2).replace('_', ' ').equals(drawthing.getText()) && p2 >= sum/3){
            System.out.println("YOU WON");
            timer.cancel();
            startTimer();
            controller.clearCanvasSP();
            ++guessCounter;
            counterLabel.setText(Integer.toString(guessCounter));
        }

        piechart.setData(observableList);
        for (Node node : piechart.lookupAll(".chart-legend-item")){
            if (node instanceof Label){
                ((Label)node).setText(((Label)node).getText().replace('_', ' '));
                ((Label)node).setPrefWidth(250);
                ((Label)node).setAlignment(Pos.CENTER);
            }
        }
        piechart.setLabelsVisible(false);

    }

    @FXML
    public void eraserButton(){
        controller.clearCanvasSP();
    }

}
