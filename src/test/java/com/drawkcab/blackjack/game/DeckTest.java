package com.drawkcab.blackjack.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeckTest {

    private Deck deck;

    @BeforeEach
    void setUp() {
        deck = new Deck(1); // Single standard deck
    }

    @Test
    void getNextCard_repeatedCalls_exhaustsDeck() {
        int count = dealAllCards(deck);

        assertThat(count).isEqualTo(52);
    }

    @Test
    void getNextCard_throwsWhenDeckEmpty() {
        dealAllCards(deck);

        assertThrows(IllegalStateException.class, () -> deck.getNextCard());
    }

    @Test
    void cardsRemaining_hasAccurateInitialCount() {
        assertThat(deck.cardsRemaining()).isEqualTo(52);
    }

    @Test
    void cardsRemaining_afterGettingCard_hasAccurateCount() {
        deck.getNextCard();

        assertThat(deck.cardsRemaining()).isEqualTo(51);
    }

    @Test
    void cardsRemaining_afterGettingMultipleCards_hasAccurateCount() {
        dealCards(deck, 10);

        assertThat(deck.cardsRemaining()).isEqualTo(42);
    }

    @Test
    void shuffle_resetsDeck() {
        dealCards(deck, 10);

        deck.shuffle();

        assertThat(deck.cardsRemaining()).isEqualTo(52);
    }

    @Test
    void shuffle_randomizesDeckOrder() {
        List<Card> originalOrder = dealCards(deck, 10);

        deck.shuffle();

        List<Card>  newOrder = dealCards(deck, 10);

        // Because shuffle is random, we can't guarantee change, but it's extremely unlikely the
        // first 10 cards are identical, even ignoring suit.
        assertThat(newOrder).isNotEqualTo(originalOrder);
    }

    @Test
    void multipleDecks_hasCorrectCount() {
        Deck freshDeck = new Deck(6);

        assertThat(freshDeck.cardsRemaining()).isEqualTo(312);
    }

    private static int dealAllCards(Deck deck) {
        int count = 0;
        while (deck.cardsRemaining() > 0) {
            deck.getNextCard();
            count++;
        }

        return count;
    }

    private static List<Card> dealCards(Deck deck, int count) {
        List<Card> dealtCards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            dealtCards.add(deck.getNextCard());
        }

        return dealtCards;
    }
}