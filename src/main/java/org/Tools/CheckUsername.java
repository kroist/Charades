package main.java.org.Tools;

public class CheckUsername {
    public static boolean check(String username){
        if(username.isEmpty() || username.length() > 16){
            return false;
        }
        for(int i = 0; i < username.length(); i++){
            char c = username.charAt(i);
            if('0' <= c && c <= '9'){
                continue;
            }
            if('a' <= c && c <= 'z'){
                continue;
            }
            if('A' <= c && c <= 'Z'){
                continue;
            }
            if(c == '_' || c == '-' || c == ' '){
                continue;
            }
            return false;
        }
        return true;
    }
}
