package com.drawkcab.blackjack.game;

import com.drawkcab.blackjack.game.exception.InvalidSplitException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a hand of cards in a Blackjack game.
 *
 * <p>A hand maintains a mutable list of cards that can grow as the player hits or splits during
 * gameplay. This class has logic for handling splits and is able to answer common BlackJack
 * questions about a hand.</p>
 *
 * @see Card
 */
public class Hand {
    private final List<Card> cards;

    /**
     * Creates a new hand containing the specified cards.
     *
     * @param cards the initial cards in the hand
     */
    public Hand(List<Card> cards) {
        // Make a copy because we may have been passed an immutable list of cards.
        this.cards =  new ArrayList<Card>(cards);
    }

    /**
     * Calculates the total value of the hand according to Blackjack rules.
     *
     * <p>Face cards (Jack, Queen, King) are counted as 10. Aces are counted as 11 when it would
     * not cause the hand to bust (go over 21), otherwise they are counted as 1.</p>
     *
     * @return the best possible total value of the hand
     */
    public int totalValue() {
        boolean hasAce = false;
        int total = 0;

        // First calculate the total, with Ace treated as 1.
        total = rawTotal();

        // Now handle promotion of Aces.
        // We only need to check if there are any aces. It's not possible for two aces to both be
        // valued at 11 in the same hand.
        if (cards.contains(Card.ACE) && total <= 11) {
            total += 10;
        }

        return total;
    }

    /**
     * Checks whether the current hand is eligible to be split.
     *
     * <p>Splitting is allowed if the hand contains exactly two cards of the same value (e.g.,
     * King and Queen can be split because both are worth 10).</p>
     *
     * @return {@code true} if the hand can be split; {@code false} otherwise
     */
    public boolean canSplit() {
        return cards.size() == 2 && cards.get(0).getValue() == cards.get(1).getValue();
    }

    /**
     * Returns what card would be in each hand if a split were to occur.
     *
     * <p>This is read only and does not change the Hand.</p>
     *
     * @return the card eligible for splitting
     * @throws InvalidSplitException if the hand cannot be split
     */
    public Card getSplitCard() {
        if (!canSplit()) {
            throw new InvalidSplitException("No split card available for unsplittable hand");
        }

        return cards.getFirst();
    }

    /**
     * Splits the current hand by removing one card and creating a new hand with it.
     *
     * <p>Splitting is allowed only if the hand is eligible (see {@link #canSplit()}). If
     * splitting is not allowed, an {@link InvalidSplitException} is thrown.</p>
     *
     * @return a new {@link Hand} containing the removed card
     * @throws InvalidSplitException if the hand is not eligible for splitting
     */
    public Hand split() {
        if (!canSplit()) {
            throw new InvalidSplitException(
                    String.format("Hand is not eligible to be split. Hand = [%s]", this));
        }
        return new Hand(List.of(cards.removeLast()));
    }

    /**
     * @return {@code true} if this hand was just split and contains a single card.
     */
    public boolean justSplit() {
        return cards.size() == 1;
    }

    /**
     * Returns true if the hand is considered in its initial state.
     *
     * <p>Importantly, a split hand (single card before hit) is not considered an initial hand,
     * and only after the automatic hit does it become an initial hand.</p>
     *
     * @return {@code true} if an initial hand (2 cards).
     */
    public boolean isInitialHand() {
        return cards.size() == 2;
    }

    /**
     * Adds a card to the hand.
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * Returns the first (face-up) card of the hand.
     *
     * @return the face-up {@link Card}
     * @throws IllegalStateException if the hand is empty
     */
    public Card getFaceUpCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("A face up card can not be retrieved when the hand " +
                    "has no cards.");
        }

        return cards.getFirst();
    }

    /**
     * @return {@code true} if the hand is a Blackjack (Ace + 10-value card).
     */
    public boolean isBlackJack() {
        return cards.size() == 2 && totalValue() == 21;
    }

    /**
     * @return {@code true} if the hand is a "soft" hand (Ace counted as 11).
     */
    public boolean isSoft() {
        return cards.contains(Card.ACE) && rawTotal() <= 11;
    }

    /**
     * @return {@code true} if the hand's total value exceeds 21 (busted).
     */
    public boolean isBust() {
        return totalValue() > 21;
    }

    private int rawTotal() {
        int total = 0;

        for(Card card : cards) {
            total += card.getValue();
        }

        return total;
    }
}
