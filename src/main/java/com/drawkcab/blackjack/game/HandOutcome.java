package com.drawkcab.blackjack.game;

/**
 * Represents the possible outcomes of a player's hand
 * after a round of Blackjack is completed.
 *
 * <p>Each outcome determines how the player's bet is handled:
 * win full, win with Blackjack bonus, push (tie), lose full bet, or
 * lose half bet (surrender).</p>
 */
public enum HandOutcome {
    /**
     * Player wins the round with a normal hand (not Blackjack).
     * Typically results in a payout of 1:1 (double the original bet).
     */
    WIN,

    /**
     * Player wins with a natural Blackjack (Ace + 10-value card on the initial deal).
     * Typically results in a bonus payout of 3:2.
     */
    BLACKJACK_WIN,

    /**
     * The round ends in a tie — the player's total and the dealer's total are equal.
     * The player retains their original bet (no win, no loss).
     */
    PUSH,

    /**
     * Player loses the round, usually due to busting or having a lower total than the dealer.
     * Results in losing the full bet.
     */
    LOSS,

    /**
     * Player chooses to surrender during the round.
     * Results in losing half the original bet and ending the round immediately.
     */
    SURRENDER
}