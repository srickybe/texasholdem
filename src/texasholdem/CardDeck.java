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
public class CardDeck {

    /**
     * Create a 52-card deck. The cards are ordered from rank 2 to rank ace, and
     * the suits in alphabetic order. The card at the top is Ace Spades and at
     * the bottom, Two Clubs.
     *
     * @return a 52-card deck
     */
    public static CardDeck createFiftyTwoCardDeck() {
        CardDeck cardDeck = new CardDeck();

        for (Card c : Card.values()) {
            cardDeck.add(c);
        }

        return cardDeck;
    }

    public static CardDeck createEmptyCardDeck() {
        return new CardDeck();
    }

    private final Stack<Card> cards;

    public CardDeck() {
        cards = new Stack<>();
    }

    public void add(Card c) {
        if (!cards.contains(c)) {
            cards.add(c);
        } else {

            throw new UnsupportedOperationException(
                    "Card deck should contain a single copy of each card");
        }
    }

    public Card pop() {
        return cards.pop();
    }

    /**
     * Shuffles the stack of cards
     */
    public void shuffle() {
        int n = cards.size();

        for (int i = 0; i < n; ++i) {
            int r = (int) (n * Math.random());
            Card tmp = cards.get(i);
            cards.set(i, cards.get(r));
            cards.set(r, tmp);
        }
    }

    @Override
    public String toString() {
        String res = "CardDeck{" + "cards=[";

        for (int i = 0; i < cards.size(); ++i) {
            res += "\n" /*+ i + ":" */ + cards.get(i);
        }

        res += "\n]}";

        return res;
    }

    public static void main(String args[]) {
        CardDeck cardDeck = CardDeck.createFiftyTwoCardDeck();
        System.out.println("cardDeck" + cardDeck);
    }
}
