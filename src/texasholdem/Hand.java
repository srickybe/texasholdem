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
import java.util.ArrayList;

/**
 * Invariant: the cards forming the hand are sorted cards.get(0) is the highest
 * ranked card of the hand
 */
public class Hand implements Comparable<Hand> {

    static private final CardComparator COMPARATOR = new CardComparator();
    private final ArrayList<Card> cards;

    public Hand() {
        cards = new ArrayList<>();
    }

    @Override
    public int compareTo(Hand other) {
        return computeTypeAndValue().compareTo(other.computeTypeAndValue());
    }

    public boolean addCard(Card card) {
        if (!cards.contains(card)) {
            cards.add(card);
            sortCards();

            return true;
        }

        return false;
    }

    public boolean contains(Card card) {
        return cards.contains(card);
    }

    public Card getCard(int index) {
        return cards.get(index);
    }

    public int size() {
        return cards.size();
    }

    public void sortCards() {
        cards.sort(COMPARATOR);
    }

    private boolean isStraightFlush() {
        return isStraight() && isFlush();
    }

    private boolean isFourOfAKind() {
        return (cards.get(0).sameRank(cards.get(1))
                && cards.get(0).sameRank(cards.get(2))
                && cards.get(0).sameRank(cards.get(3)))
                || (cards.get(1).sameRank(cards.get(2))
                && cards.get(1).sameRank(cards.get(3))
                && cards.get(1).sameRank(cards.get(4)));
    }

    private boolean isFullHouse() {
        return (cards.get(0).sameRank(cards.get(1))
                && cards.get(1).sameRank(cards.get(2))
                && cards.get(3).sameRank(cards.get(4)))
                || (cards.get(0).sameRank(cards.get(1))
                && cards.get(2).sameRank(cards.get(3))
                && cards.get(3).sameRank(cards.get(4)));
    }

    private boolean isFlush() {
        return cards.get(0).sameSuit(cards.get(1))
                && cards.get(1).sameSuit(cards.get(2))
                && cards.get(2).sameSuit(cards.get(3))
                && cards.get(3).sameSuit(cards.get(4));
    }

    private boolean isStraight() {
        return (cards.get(0).oneRankHigher(cards.get(1))
                && cards.get(1).oneRankHigher(cards.get(2))
                && cards.get(2).oneRankHigher(cards.get(3))
                && cards.get(3).oneRankHigher(cards.get(4)))
                || (cards.get(0).getRank().equals(Rank.ACE)
                && cards.get(1).getRank().equals(Rank.FIVE)
                && cards.get(2).getRank().equals(Rank.FOUR)
                && cards.get(3).getRank().equals(Rank.THREE)
                && cards.get(4).getRank().equals(Rank.TWO));
    }

    private boolean isThreeOfAKind() {
        return (cards.get(0).sameRank(cards.get(1))
                && cards.get(1).sameRank(cards.get(2)))
                || (cards.get(1).sameRank(cards.get(2))
                && cards.get(2).sameRank(cards.get(3)))
                || (cards.get(2).sameRank(cards.get(3))
                && cards.get(3).sameRank(cards.get(4)));
    }

    private boolean isTwoPairs() {
        if (cards.get(0).sameRank(cards.get(1))
                && cards.get(2).sameRank(cards.get(3))) {
            return true;
        }

        if (cards.get(0).sameRank(cards.get(1))
                && cards.get(3).sameRank(cards.get(4))) {
            return true;
        }

        return cards.get(1).sameRank(cards.get(2))
                && cards.get(3).sameRank(cards.get(4));
    }

    private boolean isOnePair() {
        return cards.get(0).sameRank(cards.get(1))
                || cards.get(1).sameRank(cards.get(2))
                || cards.get(2).sameRank(cards.get(3))
                || cards.get(3).sameRank(cards.get(4));
    }

