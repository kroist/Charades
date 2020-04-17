package main.java.org.Client;

import java.io.Serializable;

public class Point implements Serializable {
    double x, y;
    boolean single;
    public Point(double x, double y, boolean single){
        this.x = x;
        this.y = y;
        this.single = single;
    }
}