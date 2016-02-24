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
public enum HandType {

    ROYAL_FLUSH("ROYAL FLUSH", 10),
    STRAIGHT_FLUSH("STRAIGHT FLUSH", 9),
    FOUR_OF_A_KIND("FOUR OF A KIND", 8),
    FULL_HOUSE("FULL HOUSE", 7),
    FLUSH("FLUSH", 6),
    STRAIGHT("STRAIGHT", 5),
    THREE_OF_A_KIND("THREE OF A KIND", 4),
    TWO_PAIRS("TWO PAIRS", 3),
    ONE_PAIR("ONE PAIR", 2),
    HIGH_CARD("HIGH CARD", 1);

    private final String name;
    private final int rank;

    HandType(String name, int rank) {
        this.name = name;
        this.rank = rank;
    }
    
    public int getRank(){
        return rank;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "HandType{" + "name=" + name + ", rank=" + rank + '}';
    }
}

