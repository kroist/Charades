package com.charades.client;

public class Main{
    public static void main(String[] args) {
        System.out.println("HELLO");
        View view = new View();
        Model model = new Model(args);
        Controller controller = new Controller(view, model);
        view.setController(controller);
        model.setController(controller);
        view.startLaunch();
    }
}