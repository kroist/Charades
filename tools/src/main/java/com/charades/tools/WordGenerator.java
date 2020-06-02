package com.charades.tools;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class WordGenerator {
    private static final Random random = new Random();
    private static final ArrayList<String> difficulties = new ArrayList<>();
    static {
        difficulties.add("easy");
        difficulties.add("medium");
        difficulties.add("hard");
        difficulties.add("verbs");
        difficulties.add("objects");
    }
    private static final HashMap<String, ArrayList<String>> words = new HashMap<>();
    static {
        for(String difficulty : difficulties){
            words.put(difficulty, loadWords(difficulty));
        }
    }
    private static final HashMap<String, Integer> times = new HashMap<>();
    static {
        times.put("easy", 90);
        times.put("medium", 120);
        times.put("hard", 150);
        times.put("verbs", 135);
        times.put("objects", 135);
    }
    private static ArrayList<String> loadWords(String difficulty){
        ArrayList<String> words = new ArrayList<>();
        try {
            InputStream in = WordGenerator.class.getResourceAsStream("/word-lists/" + difficulty + ".txt");
            //Path path = Paths.get(WordGenerator.class.getResource("/word-lists/" + difficulty + ".txt").toURI());
            try(BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                String word;
                while ((word = br.readLine()) != null) {
                    words.add(word);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return words;
        } catch(Exception e){
            System.out.println("File not found");
            e.printStackTrace();
            return null;
        }
    }
    public static String getRandomWord(String difficulty){
        return words.get(difficulty).get(random.nextInt(words.get(difficulty).size()));
    }

    public static int getTime(String difficulty) {
        return times.get(difficulty);
    }
}