    public HandEvaluation computeTypeAndValue() {
        if (cards.size() != 5) {
            throw new UnsupportedOperationException("cards.size() != 5");
        }

        if (isStraightFlush()) {
            if (cards.get(0).getRank().equals(Rank.ACE)) {
                return new HandEvaluation(
                        HandType.ROYAL_FLUSH,
                        computeStraightFlushValue());
            }

            return new HandEvaluation(
                    HandType.STRAIGHT_FLUSH,
                    computeStraightFlushValue());
        }

        if (isFourOfAKind()) {
            return new HandEvaluation(
                    HandType.FOUR_OF_A_KIND,
                    computeFourOfAKindValue());
        }

        if (isFullHouse()) {
            return new HandEvaluation(HandType.FULL_HOUSE, computeFullHouseValue());
        }

        if (isFlush()) {
            return new HandEvaluation(HandType.FLUSH, computeFlushValue());
        }

        if (isStraight()) {
            return new HandEvaluation(HandType.STRAIGHT, computeStraightValue());
        }

        if (isThreeOfAKind()) {
            return new HandEvaluation(
                    HandType.THREE_OF_A_KIND,
                    computeThreeOfAKindValue());
        }

        if (isTwoPairs()) {
            return new HandEvaluation(HandType.TWO_PAIRS, computeTwoPairsValue());
        }

        if (isOnePair()) {
            return new HandEvaluation(HandType.ONE_PAIR, computeOnePairValue());
        }

        if (cards != null) {
            return new HandEvaluation(HandType.HIGH_CARD, computeHighCardValue());
        }

        return null;
    }

    private int computeStraightFlushValue() {
        return cards.get(0).getRank().getCardinal();
    }

    private int computeFourOfAKindValue() {
        if (cards.get(0).sameRank(cards.get(1))
                && cards.get(0).sameRank(cards.get(2))
                && cards.get(0).sameRank(cards.get(3))) {
            return cards.get(0).getRank().getCardinal() * 16
                    + cards.get(4).getRank().getCardinal();
        }

        if (cards.get(1).sameRank(cards.get(2))
                && cards.get(1).sameRank(cards.get(3))
                && cards.get(1).sameRank(cards.get(4))) {
            return cards.get(1).getRank().getCardinal() * 16
                    + cards.get(0).getRank().getCardinal();
        }

        return -1;
    }

    private int computeFullHouseValue() {
        if (cards.get(0).sameRank(cards.get(1))
                && cards.get(1).sameRank(cards.get(2))
                && cards.get(3).sameRank(cards.get(4))) {
            return cards.get(0).getRank().getCardinal() * 16
                    + cards.get(3).getRank().getCardinal();
        }

        if (cards.get(0).sameRank(cards.get(1))
                && cards.get(2).sameRank(cards.get(3))
                && cards.get(3).sameRank(cards.get(4))) {
            return cards.get(2).getRank().getCardinal() * 16
                    + cards.get(0).getRank().getCardinal();
        }

        return -1;
    }

    private int computeFlushValue() {
        return cards.get(0).getRank().getCardinal();
    }

    private int computeStraightValue() {
        if (!cards.get(0).getRank().equals(Rank.ACE)
                || !cards.get(1).getRank().equals(Rank.FIVE)) {
            return cards.get(0).getRank().getCardinal();
        }

        return Rank.FIVE.getCardinal();
    }

    private int computeThreeOfAKindValue() {
        if (cards.get(0).sameRank(cards.get(1))
                && cards.get(1).sameRank(cards.get(2))) {
            return cards.get(0).getRank().getCardinal() * 256
                    + cards.get(3).getRank().getCardinal() * 16
                    + cards.get(4).getRank().getCardinal();
        }

        if (cards.get(1).sameRank(cards.get(2))
                && cards.get(2).sameRank(cards.get(3))) {
            return cards.get(1).getRank().getCardinal() * 256
                    + cards.get(0).getRank().getCardinal() * 16
                    + cards.get(4).getRank().getCardinal();
        }

        if (cards.get(2).sameRank(cards.get(3))
                && cards.get(3).sameRank(cards.get(4))) {
            return cards.get(2).getRank().getCardinal() * 256
                    + cards.get(0).getRank().getCardinal() * 16
                    + cards.get(1).getRank().getCardinal();
        }

        return -1;
    }

    private int computeTwoPairsValue() {
        if (cards.get(0).sameRank(cards.get(1))
                && cards.get(2).sameRank(cards.get(3))) {
            return cards.get(0).getRank().getCardinal() * 256
                    + cards.get(2).getRank().getCardinal() * 16
                    + cards.get(4).getRank().getCardinal();
        }

        if (cards.get(0).sameRank(cards.get(1))
                && cards.get(3).sameRank(cards.get(4))) {
            return cards.get(0).getRank().getCardinal() * 256
                    + cards.get(3).getRank().getCardinal() * 16
                    + cards.get(2).getRank().getCardinal();
        }

        if (cards.get(1).sameRank(cards.get(2))
                && cards.get(3).sameRank(cards.get(4))) {
            return cards.get(1).getRank().getCardinal() * 256
                    + cards.get(3).getRank().getCardinal() * 16
                    + cards.get(0).getRank().getCardinal();
        }

        return -1;
    }

