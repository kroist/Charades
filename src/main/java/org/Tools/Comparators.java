package main.java.org.Tools;

import javafx.util.Pair;

import java.util.Comparator;

public class Comparators {
    public static Comparator<Pair<String, Integer>> pairComparator = (t1, t2) -> {
        int val = Integer.compare(t1.getValue(), t2.getValue());
        if(val == 0) {
            return t1.getKey().compareTo(t2.getKey());
        }
        else {
            return val;
        }
    };
}
