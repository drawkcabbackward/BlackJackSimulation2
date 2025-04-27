package com.drawkcab.blackjack.game.exception;

/**
 * Exception thrown when a player attempts to split a Blackjack hand
 * that is not eligible for splitting.
 *
 * <p>Splitting is only allowed when the hand contains exactly two cards
 * of the same value (e.g. two 8s verse a King and a Queen). Attempting
 * to split an invalid hand will result in this exception being thrown.</p>
 *
 * @see com.drawkcab.blackjack.game.Hand
 */
public class InvalidSplitException extends RuntimeException {
    /**
     * Constructs a new {@code InvalidSplitException} with the specified detail message.
     *
     * @param message the detail message explaining why the split was invalid
     */
    public InvalidSplitException(String message) {
        super(message);
    }
}
