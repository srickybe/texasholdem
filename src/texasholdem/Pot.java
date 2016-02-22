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
    private final ArrayList<Player> players;

    public Pot(int value, ArrayList<Player> players) {
        this.value = value;
        this.players = players;
    }

    public Pot() {
        this.value = 0;
        this.players = new ArrayList<>();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Player getPlayer(int index) {
        return players.get(index);
    }

    public void addPlayer(Player player) {
        if (!this.players.add(player)) {
            throw new UnsupportedOperationException();
        }
    }

    public void addChips(int chips) {
        value += chips;
    }
    
    public int numberOfPlayers() {
        return players.size();
    }

    @Override
    public String toString() {
        return "Pot{" + "value=" + value + ", players=" + players + '}';
    }

    boolean function() {
        boolean end = true;
        
        for (Player player: players) {
            if (player.getCurrentBet() > 0) {
                end = false;
                break;
            }
        }
        
        return end;
    }

}
