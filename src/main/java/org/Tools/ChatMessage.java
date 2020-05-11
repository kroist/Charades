package main.java.org.Tools;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    private String text;
    public ChatMessage(String text){
        this.text = text;
    }
    public void setText(String text){
        this.text = text;
    }
    public String getText(){
        return text;
    }
}
