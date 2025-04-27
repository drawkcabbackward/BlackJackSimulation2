package com.drawkcab.blackjack.player.strategy;

import com.drawkcab.blackjack.game.Card;
import com.drawkcab.blackjack.game.Hand;
import com.drawkcab.blackjack.player.HandState;
import com.drawkcab.blackjack.player.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

class DealerStrategyTest {
    private static final Hand SEVENTEEN = new Hand(List.of(Card.TEN, Card.SEVEN));
    private static final Hand SIXTEEN = new Hand(List.of(Card.TEN, Card.SIX));
    private static final Hand SOFT_SEVENTEEN = new Hand(List.of(Card.ACE, Card.SIX));

    // The dealer makes no decisions based on their face up card.
    private static final Card IGNORED_DEALER_CARD = Card.TWO;

    // Dealer strategy doesn't take into account their bank as they neither wager, nor have the
    // ability to raise the stakes by splitting or doubling down.
    private static final BigDecimal IGNORED_BANK = BigDecimal.ZERO;

    DealerStrategy dealerStrategy;

    @BeforeEach
    void setup() {
        dealerStrategy = new DealerStrategy();
    }

    @Test
    void getNextMove_underSeventeen_hits() {
        HandState handState = new HandState(SIXTEEN, BigDecimal.ZERO);

        Move next = dealerStrategy.getNextMove(handState, IGNORED_DEALER_CARD, IGNORED_BANK);

        assertThat(next).isEqualTo(Move.HIT);
    }

    @Test
    void getNextMove_seventeen_stays() {
        HandState handState = new HandState(SEVENTEEN, BigDecimal.ZERO);

        Move next = dealerStrategy.getNextMove(handState, IGNORED_DEALER_CARD, IGNORED_BANK);

        assertThat(next).isEqualTo(Move.STAND);
    }

    @Test
    void getNextMove_softSeventeen_stays() {
        HandState handState = new HandState(SOFT_SEVENTEEN, BigDecimal.ZERO);

        Move next = dealerStrategy.getNextMove(handState, IGNORED_DEALER_CARD, IGNORED_BANK);

        assertThat(next).isEqualTo(Move.STAND);
    }
}