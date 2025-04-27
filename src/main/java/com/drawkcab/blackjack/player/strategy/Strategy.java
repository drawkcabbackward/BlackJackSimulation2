package com.drawkcab.blackjack.player.strategy;

import com.drawkcab.blackjack.game.Card;
import com.drawkcab.blackjack.player.Move;
import com.drawkcab.blackjack.player.HandState;

import java.math.BigDecimal;

/**
 * Defines the strategy for making decisions in a game of Blackjack.
 *
 * <p>Implementations of this interface determine the player's next move
 * based on the current hand state, the dealer's visible face-up card,
 * and the player's current available bank (for making decisions like splitting or doubling down).</p>
 */
public interface Strategy {
    /**
     * Determines the next move for the player based on the current game context.
     *
     * @param hand the current state of the player's hand
     * @param dealerFaceUpCard the dealer's visible face-up card
     * @param bank the player's current available bank for betting decisions
     * @return the {@link Move} that the player should make next
     */
    Move getNextMove(HandState hand, Card dealerFaceUpCard, BigDecimal bank);
}
