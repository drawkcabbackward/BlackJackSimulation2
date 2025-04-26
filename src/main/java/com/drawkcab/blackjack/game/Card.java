package com.drawkcab.blackjack.game;

/**
 * A Card is the basic building block of BlackJack.
 *
 * <p>Generally, cards have a Rank and Suite, however in BlackJack, the suit doesn't matter.
 * In this situation, better to simplify Card to only what will be needed.</p>
 *
 * <p>Each rank has an associated base blackjack value. Number cards are worth
 * their face value, face cards (Jack, Queen, King) are worth 10, and the Ace
 * is worth 1 by default. Special handling for Ace (counting as 11) is managed
 * during hand evaluation, not within this enum.</p>
 */
public enum Card {
    ACE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(10),
    QUEEN(10),
    KING(10);

    private final int value;

    Card(int value) {
        this.value = value;
    }

    /**
     * Returns the base blackjack value of the card.
     * <p>Note: Ace is treated as 1 by default; handling 11 is the responsibility of hand logic.</p>
     *
     * @return the blackjack value of the card
     */
    public int getValue() {
        return value;
    }
}
