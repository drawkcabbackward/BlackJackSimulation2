package com.drawkcab.blackjack.player;

import com.drawkcab.blackjack.game.Card;
import com.drawkcab.blackjack.game.Hand;

import java.math.BigDecimal;


/**
 * Represents the state of an individual player hand during a round of Blackjack.
 *
 * <p>This class tracks the cards in the hand, the bet amount, and whether the hand is
 * finished, surrendered, bust, or eligible for special moves like split or double down.</p>
 */
public class HandState {
    private final Hand hand;
    private BigDecimal betAmount;
    private boolean finished;
    private boolean surrendered;

    /**
     * Creates a new hand state with the given initial hand and bet amount.
     *
     * @param hand      the initial cards in the hand
     * @param betAmount the wager associated with this hand
     */
    public HandState(Hand hand, BigDecimal betAmount) {
        this.hand = hand;
        this.betAmount = betAmount;
        this.finished = false;
        this.surrendered = false;
    }

    // --- Actions on the hand ---

    /**
     * Adds a card to the hand.
     *
     * <p>If the hand busts after the hit, the hand is automatically marked as finished.</p>
     *
     * @param card the card to add
     * @throws IllegalStateException if the hand is already finished
     */
    public void hit(Card card) {
        throwIfFinished();

        hand.addCard(card);

        if (hand.isBust()) {
            finished = true;
        }
    }

    /**
     * Doubles the current bet and adds exactly one more card to the hand, marking the hand as finished.
     *
     * @param card the card to add
     * @throws IllegalStateException if the hand is already finished
     */
    public void doubleDown(Card card) {
        throwIfFinished();

        if (!isInitialHand()) {
            throw new IllegalStateException("Can't double down after player has made a move");
        }

        betAmount = betAmount.multiply(BigDecimal.TWO);
        hand.addCard(card);
        finished = true;
    }

    /**
     * Splits the current hand into two hands.
     *
     * <p>Returns a new {@code HandState} representing the second hand created from the split.</p>
     *
     * @return a new split {@code HandState}
     * @throws IllegalStateException if the hand is already finished
     * @throws com.drawkcab.blackjack.game.exception.InvalidSplitException if hand cannot be split.
     */
    public HandState split() {
        throwIfFinished();

        return new HandState(hand.split(), betAmount);
    }

    /**
     * Stands on the hand, ending the player's turn for this hand.
     *
     * @throws IllegalStateException if the hand is already finished
     */
    public void stand() {
        throwIfFinished();

        finished = true;
    }

    /**
     * Surrenders the hand, ending the player's turn for this hand.
     *
     * <p>Surrender is only allowed on the initial hand (before any hits or splits).</p>
     *
     * @throws IllegalStateException if the hand is already finished
     * @throws IllegalStateException if the hand is not in its initial state
     */
    public void surrender() {
        throwIfFinished();

        if (!isInitialHand()) {
            throw new IllegalStateException("Can't surrender after player has made a move");
        }

        finished = true;
        surrendered = true;
    }

    // --- Information about the hand ---

    /**
     * Returns the current bet amount for the hand.
     */
    public BigDecimal getBetAmount() {
        return betAmount;
    }

    /**
     * Returns the total value of the hand.
     * <p>If the hand is surrendered, the value is treated as 0.</p>
     */
    public int getTotalValue() {
        return isSurrendered()  ? 0 : hand.totalValue();
    }

    /**
     * Returns whether the hand is a "soft" hand (contains an Ace counted as 11).
     */
    public boolean isSoft() {
        return hand.isSoft();
    }

    /**
     * Returns whether the player surrendered this hand.
     */
    public boolean isSurrendered() {
        return surrendered;
    }

    /**
     * Returns whether the hand has busted (total value exceeds 21).
     */
    public boolean isBust() {
        return getTotalValue() > 21;
    }

    /**
     * Returns whether the hand is a Blackjack (Ace + 10-value card initially).
     */
    public boolean isBlackJack() {
        return hand.isBlackJack();
    }

    /**
     * Returns whether the hand is finished and can no longer be played.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Returns whether the hand can currently be split.
     */
    public boolean canSplit() {
        return hand.canSplit();
    }

    /**
     * Returns whether the hand was just created from a split.
     */
    public boolean justSplit() {
        return hand.justSplit();
    }

    /**
     * Returns whether the hand is still in its initial two-card state.
     */
    public boolean isInitialHand() {
        return hand.isInitialHand();
    }

    /**
     * Returns the card that would be used to split this hand.
     *
     * <p>This method should only be called if {@link #canSplit()} returns {@code true}.
     * Calling this method on a hand that cannot be split may result in an
     * {@link com.drawkcab.blackjack.game.exception.InvalidSplitException}.</p>
     *
     * @return the split card
     * @throws com.drawkcab.blackjack.game.exception.InvalidSplitException if the hand cannot be
     *                                                                     split
     */
    public Card getSplitCard() {
        return hand.getSplitCard();
    }

    /**
     * Returns the first (face-up) card of the hand.
     *
     * <p>This is to inform the player of what card the Dealer is showing, as while the player has
     * all their cards face up, the dealer only has one face up card.</p>
     *
     * @return the face-up card
     */
    public Card getFaceUpCard() {
        return hand.getFaceUpCard();
    }

    private void throwIfFinished() {
        if (isFinished()) {
            throw new IllegalStateException("Cannot make a move on the hand after it is in the " +
                    "finished state");
        }
    }
}
