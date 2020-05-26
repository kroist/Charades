package main.java.org.Client;


public class Main{
    public static void main(String[] args) {
        //Sound.playSound("kick.wav"); //UNCOMMENT TO HEAR REAL BASS
        //Sound.setSound("dot.wav");
        //Sound.startSound();
        View view = new View();
        Model model = new Model();
        Controller controller = new Controller(view, model);
        view.setController(controller);
        model.setController(controller);
        view.startLaunch();
    }
}