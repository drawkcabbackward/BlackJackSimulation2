package com.drawkcab.blackjack.player;

/**
 * Represents the possible moves a player can make during a game of Blackjack.
 *
 * <p>Each move follows standard Blackjack rules:</p>
 * <ul>
 *   <li><b>HIT</b>: Receive an additional card.</li>
 *   <li><b>STAND</b>: End your turn and keep your current hand.</li>
 *   <li><b>SPLIT</b>: Split your hand into two separate hands if your first two cards have the
 *   same value. Requires an additional bet equal to the original bet.</li>
 *   <li><b>DOUBLE_DOWN</b>: Double your bet, take exactly one more card, and then stand.</li>
 *   <li><b>SURRENDER</b>: Forfeit the hand early, losing half of your bet to exit the round.</li>
 * </ul>
 */
public enum Move {
    /**
     * Take another card.
     */
    HIT,

    /**
     * End your turn and keep your current hand.
     */
    STAND,

    /**
     * Split your hand into two hands if the first two cards have the same value.
     * <p>Requires an additional bet equal to the original bet.</p>
     */
    SPLIT,

    /**
     * Double your bet, take exactly one additional card, and then end your turn.
     */
    DOUBLE_DOWN,

    /**
     * Forfeit the hand early and lose half your bet.
     */
    SURRENDER
}
