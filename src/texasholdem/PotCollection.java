/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

import java.util.Stack;

/**
 *
 * @author ricky
 */
public class PotCollection {

    private final Stack<Pot> pots;

    public PotCollection() {
        pots = new Stack<>();
    }

    public void addPot(Pot pot) {
        pots.add(pot);
    }

    public void addChips(int chips) {
        currentPot().addChips(chips);
    }

    public void addPlayer(Player player) {
        currentPot().addPlayer(player);
    }

    private Pot currentPot() {
        if (!pots.isEmpty()) {
            return pots.peek();
        } else {
            pots.add(new Pot());
            
            return pots.peek();
        }
    }

    public void removePlayer(Player player) {
        for (Pot pot : pots) {
            pot.removePlayer(player);
        }
    }

    @Override
    public String toString() {
        String res = "" + "Pots{" + "pots=[";

        for (int i = 0; i < pots.size() - 1; ++i) {
            Pot pot = pots.get(i);
            res += "Pot{" + "value=" + pot.getChips() + ", players=[";
            int nPlayers = pot.numberOfPlayers();

            for (int j = 0; j < nPlayers - 1; ++j) {
                res += pot.getPlayer(j).getName() + ", ";
            }

            if (nPlayers > 0) {
                res += pot.getPlayer(nPlayers - 1).getName();
            }

            res += "]}, ";
        }

        if (!pots.isEmpty()) {
            Pot pot = pots.get(pots.size() - 1);
            res += "Pot{" + "value=" + pot.getChips() + ", players=[";
            int nPlayers = pot.numberOfPlayers();

            for (int j = 0; j < nPlayers - 1; ++j) {
                res += pot.getPlayer(j).getName() + ", ";
            }

            if (nPlayers > 0) {
                res += pot.getPlayer(nPlayers - 1).getName();
            }
            
            res += "]}";
        }

        res += "]}";

        return res;
    }

}
