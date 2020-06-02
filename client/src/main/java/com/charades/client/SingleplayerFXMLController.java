package com.charades.client;

import com.charades.tools.WordGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SingleplayerFXMLController {
    @FXML public Canvas microCanvas;
    @FXML public Label drawthing;
    @FXML public PieChart piechart;
    @FXML public ImageView compressedImage;

    public Controller controller;
    public ArrayList<String> words = new ArrayList<>();
    public Random randomGenerator;

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

    @FXML
    public void retToMenuSp(){
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

        piechart.setData(observableList);
        piechart.setLabelsVisible(false);

    }

    @FXML
    public void eraserButton(){
        controller.clearCanvasSP();
    }

}
