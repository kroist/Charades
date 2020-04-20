package main.java.org.Tools;

import java.io.Serializable;

public class Point implements Serializable {
    private double x, y;
    private boolean single;
    public Point(double x, double y, boolean single){
        this.x = x;
        this.y = y;
        this.single = single;
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public boolean getSingle(){
        return single;
    }
}