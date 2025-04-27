package com.drawkcab.blackjack.player;

import com.drawkcab.blackjack.game.Card;
import com.drawkcab.blackjack.game.Hand;
import com.drawkcab.blackjack.player.strategy.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;

class DealerTest {
    private Dealer dealer;

    private static final Hand SEVENTEEN = new Hand(List.of(Card.TEN, Card.SEVEN));
    private static final Hand BLACK_JACK = new Hand(List.of(Card.TEN, Card.ACE));


    @BeforeEach
    void setup() {
        dealer = new Dealer(mock(Strategy.class));
    }

    @Test
    void startRound_initializesHand() {
        dealer.startRound(SEVENTEEN);

        assertThat(dealer.getActivePlayerHand().getTotalValue()).isEqualTo(17);
    }

    @Test
    void getFaceUpCard_returnsFirstCardInHand() {
        dealer.startRound(SEVENTEEN);

        assertThat(dealer.getFaceUpCard()).isEqualTo(Card.TEN);
    }

    @Test
    void hasBlackJack_blackJack_true() {
        dealer.startRound(BLACK_JACK);

        assertThat(dealer.hasBlackJack()).isTrue();
    }

    @Test
    void hasBlackJack_notBlackJack_false() {
        dealer.startRound(SEVENTEEN);

        assertThat(dealer.hasBlackJack()).isFalse();
    }
}