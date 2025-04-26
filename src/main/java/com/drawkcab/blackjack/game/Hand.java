package com.drawkcab.blackjack.game;

import com.drawkcab.blackjack.game.exception.InvalidSplitException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a hand of cards in a Blackjack game.
 *
 * <p>A hand maintains a mutable list of cards that can grow as the player hits or splits during
 * gameplay. It provides methods for calculating the best possible hand value (considering Aces
 * as either 1 or 11), checking for split eligibility, and performing splits.</p>
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

        // The algorithm counts aces as 1 initially and then tries to promote an ace to 11 if
        // promotion would keep the total less than or equal to 21.
        for(Card card : cards) {
            if (card == Card.ACE) {
                hasAce = true;
            }

            // For ace this will return 1, for every other card it will return the only value it
            // has.
            total += card.getValue();
        }

        // We only need to check if there are any aces. Its not possible for two aces to both be
        // valued at 11 in the same hand.
        if (hasAce && total <= 11) {
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

    public void addCard(Card card) {
        cards.add(card);
    }
}
