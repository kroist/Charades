package com.charades.client;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
    public static Clip sound;
    public static void setSound(final String url) {
        try {
            if (sound != null)sound.stop();
            sound = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream("/main/resources/" + url));
            sound.open(inputStream);
            sound.loop(-1);
            //sound.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    public static void startSound(){
        if (sound != null){
            sound.loop(-1);
            if (!sound.isRunning())sound.start();
        }
    }
    public static void stopSound(){
        if (sound != null)sound.stop();
    }
}