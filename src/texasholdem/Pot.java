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

    private int chips;
    private final ArrayList<Player> players;

    public Pot(int value, ArrayList<Player> players) {
        this.chips = value;
        this.players = players;
    }

    public Pot() {
        this.chips = 0;
        this.players = new ArrayList<>();
    }

    public int getChips() {
        return chips;
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
        this.chips += chips;
    }

    void giveBackChips() {
        Player player = this.getPlayer(0);
        player.addToStack(this.getChips());
        player.setCurrentBet(0);
        System.out.println("give back chips to " + player.getName());
        System.out.println("player = " + player);
    }

    boolean noChipsLeft() {
        for (Player player : players) {
            if (player.getCurrentBet() > 0) {
                return false;
            }
        }

        return true;
    }

    public int numberOfPlayers() {
        return players.size();
    }

    @Override
    public String toString() {
        return "Pot{" + "value=" + chips + ", players=" + players + '}';
    }

}
