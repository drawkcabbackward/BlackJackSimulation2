package com.drawkcab.blackjack.player;

import com.drawkcab.blackjack.game.Card;
import com.drawkcab.blackjack.game.Hand;
import com.drawkcab.blackjack.player.strategy.Strategy;

import java.math.BigDecimal;

public class Dealer extends Player {

    public Dealer(Strategy strategy) {
        super( strategy, BigDecimal.ZERO);
    }

    public Card getFaceUpCard() {
        return getActivePlayerHand().getFaceUpCard();
    }

    public void startRound(Hand hand) {
        super.startRound(hand, BigDecimal.ZERO, hand.getFaceUpCard());
    }

    public boolean hasBlackJack() {
        return getActivePlayerHand().isBlackJack();
    }
}
