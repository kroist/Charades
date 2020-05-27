package com.charades.tools;

import java.io.Serializable;

public class GameWord implements Serializable {
    private final String word;
    public GameWord(String word){
        this.word = word;
    }
    public String getWord(){
        return word;
    }
}
