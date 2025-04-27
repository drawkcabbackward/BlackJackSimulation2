package com.drawkcab.blackjack.player;

import com.drawkcab.blackjack.game.Card;
import com.drawkcab.blackjack.game.Hand;
import com.drawkcab.blackjack.player.strategy.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PlayerTest {

    private static final BigDecimal SEED_AMOUNT = BigDecimal.valueOf(1000);
    private static final BigDecimal BET = BigDecimal.TEN;
    private static final BigDecimal SEED_AMOUNT_MINUS_BET = SEED_AMOUNT.subtract(BET);
    private static final BigDecimal SEED_AMOUNT_MINUS_TWO_BET =
            SEED_AMOUNT.subtract(BET.multiply(BigDecimal.TWO));

    private static final List<Card> SEVENTEEN = List.of(Card.TEN, Card.SEVEN);
    private static final List<Card> TWO_EIGHTS = List.of(Card.EIGHT, Card.EIGHT);
    private static final Card DEALER_FACE_UP = Card.TEN;

    private Strategy mockStrategy;
    private Player player;
    private Hand freshHand;


    @BeforeEach
    void setup() {
        mockStrategy = mock(Strategy.class);
        player = new Player(mockStrategy, SEED_AMOUNT);
        freshHand = new Hand(SEVENTEEN);
    }

    @Test
    void startRound_initializesHandAndDeductsBet() {
        player.startRound(freshHand, BET, DEALER_FACE_UP);

        assertThat(player.getBank()).isEqualTo(SEED_AMOUNT_MINUS_BET);
        assertThat(player.getActivePlayerHand().getTotalValue()).isEqualTo(17);
    }

    @Test
    void startRound_twice_throwsException() {
        player.startRound(freshHand, BET, DEALER_FACE_UP);

        assertThrows(IllegalStateException.class,
                () ->  player.startRound(freshHand, BET, DEALER_FACE_UP));
    }

    @Test
    void getNextMove_usesStrategy_whenNotJustSplit() {
        when(mockStrategy.getNextMove(any(), eq(DEALER_FACE_UP), eq(SEED_AMOUNT_MINUS_BET)))
                .thenReturn(Move.STAND);
        player.startRound(freshHand, BET, DEALER_FACE_UP);

        Move move = player.getNextMove();

        assertThat(move).isEqualTo(Move.STAND);
    }

    @Test
    void getNextMove_whenJustSplit_forcesHit() {
        when(mockStrategy.getNextMove(any(), any(), any()))
                .thenReturn(Move.STAND);
        player.startRound(new Hand(TWO_EIGHTS), BET, DEALER_FACE_UP);

        player.split(); // Now both hands have one card
        // Now the active hand should "justSplit"

        assertThat(player.getNextMove()).isEqualTo(Move.HIT);
    }

    @Test
    void hit_addsCardToHand() {
        player.startRound(freshHand, BET, DEALER_FACE_UP);

        player.hit(Card.FIVE);

        List<HandState> handStates = player.endRound();
        assertThat(handStates).hasSize(1);
        assertThat(handStates.getFirst().getTotalValue()).isEqualTo(22);
    }

    @Test
    void doubleDown_addsCard_doublesBetAddsCardAndFinishesHand() {
        player.startRound(freshHand, BET, DEALER_FACE_UP);

        player.doubleDown(Card.FIVE);

        assertThat(player.hasUnfinishedHands()).isFalse();
        assertThat(player.getBank()).isEqualTo(SEED_AMOUNT_MINUS_TWO_BET);

        List<HandState> handStates = player.endRound();
        assertThat(handStates).hasSize(1);
        assertThat(handStates.getFirst().getTotalValue()).isEqualTo(22);
    }

    @Test
    void split_createsNewHand_reducesBank() {
        player.startRound(new Hand(List.of(Card.EIGHT, Card.EIGHT)), BET, DEALER_FACE_UP);

        player.split();

        assertThat(player.getBank()).isEqualTo(SEED_AMOUNT_MINUS_TWO_BET);
        List<HandState> handStates = player.endRound();
        assertThat(handStates).hasSize(2);
    }

    @Test
    void surrender_marksHandSurrendered() {
        player.startRound(freshHand, BET, DEALER_FACE_UP);

        player.surrender();

        List<HandState> handStates = player.endRound();
        assertThat(handStates).hasSize(1);
        assertThat(handStates.getFirst().isSurrendered()).isTrue();
    }

    @Test
    void stand_finishesHand() {
        player.startRound(freshHand, BET, DEALER_FACE_UP);

        player.stand();

        List<HandState> handStates = player.endRound();
        assertThat(handStates).hasSize(1);
        assertThat(handStates.getFirst().isFinished()).isTrue();
    }

    @Test
    void pay_increasesBank() {
        player.pay(BigDecimal.valueOf(50));

        assertThat(player.getBank()).isEqualTo(SEED_AMOUNT.add(BigDecimal.valueOf(50)));
    }

    @Test
    void endRound_returnsFinalHandsAndResetsState() {
        player.startRound(freshHand, BET, DEALER_FACE_UP);

        List<HandState> handStates = player.endRound();

        assertThat(handStates).hasSize(1);
        assertThat(handStates.getFirst().getTotalValue()).isEqualTo(17);
        assertThat(player.hasUnfinishedHands()).isFalse();
    }

    @Test
    void getNextMove_withoutStartingRound_throwsException() {
        assertThrows(IllegalStateException.class, () -> player.getNextMove());
    }
}