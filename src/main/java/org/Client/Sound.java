package main.java.org.Client;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
    public static Clip sound;
    public static void playSound(final String url) {
        try {
            sound = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream("/main/resources/" + url));
            sound.open(inputStream);
            sound.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    public static void stopSound(){
        sound.stop();
    }
}
