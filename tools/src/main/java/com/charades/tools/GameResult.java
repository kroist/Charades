package com.charades.tools;

import java.io.Serializable;

public class GameResult implements Serializable {
    private final String winnerNickname;
    private final String hiddenWord;

    public GameResult(String winnerNickname, String hiddenWord) {
        this.winnerNickname = winnerNickname;
        this.hiddenWord = hiddenWord;
    }

    public String getWinnerNickname() {
        return winnerNickname;
    }

    public String getHiddenWord() {
        return hiddenWord;
    }
}
