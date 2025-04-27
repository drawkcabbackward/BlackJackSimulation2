package com.drawkcab.blackjack.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a deck of cards for use in a game of Blackjack.
 *
 * <p>This deck can consist of a single standard 52-card set or multiple combined decks (e.g., a 6-deck shoe).
 * Cards are shuffled at creation and after each manual shuffle operation.</p>
 *
 * <p>Cards are dealt sequentially from the top. Once all cards are dealt, attempting to deal further
 * will result in an {@link IllegalStateException}.</p>
 */
public class Deck {
    // Using a List + position instead of Deque to allow efficient random access for shuffling.
    private final List<Card> cards;
    private int pos;

    /**
     * Constructs a new shuffled deck.
     *
     * @param numDecks the number of standard 52-card decks to include
     */
    Deck(int numDecks) {
        cards = new ArrayList<>();
        pos = 0;

        for (Card card : Card.values()) {
            for (int i = 0; i < numDecks; i++) {
                cards.add(card);
            }
        }
        shuffle();
    }

    /**
     * Deals the next card from the deck.
     *
     * <p>If no cards remain, this method will throw an {@link IllegalStateException}.</p>
     *
     * @return the next {@link Card} in the deck
     * @throws IllegalStateException if the deck is empty
     */
    public Card getNextCard() {
        if (pos >= cards.size()) {
            throw new IllegalStateException("No cards remaining in deck.");
        }

        return cards.get(pos++);
    }

    /**
     * Shuffles the entire deck and resets the dealing position.
     *
     * <p>Shuffling randomizes the order of all cards, including those already dealt.</p>
     */
    public void shuffle() {
        // Collections.shuffle() performs an in-place O(n) Fisher–Yates shuffle.
        Collections.shuffle(cards);
        pos = 0;
    }

    /**
     * Returns the number of cards remaining to be dealt.
     *
     * @return the number of undealt cards remaining in the deck
     */
    public int cardsRemaining() {
        return cards.size() - pos;
    }
}
