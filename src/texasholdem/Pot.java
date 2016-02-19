/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

import java.util.ArrayList;

/**
 *
 * @author ricky
 */
class Pot {

    private int value;
    private ArrayList<Player> players;

    public Pot(int value, ArrayList<Player> players) {
        this.value = value;
        this.players = players;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }
    
    public void addChips(int chips) {
        value += chips;
    }
}
