package com.charades.client;

public class Main{
    public static void main(String[] args) {
        //com.charades.client.Sound.playSound("kick.wav"); //UNCOMMENT TO HEAR REAL BASS
        //com.charades.client.Sound.setSound("dot.wav");
        //com.charades.client.Sound.startSound();
        View view = new View();
        Model model = new Model(args);
        Controller controller = new Controller(view, model);
        view.setController(controller);
        model.setController(controller);
        view.startLaunch();
    }
}