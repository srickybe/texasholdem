/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

/**
 *
 * @author ricky
 */
public enum Rank {

    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(11),
    QUEEN(12),
    KING(13),
    ACE(14);

    private final int cardinal;

    Rank(int value) {
        this.cardinal = value;
    }

    int getCardinal() {
        return cardinal;
    }

    public static Rank random() {
        return Rank.values()[(int) (Rank.values().length * Math.random())];
    }
}
