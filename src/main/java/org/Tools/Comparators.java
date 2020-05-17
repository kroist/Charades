package main.java.org.Tools;


import java.util.Comparator;

public class Comparators {
    /*public static Comparator<Pair<String, Integer>> pairComparator = (t1, t2) -> {
        int val = Integer.compare(t1.getValue(), t2.getValue());
        if(val == 0) {
            return t1.getKey().compareTo(t2.getKey());
        }
        else {
            return val;
        }
    };*/
    public static Comparator<String> pairComparator = (t1, t2) -> {
        String[] s1 = t1.split(":");
        String[] s2 = t2.split(":");
        int val = Integer.compare(Integer.parseInt(s1[1]), Integer.parseInt(s2[1]));
        if (val == 0){
            return s1[0].compareTo(s2[0]);
        }
        else {
            return val;
        }
    };
}
