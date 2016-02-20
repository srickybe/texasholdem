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
        this.pots = new ArrayList<>();
        this.whoRaised = new ArrayList<>();
        this.raises = new ArrayList<>();
        this.highestBets = new ArrayList<>();
    }

    public void add(Player player) {
        if (!players.contains(player)) {
            if (!players.add(player)) {
                throw new UnsupportedOperationException();
            }
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
            Action action = player.lastAction();

            if (action != null && !action.isCheck() && !action.isFold()) {
                return false;
            }
        }

        return true;
    }

    private boolean previousRaise() {
        for (Player player : players) {
            Action action = player.lastAction();

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
        int smallBlind = (int) (100 + 51 * Math.random());
        this.raises.add(smallBlind);
        System.out.println("smallblind = " + smallBlind);
        int bigBlind = 2 * smallBlind;
        this.raises.add(bigBlind);
        System.out.println("bigBlind = " + bigBlind);
        setLastPlayerToRaise(null);
        setHighestBet(0);
        boolean end = false;
        pots.add(new Pot(0, players));

        for (int i = 0; !end; i = nextInt(i)) {
            System.out.println("#####game = " + this);
            Player player = players.get(i);

            if (player.folded()) {
                continue;
            }

            ArrayList<Action> actions = possibleActions(player);

            if (actions.isEmpty()) {
                continue;
            }

            Decision decision = player.act(actions, getHighestBet(), maxRaise());
            update(decision, player);
            System.out.println("game = " + this);

            if (onePlayerLeft()
                    || raiseChecked(player)
                    || (everyPlayerChecked() && player.doubleChecked())) {
                System.out.println("onPlayerLeft() = " + onePlayerLeft());
                System.out.println("raiseChecked(player) = "
                        + raiseChecked(player));
                System.out.println("everyPlayerChecked() = "
                        + everyPlayerChecked());
                System.out.println("player.doubleChecked() = "
                        + player.doubleChecked());
                break;
            }
        }
    }

    private int nextInt(int i) {
        ++i;

        if (i >= players.size()) {
            i = 0;
        }

        return i;
    }

    private void update(Decision decision, Player player) {
        switch (decision.getAction()) {
            case BET:
                updateAfterRaise(decision, player);
                break;

            case CALL:
                updateAfterCall(decision);
                break;

            case CALL_ALL_IN:
                updateAfterCall(decision);
                break;

            case CHECK:
                break;

            case FOLD:
                break;

            case RAISE:
                updateAfterRaise(decision, player);
                break;

            case RAISE_ALL_IN:
                updateAfterRaise(decision, player);
                break;

            case RAISE_INFERIOR_TO_LAST_RAISE:
                updateAfterRaise(decision, player);
                break;

            default:
                break;
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
        int count = 0;

        for (Player player : players) {
            if (player.lastAction() == null) {
                return false;
            }

            if (!player.folded()) {
                ++count;

                if (count > 1) {
                    return false;
                }
            }
        }

        return count == 1;
    }

    private boolean everyPlayerChecked() {
        for (Player player : players) {
            Action action = player.lastAction();

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
        return player.equals(lastPlayerToRaise()) 
                && player.lastAction().isCheck();
    }

    private ArrayList<Action> possibleActions(Player player) {

        if (noBet()) {
            return actionsWhenNoBet();
        }

        if (previousRaise()) {
            if (player.equals(lastPlayerToRaise())) {
                return actionsForWhoRaised();
            } 

            return actionsAfterRaise(player);
        }

        return new ArrayList<>();
    }

    private ArrayList<Action> actionsAfterRaise(Player player) {
        ArrayList<Action> possibleActions = new ArrayList<>();

        if (player.canFullyRaise(getHighestBet(), maxRaise())) {
            possibleActions.add(Action.RAISE);
            possibleActions.add(Action.RAISE_ALL_IN);
        } else if (player.canRaise(getHighestBet(), maxRaise())) {
            possibleActions.add(Action.RAISE_INFERIOR_TO_LAST_RAISE);
        }

        if (player.canCall(getHighestBet())) {
            possibleActions.add(Action.CALL);
        } else {
            if (player.canBet()) {
                possibleActions.add(Action.CALL_ALL_IN);
            } else {
                return possibleActions;
            }
        }

        possibleActions.add(Action.FOLD);

        return possibleActions;
    }

    private ArrayList<Action> actionsForWhoRaised() {
        ArrayList<Action> possibleActions = new ArrayList<>();
        possibleActions.add(Action.CHECK);
        Player player = lastPlayerToRaise();

        if (countWhoCanBetExcept(player) > 0) {
            if (player.canFullyRaise(getHighestBet(), maxRaise())) {
                possibleActions.add(Action.RAISE);
                possibleActions.add(Action.RAISE_ALL_IN);
            } else if (player.canRaise(getHighestBet(), maxRaise())) {
                possibleActions.add(Action.RAISE_INFERIOR_TO_LAST_RAISE);
            }
        }

        return possibleActions;
    }

    private int countWhoCanBetExcept(Player player) {
        int count = 0;

        for (Player p : players) {
            if (p != player && p.canBet()) {
                ++count;
            }
        }

        return count;
    }

    private ArrayList<Action> actionsWhenNoBet() {
        ArrayList<Action> possibleActions = new ArrayList<>();
        possibleActions.add(Action.CHECK);
        possibleActions.add(Action.BET);
        possibleActions.add(Action.RAISE_ALL_IN);
        possibleActions.add(Action.FOLD);

        return possibleActions;
    }

    @Override
    public String toString() {
        return "Game{" + "players=" + players
                + ", pots=" + pots
                + ", highestBets=" + highestBets + '}';
    }

}