    private int computeOnePairValue() {
        if (cards.get(0).sameRank(cards.get(1))) {
            return cards.get(0).getRank().getCardinal() * 4096
                    + cards.get(2).getRank().getCardinal() * 256
                    + cards.get(3).getRank().getCardinal() * 16
                    + cards.get(4).getRank().getCardinal();
        }

        if (cards.get(1).sameRank(cards.get(2))) {
            return cards.get(1).getRank().getCardinal() * 4096
                    + cards.get(0).getRank().getCardinal() * 256
                    + cards.get(3).getRank().getCardinal() * 16
                    + cards.get(4).getRank().getCardinal();
        }

        if (cards.get(2).sameRank(cards.get(3))) {
            return cards.get(2).getRank().getCardinal() * 4096
                    + cards.get(0).getRank().getCardinal() * 256
                    + cards.get(1).getRank().getCardinal() * 16
                    + cards.get(4).getRank().getCardinal();
        }

        if (cards.get(3).sameRank(cards.get(4))) {
            return cards.get(3).getRank().getCardinal() * 4096
                    + cards.get(0).getRank().getCardinal() * 256
                    + cards.get(1).getRank().getCardinal() * 16
                    + cards.get(2).getRank().getCardinal();
        }

        return -1;
    }

    private int computeHighCardValue() {
        return cards.get(0).getRank().getCardinal() * 65536
                + cards.get(1).getRank().getCardinal() * 4096
                + cards.get(2).getRank().getCardinal() * 256
                + cards.get(3).getRank().getCardinal() * 16
                + cards.get(4).getRank().getCardinal();
    }

    public Hand bestHand() {
        switch (cards.size()) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                throw new UnsupportedOperationException("A hand has at least "
                        + "five cards!!!");
            case 5:
                return this;

            case 6:
                return bestHandFrom6Cards();

            case 7:
                return bestHandFrom7Cards();

            default:
                throw new UnsupportedOperationException("A hand has at most "
                        + "seven cards!!!");
        }
    }

    private Hand bestHandFrom7Cards() {
        if (size() != 7) {
            return null;
        }

        Hand result = null;
        HandEvaluation maxEval = new HandEvaluation(HandType.HIGH_CARD, -1);

        for (int i = 0; i < size(); ++i) {
            Card c1 = cards.remove(i);

            for (int j = i; j < size(); ++j) {
                Card c2 = cards.remove(j);
                HandEvaluation eval = computeTypeAndValue();

                if (eval.compareTo(maxEval) > 0) {
                    maxEval = eval;
                    result = new Hand();
                    result.addCard(getCard(0));
                    result.addCard(getCard(1));
                    result.addCard(getCard(2));
                    result.addCard(getCard(3));
                    result.addCard(getCard(4));
                }

                cards.add(j, c2);
            }

            cards.add(i, c1);
        }

        return result;
    }

    private Hand bestHandFrom6Cards() {
        if (size() != 6) {
            return null;
        }

        Hand result = null;
        HandEvaluation maxEval = new HandEvaluation(HandType.HIGH_CARD, -1);

        for (int i = 0; i < size(); ++i) {
            Card c2 = cards.remove(i);
            HandEvaluation eval = computeTypeAndValue();

            if (eval.compareTo(maxEval) > 0) {
                maxEval = eval;
                result = new Hand();
                result.addCard(getCard(0));
                result.addCard(getCard(1));
                result.addCard(getCard(2));
                result.addCard(getCard(3));
                result.addCard(getCard(4));
            }

            cards.add(i, c2);
        }

        return result;
    }

    @Override
    public String toString() {
        return "Hand{" + "cards=" + cards + '}';
    }

    public static void main(String[] args) {
        for (int j = 0; j < 100; ++j) {
            Hand hand = new Hand();

            for (int i = 0; i < 7; ++i) {
                while (!hand.addCard(Card.random())) {
                }
            }

            System.out.println("hand = " + hand);
            Hand bestHand = hand.bestHandFrom7Cards();
            System.out.println("best hand = " + bestHand);
            HandEvaluation eval = bestHand.computeTypeAndValue();
            System.out.println("best hand type and value = " + eval);
            System.out.println("hand = " + hand + "\n\n");
        }
    }
}
