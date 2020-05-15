package main.java.org.Tools;

import java.io.Serializable;

public class GameTime implements Serializable {
    private final int time;

    public GameTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }
}
