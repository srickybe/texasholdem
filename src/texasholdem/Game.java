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
public class Game {

    ArrayList<Player> players;
    ArrayList<Pot> pots;
    ArrayList<Player> whoRaised;
    ArrayList<Integer> raises;

    Game() {
        this.players = new ArrayList<>();
        this.players.add(new Player("Ricky"));
        this.players.add(new Player("Gery"));
        this.players.add(new Player("Audry"));
        this.players.add(new Player("Flora"));
        this.pots = new ArrayList<>();
        this.whoRaised = new ArrayList<>();
        this.raises = new ArrayList<>();
    }

    private boolean noBet() {
        for (int i = 0; i < players.size(); ++i) {
            Player player = players.get(i);
            Action action = player.latestAction();

            if (action != null && !action.isCheck() && !action.isFold()) {
                return false;
            }
        }

        return true;
    }

    private boolean everyoneCalled() {
        if (!everyOneActed()) {
            return false;
        }

        if (raiseCount() != 1) {
            return false;
        }

        for (Player player : players) {
            Action action = player.latestAction();

            if (!action.isCall() || !action.isCallAllIn() || !action.isFold()) {
                return false;
            }
        }

        return true;
    }

    private int raiseCount() {
        int result = 0;

        for (Player player : players) {
            Action action = player.latestAction();

            if (action.isRaise()
                    || action.isRaiseAllIn()
                    || action.isRaiseInferiorToLastRaise()
                    || action.isBet()) {
                ++result;
            }
        }

        return result;
    }

    private boolean everyOneActed() {
        for (Player player : players) {
            Action action = player.latestAction();
            
            if (action == null) {
                return false;
            }
        }

        return true;
    }

    private boolean previousRaiseOrBet() {
        for (Player player : players) {
            Action action = player.latestAction();

            if (action != null) {
                if (action.isBet()
                        || action.isRaise()
                        || action.isRaiseAllIn()
                        || action.isRaiseInferiorToLastRaise()) {
                    return true;
                }
            }
        }

        return false;
    }

    class ALLInException extends Exception {

        public ALLInException(String message) {
            super(message);
        }

    }

    Player lastPlayerToRaise() {
        if (!whoRaised.isEmpty()) {
            return whoRaised.get(whoRaised.size()-1);
        }
        
        return null;
    }
    
    void setLastPlayerToRaise(Player player) {
        whoRaised.add(player);
    }
    
    void setPlayers(ArrayList<Player> gamers) {
        this.players = gamers;
    }

    void bettingRound() {
        int smallBlind = (int) (100 * Math.random());
        System.out.println("smallblind = " + smallBlind);
        int bigBlind = 2 * smallBlind;
        System.out.println("bigBlind = " + bigBlind);
        setLastPlayerToRaise(null);
        int highestBet = 0;
        int lastRaise = bigBlind;
        boolean end = false;
        pots.add(new Pot(0, players));

        for (int i = 0; !end;) {
            Player player = players.get(i);
            System.err.println("gamer = " + player);
            System.out.println("Highest bet = " + highestBet);
            ArrayList<Action> actions
                    = possibleActions(player, highestBet, lastRaise);

            if (actions.isEmpty()) {
                break;
            }

            Decision decision = player.act(actions, highestBet, lastRaise);
            System.out.println("decision = " + decision);

            switch (decision.getAction()) {
                case BET:
                    lastRaise = decision.getBet();
                    pots.get(pots.size()-1).addChips(lastRaise);
                    highestBet = player.getCurrentBet();
                    setLastPlayerToRaise(player);
                    break;

                case CALL:
                    pots.get(pots.size()-1).addChips(lastRaise);
                    break;

                case CALL_ALL_IN:
                    pots.get(pots.size()-1).addChips(lastRaise);
                    throw new UnsupportedOperationException();
                //break;

                case CHECK:
                    end = raiseConfirmedBy(player) 
                            || (everyPlayerChecked() 
                            && player.hasDoubleChecked());
                    break;

                case FOLD:
                    players.remove(player);
                    --i;
                    end = onePlayerLeft();
                    break;

                case RAISE:
                    lastRaise = decision.getBet();
                    pots.get(pots.size()-1).addChips(lastRaise);
                    highestBet = player.getCurrentBet();
                    setLastPlayerToRaise(player);
                    break;

                case RAISE_ALL_IN:
                    lastRaise = decision.getBet();
                    pots.get(pots.size()-1).addChips(lastRaise);
                    highestBet = player.getCurrentBet();
                    setLastPlayerToRaise(player);
                    throw new UnsupportedOperationException();
                //break;

                case RAISE_INFERIOR_TO_LAST_RAISE:
                    pots.get(pots.size()-1).addChips(lastRaise);
                    highestBet = player.getCurrentBet();
                    setLastPlayerToRaise(player);
                    break;

                default:
                    break;
            }

            System.out.println("gamer = " + player);
            System.out.println("pot = " + pots);
            ++i;

            if (i >= players.size()) {
                i = 0;
            }
        }
    }

