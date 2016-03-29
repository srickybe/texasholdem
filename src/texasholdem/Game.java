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
    PotCollection pots;
    ArrayList<Player> whoRaised;
    ArrayList<Integer> raises;
    ArrayList<Integer> highestBets;
    ArrayList<Pot> pot;

    Game() {
        this.players = new ArrayList<>();
        this.pots = new PotCollection();
        this.whoRaised = new ArrayList<>();
        this.raises = new ArrayList<>();
        this.highestBets = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            if (!players.add(player)) {
                throw new UnsupportedOperationException();
            }
        }
    }

    private void addPlayerToPot(Player player) {
        pots.addPlayer(player);
    }

    public void addToPlayersHand(Card... cards) {
        for (Card card : cards) {
            players.stream().forEach((player) -> {
                player.addToHand(card);
            });
        }
    }

    public void dealOneCardToPlayers(CardDeck cardDeck) {
        players.stream().forEach((player) -> {
            player.addToHand(cardDeck.pop());
        });
    }

    public int firstAfterButtonIndex() {
        for (int i = 1; i < players.size(); i = nextIndex(i)) {
            if (!players.get(i).folded() && players.get(i).canBet()) {
                return i;
            }
        }

        return -1;
    }

    private int getHighestBet() {
        return highestBets.get(highestBets.size() - 1);
    }

    private void setHighestBet(int bet) {
        highestBets.add(bet);
    }

    private void addToPot(int chips) {
        pots.addChips(chips);
    }

    private int getLastRaise() {
        return raises.get(raises.size() - 1);
    }

    public Player lastToHaveRaised() {
        if (!whoRaised.isEmpty()) {
            for (int i = whoRaised.size() - 1; i >= 0; --i) {
                if (whoRaised.get(i) != null) {
                    return whoRaised.get(i);
                }
            }
        }

        return null;
    }

    Player lastPlayerToRaise() {
        if (!whoRaised.isEmpty()) {
            return whoRaised.get(whoRaised.size() - 1);
        }

        return null;
    }

    public Player winner() {
        Player result = null;
        int count = 0;

        for (Player player : players) {
            if (!player.folded()) {
                result = player;
                ++count;

                if (count > 1) {
                    return null;
                }
            }
        }

        return result;
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

    private boolean allInBet() {
        for (int i = 0; i < players.size(); ++i) {
            Player player = players.get(i);
            Action action = player.lastAction();

            if (action != null && action.isAllIn()) {
                return true;
            }
        }

        return false;
    }

    public void hand() {

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

    private void resetPlayersBet() {
        for (Player player : players) {
            player.setCurrentBet(0);
        }
    }

    private void resetActivePlayersActions() {
        for (Player player : players) {
            if (!player.folded()) {
                player.resetActions();
            }
        }
    }

    void setLastPlayerToRaise(Player player) {
        whoRaised.add(player);
    }

    void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    void setSmallBlind() {
        paySmallBlind(players.get(smallBlindIndex()));
    }

    private int smallBlindIndex() {
        if (players.size() > 2) {
            return 1;
        } else {
            if (players.size() == 2) {
                return 0;
            }
        }

        return -1;
    }

    private void paySmallBlind(Player player) {
        ArrayList<Action> action = new ArrayList<>();
        action.add(Action.BET);
        Decision decision = player.act(action, 0, 1);
        update(decision, player);
    }

    void setBigBlind() {
        payBligBlind(players.get(bigBlindIndex()));
    }

    private int bigBlindIndex() {
        if (players.size() > 2) {
            return 2;
        } else {
            if (players.size() == 2) {
                return 1;
            }
        }

        return -1;
    }

    private void payBligBlind(Player player) {
        ArrayList<Action> action = new ArrayList<>();
        action.add(Action.BET);
        int smallBlind = raises.get(0);
        Decision decision = player.act(action, smallBlind, smallBlind * 2);
        update(decision, player);
    }

    int underTheGunIndex() {
        return nextIndex(bigBlindIndex());
    }

    void bettingRound(int index) {
        System.out.println("void bettingRound(" + index + ")");

        if (index < 0 || index >= players.size() || countActivePlayers() < 2) {
            return;
        }

        System.out.println("Starting with " + players.get(index).getName());

        for (int i = index; true; i = nextIndex(i)) {
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
            System.out.println(player.getName() + " acted!!!");
            System.out.println(player.getName() + " status:\n" + player);

            if (onePlayerLeft()
                    || raiseChecked(player)
                    || (everyPlayerChecked() && player.doubleChecked())) {
                break;
            }
        }

        System.out.println("######pot = " + pots.chips());
        System.out.println("allAllIn() = " + allAllIn());
        System.out.println("allInBet() = " + allInBet());

        if (allInBet()) {
            PotCollection shared = makePots();
            System.out.println("makePots() = " + shared);
            pots.popPot();
            pots.addPots(shared);
        }

        System.out.println("######pot = " + pots.chips());
        closeBettingRound();
        System.out.println("game=\n" + this);
    }

    public void initializePots() {
        pots.addPot(new Pot());

        for (Player player : players) {
            if (!player.folded() && player.canBet()) {
                pots.addPlayer(player);
            }
        }
    }

    private boolean allAllIn() {
        for (Player player : players) {
            if (player.lastAction() != null) {
                if (!player.folded()) {
                    if (!player.lastAction().isAllIn()) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }

        return true;
    }

    private void closeBettingRound() {
        resetHighestBet();
        resetPlayersBet();
        resetActivePlayersActions();
        removeRaises();
        setLastPlayerToRaise(null);
    }

    private void resetHighestBet() {
        highestBets.clear();
        highestBets.add(0);
    }

    private void removeRaises() {
        for (int i = 2; i < raises.size();) {
            raises.remove(i);
        }
    }

    private int nextIndex(int i) {
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
                updateAfterFold(player);
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

    private void updateAfterFold(Player player) {
        pots.removePlayer(player);
    }

    private PotCollection makePots() {
        PotCollection result = new PotCollection();

        while (true) {
            Pot pt = creatPot();

            if (1 == pt.numberOfPlayers()) {
                pt.giveBackChips();
                return result;
            } else {
                if (pt.noChipsLeft()) {
                    result.addPot(pt);
                    return result;
                } else {
                    result.addPot(pt);
                }
            }
        }
    }

    private Pot creatPot() {
        int minBet = minimumBet(players, 0);
        System.out.println("minBet = " + minBet);
        Pot result = new Pot();

        for (Player player : players) {
            if (player.getCurrentBet() < minBet) {
                result.addChips(player.getCurrentBet());
                player.setCurrentBet(0);
            } else {
                result.addChips(minBet);
                player.setCurrentBet(player.getCurrentBet() - minBet);
                result.addPlayer(player);
            }
        }

        System.out.println("createPot() = " + result);

        return result;
    }

    public int minimumBet(ArrayList<Player> players, int min) {
        int result = Integer.MAX_VALUE;

        for (Player player : players) {
            Action action = player.lastAction();

            if (action != null) {
                if (action.isCall() || action.isCheck() || action.isAllIn()) {
                    int bet = player.getCurrentBet();

                    if (bet > min && bet < result) {
                        result = bet;
                    }
                }
            }
        }

        return result;
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

    public int countActivePlayers() {
        int count = 0;

        for (Player player : players) {
            if (!player.folded() && player.canBet()) {
                ++count;
            }
        }

        return count;
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

    public void showdown() {
        Player lastToRaise = lastToHaveRaised();
        int index
                = lastToRaise != null
                        ? players.indexOf(lastToRaise)
                        : 0;
        System.out.println("lastToRaise = " + lastToRaise);
        System.out.println("index = " + index);
       
        ArrayList<Player> winners = new ArrayList<>();
        Player currentWinner = players.get(index);
        winners.add(currentWinner);
        System.out.println(currentWinner.getName()
                + ", SHOW YOUR HAND, PLEASE");
        Hand bestHand = currentWinner.bestHand();
        System.out.println("Best hand = " + bestHand.toString());

        for (int i = nextIndex(index); i < players.size(); i = nextIndex(i)) {
            if (i == index) {
                break;
            }

            Player player = players.get(i);

            if (!player.folded()) {
                System.out.println(player.getName() + ", WILL YOU SHOW YOUR HAND?");
                String decision = player.decideToShowHand();
                System.out.println("decision = " + decision);

                if ("Y".equals(decision)) {
                    Hand hand = player.bestHand();
                    int cmp = bestHand.compareTo(hand);
                    System.out.println("hand = " + hand.toString());
                    System.out.println("cmp = " + cmp);

                    if (cmp < 0) {
                        System.out.println(player.getName()
                                + " has up to now the best Hand");
                        bestHand = hand;
                        winners.clear();
                        winners.add(player);
                    } else if (cmp == 0) {
                        System.out.println(player.getName() + " has an equal hand");
                        winners.add(player);
                    }
                }
            }
        }

        System.out.println("The winner(s) is (are): " + winners.toString() + "\n");
        System.out.println("The best is:\n" + bestHand);
    }

    @Override
    public String toString() {
        String res = "Game{" + "\nplayers=";

        for (Player player : players) {
            res += "\n" + player;
        }

        res += ",\npots=" + pots;

        return res;

        /*return "Game{" + "players=" + players
         + ", \n\npots=" + pots
         //+ ", \n\nwhoRaised=" + whoRaised
         //+ ", \n\nraises=" + raises
         //+ ", \n\nhighestBets=" + highestBets
         /*+ ", \n\nsmallBlind=" + smallBlind
         + ", \n\nbigBlind=" + bigBlind + '}';*/
    }
}
