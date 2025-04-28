package com.drawkcab.blackjack.player;

import com.drawkcab.blackjack.game.Card;
import com.drawkcab.blackjack.game.Hand;
import com.drawkcab.blackjack.player.strategy.Strategy;

import java.math.BigDecimal;

/**
 * Represents the dealer in a Blackjack game.
 *
 * <p>The dealer behaves similarly to a player but follows specific dealer rules such as revealing a
 * face-up card and checking for Blackjack.</p>
 */
public class Dealer extends Player {

    /**
     * Creates a new Dealer with the given strategy.
     */
    public Dealer(Strategy strategy) {
        super( strategy, BigDecimal.ZERO);
    }

    /**
     * Returns the dealer's face-up card.
     */
    public Card getFaceUpCard() {
        return getActivePlayerHand().getFaceUpCard();
    }

    /**
     * Starts a new round for the dealer with the provided hand.
     */
    public void startRound(Hand hand) {
        super.startRound(hand, BigDecimal.ZERO, hand.getFaceUpCard());
    }

    /**
     * Checks whether the dealer has a Blackjack.
     */
    public boolean hasBlackJack() {
        return getActivePlayerHand().isBlackJack();
    }
}
