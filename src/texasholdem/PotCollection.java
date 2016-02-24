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
public class PotCollection {

    private final ArrayList<Pot> pots;

    public PotCollection() {
        pots = new ArrayList<>();
    }

    public void addPot(Pot pot) {
        pots.add(pot);
    }

    public Pot lastPot() {
        return pots.get(pots.size() - 1);
    }

    public void removePlayer(Player player) {
        for (Pot pot : pots) {
            pot.removePlayer(player);
        }
    }

    @Override
    public String toString() {
        String res = "" + "Pots{" + "pots=";

        for (int i = 0; i < pots.size() - 1; ++i) {
            Pot pot = pots.get(i);
            res += "Pot{" + "value=" + pot.getChips() + ", players=[";
            int nPlayers = pot.numberOfPlayers();

            for (int j = 0; j < nPlayers - 1; ++j) {
                res += pot.getPlayer(j).getName() + ", ";
            }

            res += pot.getPlayer(nPlayers - 1).getName() + "]}, ";
        }

        Pot pot = pots.get(pots.size() - 1);
        res += "Pot{" + "value=" + pot.getChips() + ", players=[";
        int nPlayers = pot.numberOfPlayers();

        for (int j = 0; j < nPlayers - 1; ++j) {
            res += pot.getPlayer(j).getName() + ", ";
        }

        res += pot.getPlayer(nPlayers - 1).getName() + "]}}";

        return res;
    }

}
