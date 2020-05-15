package main.java.org.Tools;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class WordGenerator {
    private static final Random random = new Random();
    private static final ArrayList<String> difficulties = new ArrayList<>();
    static {
        difficulties.add("easy");
        difficulties.add("medium");
        difficulties.add("hard");
        difficulties.add("persons");
        difficulties.add("verbs");
        difficulties.add("objects");
    }
    private static final HashMap<String, ArrayList<String>> words = new HashMap<>();
    static {
        for(String difficulty : difficulties){
            words.put(difficulty, loadWords(difficulty));
        }
    }
    private static ArrayList<String> loadWords(String difficulty){
        ArrayList<String> words = new ArrayList<>();
        try {
            Path path = Paths.get(WordGenerator.class.getResource("/word-lists/easy.txt").toURI());
            try(BufferedReader br = new BufferedReader(new FileReader(String.valueOf(path)))) {
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
}
