/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

/**
 *
 * @author ricky
 */
public class Player {

    private final String name;
    private int stack;
    private Hand hole;
    private Hand hand;
    private int currentBet;
    private final ArrayList<Action> actions;

    Player(String name) {
        this.name = name;
        this.stack = (int) (250 + 751 * Math.random());
        this.currentBet = 0;
        this.actions = new ArrayList<>();
    }

    Player(String name, int stack) {
        this.name = name;
        this.stack = stack;
        this.hole = new Hand();
        this.hand = new Hand();
        this.currentBet = 0;
        this.actions = new ArrayList<>();
    }

    public int getStack() {
        return stack;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public Action lastAction() {
        if (actions.isEmpty()) {
            return null;
        }

        return actions.get(actions.size() - 1);
    }

    void setCurrentBet(int bet) {
        currentBet = bet;
    }

    public void setLatestAction(Action action) {
        if (!actions.add(action)) {
            throw new UnsupportedOperationException();
        }
    }

    Decision act(ArrayList<Action> actions, int highestBet, int bigBlind) {
        Action action = choseAction(actions);
        setLatestAction(action);

        switch (action) {
            case BET:
                return new Decision(action, bet(bigBlind));

            case RAISE:
                return new Decision(action, raiseFully(highestBet, bigBlind));

            case RAISE_ALL_IN:
                return new Decision(action, raiseAllIn(highestBet));

            case RAISE_INFERIOR_TO_LAST_RAISE:
                return new Decision(
                        action,
                        raiseInferiorToLastRaise(highestBet, bigBlind));

            case CALL:
                return new Decision(action, call(highestBet));

            case CALL_ALL_IN:
                return new Decision(action, allIn());

            default:
                return new Decision(action, 0);
        }
    }

    public void addToHand(Card card) {
        hand.addCard(card);
        
        if (hand.size() < 2) {
            hole.addCard(card);
        }
    }

    public void addToStack(int chips) {
        stack += chips;
    }

    int allIn() {
        int chipsToAddToPot1 = getStack();
        substract(chipsToAddToPot1);
        setCurrentBet(getCurrentBet() + chipsToAddToPot1);

        return chipsToAddToPot1;
    }

    int bet(int bigBlind) {
        Integer chipsToAddToPot1 = 0;

        while (chipsToAddToPot1 < bigBlind || chipsToAddToPot1 >= getStack()) {
            System.out.println("Bet any amount between " + bigBlind + " and "
                    + getStack() + " excluded");
            chipsToAddToPot1 = getIntegerInput();
        }

        substract(chipsToAddToPot1);
        setCurrentBet(chipsToAddToPot1);

        return chipsToAddToPot1;
    }

    int betBigBlind(int smallBlind) {
        return smallBlind * 2;
    }

    int betSmallBlind() {
        return (int) (50 * Math.random());
    }

    int call(int highestBet) {
        int chipsToAddToPot1 = highestBet - getCurrentBet();
        substract(chipsToAddToPot1);
        setCurrentBet(highestBet);

        return chipsToAddToPot1;
    }

    int callAllIn(int highestBet) {
        if (!canCall(highestBet)) {
            int chipsToAddToPot1 = getStack();
            substract(chipsToAddToPot1);
            setCurrentBet(getCurrentBet() + chipsToAddToPot1);

            return chipsToAddToPot1;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    boolean canBet() {
        return getStack() > 0;
    }

    boolean canCall(int highestBet) {
        return getCurrentBet() + getStack() > highestBet;
    }

    boolean canFullyRaise(int highestBet, int lastRaise) {
        return highestBet + 2 * lastRaise < getCurrentBet() + getStack();
    }

    boolean canRaise(int highestBet, int lastRaise) {
        if (!canFullyRaise(highestBet, lastRaise)) {
            return getCurrentBet() + getStack() > highestBet;
        }

        throw new UnsupportedOperationException();
    }

    Action choseAction(ArrayList<Action> actions) {
        Integer choice = null;

        while (choice == null || choice < 0 || choice >= actions.size()) {
            try {
                promptPossibleActions(actions);
                Scanner input = new Scanner(System.in);
                choice = input.nextInt();
            } catch (Exception e) {
                choice = null;
            }
        }

        return actions.get(choice);
    }

    public boolean folded() {
        return this.lastAction() == null ? false : this.lastAction().isFold();
    }

    int getIntegerInput() {
        Integer amount = null;

        while (amount == null) {
            try {
                Scanner input = new Scanner(System.in);
                amount = input.nextInt();
            } catch (Exception e) {
                amount = null;
            }
        }

        return amount;
    }

    public boolean isAllIn() {
        return getStack() == 0;
    }

    public boolean doubleChecked() {
        int actionCount = actions.size();

        if (actionCount >= 2) {
            return actions.get(actionCount - 1).isCheck()
                    && actions.get(actionCount - 2).isCheck();
        }

        return false;
    }

    int raiseAllIn(int highestBet) {
        int bet = getCurrentBet() + getStack();
        int chipsToAddToPot1 = getStack();
        substract(chipsToAddToPot1);
        setCurrentBet(bet);

        return chipsToAddToPot1;
    }

    int raiseFully(int highestBet, int lastRaise) {

        int bet = -1;

        while (bet < highestBet + 2 * lastRaise || bet > getStack()) {
            System.out.println("Bet higher than "
                    + (highestBet + 2 * lastRaise));
            bet = getIntegerInput();
        }

        int chipsToAddToPot1 = bet - getCurrentBet();
        substract(chipsToAddToPot1);
        setCurrentBet(bet);

        return chipsToAddToPot1;
    }

    int raiseInferiorToLastRaise(int highestBet, int lastRaise) {
        int bet = getCurrentBet() + getStack();
        int chipsToAddToPot1 = getStack();
        substract(chipsToAddToPot1);
        setCurrentBet(bet);

        return chipsToAddToPot1;
    }

    public void resetActions() {
        setLatestAction(null);
    }
    
    void fold() {
    }

    void substract(int bet) {
        stack -= bet;
    }

    @Override
    public String toString() {
        return "Player{" 
                + "\nname=" + name
                + ", \nchips=" + stack
                + ", \ncurrentBet=" + currentBet 
                + ", \nhand=" + hand + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Player other = (Player) obj;

        return Objects.equals(this.name, other.name);
    }

    public String getName() {
        return name;
    }

    private void promptPossibleActions(ArrayList<Action> actions) {
        String message = "" + this.getName();

        for (int i = 0; i < actions.size(); ++i) {
            message += "\nType " + i + " TO " + actions.get(i).toString() + "\n";
        }

        System.out.println(message);
    }
}
