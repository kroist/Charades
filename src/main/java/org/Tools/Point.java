package main.java.org.Tools;

import java.io.Serializable;

public class Point implements Serializable {
    public double x, y;
    public boolean single;
    public Point(double x, double y, boolean single){
        this.x = x;
        this.y = y;
        this.single = single;
    }
}