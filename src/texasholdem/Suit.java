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
public enum Suit {

    CLUBS(1),
    DIAMONDS(2),
    HEARTS(3),
    SPADES(4);

    private final int numeric;

    Suit(int value) {
        this.numeric = value;
    }

    public int getNumeric() {
        return numeric;
    }

    public static Suit random() {
        return Suit.values()[(int) (Suit.values().length * Math.random())];
    }
}

