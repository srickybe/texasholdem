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
    ArrayList<Integer> highestBets;

    Game() {
        this.players = new ArrayList<>();
        this.players.add(new Player("Ricky"));
        this.players.add(new Player("Gery"));
        this.players.add(new Player("Audry"));
        this.players.add(new Player("Flora"));
        this.pots = new ArrayList<>();
        this.whoRaised = new ArrayList<>();
        this.raises = new ArrayList<>();
        this.highestBets = new ArrayList<>();
    }

    public void add(Player player) {
        if(!players.add(player)) {
            throw new UnsupportedOperationException();
        }
    }
    
    private int getHighestBet() {
        return highestBets.get(highestBets.size() - 1);
    }

    private void setHighestBet(int bet) {
        highestBets.add(bet);
    }

    private void addToPot(int chips) {
        pots.get(pots.size() - 1).addChips(chips);
    }

    private int getLastRaise() {
        return raises.get(raises.size() - 1);
    }

    private int getCurrentPot() {
        return pots.get(pots.size() - 1).getValue();
    }

    private int maxRaise() {
        int max = 0;

        for (Integer raise : raises) {
            if (raise > max) {
                max = raise;
            }
        }

        return max;
    }

    private void setLastRaise(int raise) {
        raises.add(raise);
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

    private boolean previousRaise() {
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
            return whoRaised.get(whoRaised.size() - 1);
        }

        return null;
    }

    void setLastPlayerToRaise(Player player) {
        whoRaised.add(player);
    }

    void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    void bettingRound() {
        int smallBlind = (int) (100 * Math.random());
        this.raises.add(smallBlind);
        System.out.println("smallblind = " + smallBlind);
        int bigBlind = 2 * smallBlind;
        this.raises.add(bigBlind);
        System.out.println("bigBlind = " + bigBlind);
        setLastPlayerToRaise(null);
        setHighestBet(0);
        boolean end = false;
        pots.add(new Pot(0, players));

        for (int i = 0; !end;) {
            Player player = players.get(i);
            System.out.println("player = " + player);
            System.out.println("Highest bet = " + getHighestBet());
            ArrayList<Action> actions = possibleActions(player);

            if (actions.isEmpty()) {
                break;
            }

            Decision decision = player.act(actions, getHighestBet(), maxRaise());
            System.out.println("decision = " + decision);

            switch (decision.getAction()) {
                case BET:
                    updateAfterRaise(decision, player);
                    break;

                case CALL:
                    updateAfterCall(decision);
                    break;

                case CALL_ALL_IN:
                    updateAfterCall(decision);
                    throw new UnsupportedOperationException();

                case CHECK:
                    end = raiseChecked(player)
                            || (everyPlayerChecked() && player.doubleChecked());
                    break;

                case FOLD:
                    players.remove(player);
                    --i;
                    end = onePlayerLeft();
                    break;

                case RAISE:
                    updateAfterRaise(decision, player);
                    break;

                case RAISE_ALL_IN:
                    updateAfterRaise(decision, player);
                    throw new UnsupportedOperationException();

                case RAISE_INFERIOR_TO_LAST_RAISE:
                    updateAfterRaise(decision, player);
                    break;

                default:
                    break;
            }

            System.out.println("player = " + player);
            System.out.println("pot = " + getCurrentPot());
            ++i;

            if (i >= players.size()) {
                i = 0;
            }
        }
    }

    private void updateAfterRaise(Decision decision, Player player) {
        setLastRaise(decision.getBet());
        addToPot(getLastRaise());
        setHighestBet(player.getCurrentBet());
        setLastPlayerToRaise(player);
    }
    
    private void updateAfterCall(Decision decision) {
        addToPot(decision.getBet());
    }

    private boolean onePlayerLeft() {
        return players.size() == 1;
    }

    private boolean everyPlayerChecked() {
        for (Player player : players) {
            Action action = player.latestAction();

            if (action == null) {
                return false;
            }

            if (!action.isCheck()) {
                return false;
            }
        }

        return true;
    }

    private boolean raiseChecked(Player player) {
        return player == lastPlayerToRaise()
                && player.latestAction().isCheck();
    }

    private ArrayList<Action> possibleActions(Player player) {
        
        if (noBet()) {
            return actionsWithoutBet();
        }

        if (previousRaise()) {
            if (player == lastPlayerToRaise()) {
                return actionsForWhoRaised(player);
            }
            
            return actionsAfterRaise(player);
        }

        return new ArrayList<>();
    }

    private ArrayList<Action> actionsAfterRaise(Player player) {
        ArrayList<Action> possibleActions = new ArrayList<>();
        if (player.canFullyRaise(getHighestBet(), getLastRaise())) {
            possibleActions.add(Action.RAISE);
            possibleActions.add(Action.RAISE_ALL_IN);
        } else if (player.canRaise(getHighestBet(), getLastRaise())) {
            possibleActions.add(Action.RAISE_INFERIOR_TO_LAST_RAISE);
        }
        
        if (player.canCall(getHighestBet())) {
            possibleActions.add(Action.CALL);
        } else {
            possibleActions.add(Action.CALL_ALL_IN);
        }
        
        possibleActions.add(Action.FOLD);
        
        return possibleActions;
    }

    private ArrayList<Action> actionsForWhoRaised(Player player) {
        ArrayList<Action> possibleActions = new ArrayList<>();
        possibleActions.add(Action.CHECK);
        
        if (player.canFullyRaise(getHighestBet(), getLastRaise())) {
            possibleActions.add(Action.RAISE);
            possibleActions.add(Action.RAISE_ALL_IN);
        } else if (player.canRaise(getHighestBet(), getLastRaise())) {
            possibleActions.add(Action.RAISE_INFERIOR_TO_LAST_RAISE);
        }
        
        return possibleActions;
    }

    private ArrayList<Action> actionsWithoutBet() {
        ArrayList<Action> possibleActions = new ArrayList<>();
        possibleActions.add(Action.CHECK);
        possibleActions.add(Action.BET);
        possibleActions.add(Action.RAISE_ALL_IN);
        possibleActions.add(Action.FOLD);
        
        return possibleActions;
    }
}
