package com.drawkcab.blackjack.player.strategy;

import com.drawkcab.blackjack.game.Card;
import com.drawkcab.blackjack.player.Move;
import com.drawkcab.blackjack.player.HandState;

import java.math.BigDecimal;

/**
 * A fixed strategy implementation for the dealer in Blackjack.
 *
 * <p>According to standard Blackjack rules, the dealer must:
 * <ul>
 *   <li>Hit if the hand total is less than 17</li>
 *   <li>Stand if the hand total is 17 or more</li>
 * </ul>
 *
 * <p>This strategy does not consider the dealer's face-up card, player's hands, or available bank balance,
 * and always follows the predefined dealer behavior.</p>
 */
public class DealerStrategy implements Strategy {

    /**
     * Determines the dealer's next move based solely on the current hand total.
     *
     * <p>This strategy stands on soft seventeen and does not hit.</p>
     *
     * @param hand the dealer's current hand
     * @param dealerFaceUpCard the dealer's visible card (ignored in this strategy)
     * @param bank the current bank balance (ignored in this strategy)
     * @return {@link Move#HIT} if the hand total is less than 17, {@link Move#STAND} otherwise
     */
    @Override
    public Move getNextMove(HandState hand, Card dealerFaceUpCard, BigDecimal bank) {
        if (hand.getTotalValue() >= 17) {
            return Move.STAND;
        } else {
            return Move.HIT;
        }
    }
}
