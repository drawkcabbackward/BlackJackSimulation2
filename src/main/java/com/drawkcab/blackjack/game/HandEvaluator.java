package com.drawkcab.blackjack.game;

import com.drawkcab.blackjack.player.HandState;

/**
 * Evaluates the outcome of a player's hand against the dealer's hand
 * in a game of Blackjack.
 *
 * <p>Determines if the player wins, loses, pushes (ties),
 * wins with a Blackjack, or surrenders, based on the final hands of both
 * the player and the dealer according to standard Blackjack rules.</p>
 */
public class HandEvaluator {
    HandEvaluator() {
    }


    /**
     * Determines the outcome of a player's hand compared to the dealer's hand.
     *
     * <p>The outcome is decided based on the following priority:
     * <ul>
     *     <li>Dealer Blackjack vs Player Blackjack (push) or player loss</li>
     *     <li>Player surrender</li>
     *     <li>Player Blackjack win</li>
     *     <li>Player bust (loss)</li>
     *     <li>Dealer bust (player win)</li>
     *     <li>Comparing hand totals (higher wins, equal pushes)</li>
     * </ul>
     * </p>
     *
     * @param handState  the player's hand
     * @param dealerHand the dealer's hand
     * @return the {@link HandOutcome} representing the result for the player
     */
    public HandOutcome getOutcome(HandState handState, HandState dealerHand) {
        if (dealerHand.isBlackJack()) {
            return handState.isBlackJack() ? HandOutcome.PUSH : HandOutcome.LOSS;
        }

        if (handState.isSurrendered()) {
            return HandOutcome.SURRENDER;
        }

        if (handState.isBlackJack()) {
            return HandOutcome.BLACKJACK_WIN;
        }

        if (handState.isBust()) {
            return HandOutcome.LOSS;
        }

        if (dealerHand.isBust()) {
            return HandOutcome.WIN;
        }

        if (handState.getTotalValue() > dealerHand.getTotalValue()) {
            return HandOutcome.WIN;
        } else if (handState.getTotalValue() == dealerHand.getTotalValue()) {
            return HandOutcome.PUSH;
        } else {
            return HandOutcome.LOSS;
        }
    }
}
