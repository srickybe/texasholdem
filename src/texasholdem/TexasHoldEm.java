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
public class TexasHoldEm {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Game game = new Game();
        game.add(new Player("Ricky"));
        game.add(new Player("Gery"));
        game.add(new Player("Audry"));
        game.add(new Player("Flora"));
        game.preFlop();
    }
    
}
