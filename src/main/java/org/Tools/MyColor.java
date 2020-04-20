package main.java.org.Tools;

import javafx.scene.paint.Color;
import java.io.Serializable;

public class MyColor implements Serializable {
    private double R, G, B, Alpha;
    public MyColor(Color color){
        R = color.getRed();
        G = color.getGreen();
        B = color.getBlue();
        Alpha = color.getOpacity();
    }
    public Color getColor(){
        return new Color(R, G, B, Alpha);
    }
}