    private boolean onePlayerLeft() {
        return players.size() == 1;
    }

    private boolean everyPlayerChecked() {
        for(Player player: players) {
            if (!player.latestAction().isCheck()) {
                return false;
            }
        }
        
        return true;
    }

    private boolean raiseConfirmedBy(Player player) {
        return player == lastPlayerToRaise() 
                && player.latestAction().isCheck();
    }

    private ArrayList<Action> possibleActions(
            Player gamer,
            int highestBet,
            int lastRaise) {
        ArrayList<Action> possibleActions = new ArrayList<>();

        //bet if no one has bet yet
        //raise if you want to bet higher than a previous raiseFully or bet
        //check if no one has bet yet or you've raised and everyone called
        //call to match a previous bet or raiseFully
        if (noBet()) {
            possibleActions.add(Action.CHECK);
            possibleActions.add(Action.BET);
            possibleActions.add(Action.RAISE_ALL_IN);
            possibleActions.add(Action.FOLD);

            return possibleActions;
        }

        if (everyoneCalled()) {
            possibleActions.add(Action.CHECK);

            if (gamer.canFullyRaise(highestBet, lastRaise)) {
                possibleActions.add(Action.RAISE);
                possibleActions.add(Action.RAISE_ALL_IN);
            } else {
                if (gamer.canRaise(highestBet, lastRaise)) {
                    possibleActions.add(Action.RAISE_INFERIOR_TO_LAST_RAISE);
                }
            }

            return possibleActions;
        }

        if (previousRaiseOrBet() && gamer == lastPlayerToRaise()) {
            possibleActions.add(Action.CHECK);

            if (gamer.canFullyRaise(highestBet, lastRaise)) {
                possibleActions.add(Action.RAISE);
                possibleActions.add(Action.RAISE_ALL_IN);
            } else {
                if (gamer.canRaise(highestBet, lastRaise)) {
                    possibleActions.add(Action.RAISE_INFERIOR_TO_LAST_RAISE);
                }
            }

            return possibleActions;
        }

        if (previousRaiseOrBet()) {
            if (gamer.canFullyRaise(highestBet, lastRaise)) {
                possibleActions.add(Action.RAISE);
                possibleActions.add(Action.RAISE_ALL_IN);
            } else {
                if (gamer.canRaise(highestBet, lastRaise)) {
                    possibleActions.add(Action.RAISE_INFERIOR_TO_LAST_RAISE);
                }
            }

            if (gamer.canCall(highestBet)) {
                possibleActions.add(Action.CALL);

            } else {
                possibleActions.add(Action.CALL_ALL_IN);
            }

            possibleActions.add(Action.FOLD);

            return possibleActions;
        }

        /**
         * bet, all in, call, check, fold, raise bet : nobody has raised all in:
         * you can raise or you want to call a bet higher than your stack check:
         * nobody has bet yet
         */
        return possibleActions;
    }
}
