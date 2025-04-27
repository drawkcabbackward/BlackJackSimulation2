package com.drawkcab.blackjack.game;

import com.drawkcab.blackjack.player.HandState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

class HandEvaluatorTest {

    private static final Hand BLACK_JACK = new Hand(List.of(Card.ACE, Card.KING));
    private static final Hand EIGHTEEN = new Hand(List.of(Card.TEN, Card.EIGHT));
    private static final Hand SEVENTEEN = new Hand(List.of(Card.TEN, Card.SEVEN));
    private static final Hand TWENTY_TWO = new Hand(List.of(Card.TEN, Card.TEN, Card.TWO));
    private HandEvaluator handEvaluator;

    @BeforeEach
    public void setUp() {
        handEvaluator = new HandEvaluator();
    }

    @Test
    void getOutcome_winningHand_win() {
        HandState dealerHand = new HandState(SEVENTEEN, BigDecimal.ZERO);
        HandState handState = new HandState(EIGHTEEN, BigDecimal.TWO);

        HandOutcome outcome = handEvaluator.getOutcome(handState, dealerHand);

        assertThat(outcome).isEqualTo(HandOutcome.WIN);
    }

    @Test
    void getOutcome_losingHand_loss() {
        HandState dealerHand = new HandState(EIGHTEEN, BigDecimal.ZERO);
        HandState handState = new HandState(SEVENTEEN, BigDecimal.TWO);

        HandOutcome outcome = handEvaluator.getOutcome(handState, dealerHand);

        assertThat(outcome).isEqualTo(HandOutcome.LOSS);
    }

    @Test
    void getOutcome_matchingValue_push() {
        HandState dealerHand = new HandState(EIGHTEEN, BigDecimal.ZERO);
        HandState handState = new HandState(EIGHTEEN, BigDecimal.TWO);

        HandOutcome outcome = handEvaluator.getOutcome(handState, dealerHand);

        assertThat(outcome).isEqualTo(HandOutcome.PUSH);
    }

    @Test
    void getOutcome_playerBlackJackVerseNotBlackJack_blackjackWin() {
        HandState dealerHand = new HandState(SEVENTEEN, BigDecimal.ZERO);
        HandState handState = new HandState(BLACK_JACK, BigDecimal.TWO);

        HandOutcome outcome = handEvaluator.getOutcome(handState, dealerHand);

        assertThat(outcome).isEqualTo(HandOutcome.BLACKJACK_WIN);
    }

    @Test
    void getOutcome_playerBust_loss() {
        HandState dealerHand = new HandState(EIGHTEEN, BigDecimal.ZERO);
        HandState handState = new HandState(TWENTY_TWO, BigDecimal.TWO);

        HandOutcome outcome = handEvaluator.getOutcome(handState, dealerHand);

        assertThat(outcome).isEqualTo(HandOutcome.LOSS);
    }

    @Test
    void getOutcome_bustVsBust_loss() {
        HandState dealerHand = new HandState(TWENTY_TWO, BigDecimal.ZERO);
        HandState handState = new HandState(TWENTY_TWO, BigDecimal.TWO);

        HandOutcome outcome = handEvaluator.getOutcome(handState, dealerHand);

        assertThat(outcome).isEqualTo(HandOutcome.LOSS);
    }

    @Test
    void getOutcome_lowVsBust_win() {
        HandState dealerHand = new HandState(TWENTY_TWO, BigDecimal.ZERO);
        HandState handState = new HandState(SEVENTEEN, BigDecimal.TWO);

        HandOutcome outcome = handEvaluator.getOutcome(handState, dealerHand);

        assertThat(outcome).isEqualTo(HandOutcome.WIN);
    }

    @Test
    void getOutcome_surrenderVsNonBlackJack_surrender() {
        HandState dealerHand = new HandState(EIGHTEEN, BigDecimal.ZERO);
        HandState handState = new HandState(EIGHTEEN, BigDecimal.TWO);
        handState.surrender();

        HandOutcome outcome = handEvaluator.getOutcome(handState, dealerHand);

        assertThat(outcome).isEqualTo(HandOutcome.SURRENDER);
    }

    @Test
    void getOutcome_surrenderVsBlackJack_loss() {
        HandState dealerHand = new HandState(BLACK_JACK, BigDecimal.ZERO);
        HandState handState = new HandState(EIGHTEEN, BigDecimal.TWO);
        handState.surrender();

        HandOutcome outcome = handEvaluator.getOutcome(handState, dealerHand);

        assertThat(outcome).isEqualTo(HandOutcome.LOSS);
    }

    @Test
    void getOutcome_blackJackVsBlackJack_push() {
        HandState dealerHand = new HandState(BLACK_JACK, BigDecimal.ZERO);
        HandState handState = new HandState(BLACK_JACK, BigDecimal.TWO);

        HandOutcome outcome = handEvaluator.getOutcome(handState, dealerHand);

        assertThat(outcome).isEqualTo(HandOutcome.PUSH);
    }
}