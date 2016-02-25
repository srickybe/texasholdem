/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author ricky
 */
public class TexasHoldEm {

    private static final String PROMPT_PLAYER_INPUT
            = "Type 1 TO INPUT A PLAYER"
            + "\nTYPE 0 TO END THE SEQUENCE: ";
    private final int MAX_NUMBER_OF_PLAYERS = 10;
    private final ArrayList<Player> players;
    private final CardDeck cardDeck;

    public TexasHoldEm() {
        this.players = new ArrayList<>();
        this.cardDeck = CardDeck.createFiftyTwoCardDeck();
    }

    private boolean addPlayer(Player player) {
        for (Player p : players) {
            if (p.getName().equals(player.getName())) {
                throw new UnsupportedOperationException();
            }
        }

        return players.add(player);
    }

    private Integer getIntegerInput() {
        Integer entry = null;

        while (entry == null) {
            Scanner input = new Scanner(System.in);

            try {
                entry = input.nextInt();
            } catch (Exception e) {
                entry = null;
            }
        }

        return entry;
    }

    private int inputPlayerStack() {
        System.out.print("INPUT THE PLAYER'S STACK: ");
        return this.getIntegerInput();
    }

    private String inputPlayerName() {
        String name = "";

        while ("".equals(name)) {
            System.out.print("INPUT THE PLAYER'S NAME: ");
            Scanner input = new Scanner(System.in);

            try {
                name = input.next();
            } catch (Exception e) {
                name = "";
            }
        }

        return name;
    }

    public void playHand() {
        Game game = new Game();

        for (Player player : players) {
            if (player.canBet()) {
                game.add(player);
            }
        }

        preFlop(game);
    }

    public void preFlop(Game game) {
        game.setSmallBlind();
        game.setBigBlind();
        shuffleCards();
        game.dealOneCardToPlayers(cardDeck);
        game.dealOneCardToPlayers(cardDeck);
        game.bettingRound(game.underTheGunIndex());
    }

    public void setPlayers() {
        System.out.println("INPUT PLAYERS");

        while (true) {
            int choice = -1;

            while (choice != 0 && choice != 1) {
                System.out.print(PROMPT_PLAYER_INPUT);
                choice = getIntegerInput();
            }

            if (choice == 1) {
                addPlayer(inputPlayer());

                if (players.size() == MAX_NUMBER_OF_PLAYERS) {
                    break;
                }
            } else {
                break;
            }
        }
    }

    private Player inputPlayer() {
        return new Player(inputPlayerName(), inputPlayerStack());
    }

    private void shuffleCards() {
        cardDeck.shuffle();
    }

    private static void TestTexasHoldEm() {
        TexasHoldEm the = new TexasHoldEm();
        Player Ricky = new Player("Ricky", 700);
        Player Gery = new Player("Gery", 300);
        Player Audry = new Player("Audry", 500);
        Player Flora = new Player("Flora", 350);
        
        the.addPlayer(Ricky);
        the.addPlayer(Gery);
        the.addPlayer(Audry);
        the.addPlayer(Flora);
        the.playHand();
    }

    public static void main(String[] args) {
        TestTexasHoldEm();
    }
}
