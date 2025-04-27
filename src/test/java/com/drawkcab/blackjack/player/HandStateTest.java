package com.drawkcab.blackjack.player;

import com.drawkcab.blackjack.game.Card;
import com.drawkcab.blackjack.game.Hand;
import com.drawkcab.blackjack.game.exception.InvalidSplitException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HandStateTest {
    private static final BigDecimal BET = BigDecimal.ONE;
    private static final List<Card> SEVENTEEN = List.of(Card.TEN, Card.SEVEN);
    private static final List<Card> SEVEN = List.of(Card.THREE, Card.FOUR);
    private static final List<Card> SOFT_SEVENTEEN = List.of(Card.ACE, Card.SIX);
    private static final List<Card> TWO_EIGHTS = List.of(Card.EIGHT, Card.EIGHT);
    private static final List<Card> ACE_TEN = List.of(Card.ACE, Card.TEN);

    @Test
    void getTotalValue() {
        HandState handState = new HandState(new Hand(SEVEN), BET);

        assertThat(handState.getTotalValue()).isEqualTo(7);
    }

    @Test
    void getBetAmount() {
        HandState handState = new HandState(new Hand(SEVEN), BET);

        assertThat(handState.getBetAmount()).isEqualTo(BET);
    }

    @Test
    void doubleDown() {
        HandState handState = new HandState(new Hand(SEVEN), BigDecimal.ONE);

        handState.doubleDown(Card.TEN);

        assertThat(handState.getTotalValue()).isEqualTo(17);
        assertThat(handState.getBetAmount()).isEqualTo(BigDecimal.TWO);
        assertThat(handState.isFinished()).isTrue();
    }

    @Test
    void doubleDown_ifAlreadyFinishedHand_throws() {
        HandState handState = new HandState(new Hand(SEVEN), BET);
        handState.stand();

        assertThrows(IllegalStateException.class, () -> handState.doubleDown(Card.TEN));
    }

    @Test
    void doubleDown_ifMoveAlreadyMade_throws() {
        HandState handState = new HandState(new Hand(SEVEN), BET);
        handState.hit(Card.TWO);

        assertThrows(IllegalStateException.class, () -> handState.doubleDown(Card.TEN));
    }

    @Test
    void split_splittable_twoHandsOneCardEachSameBet() {
        HandState splittableHand = new HandState(new Hand(TWO_EIGHTS), BET);

        HandState split = splittableHand.split();

        assertThat(splittableHand.getTotalValue()).isEqualTo(8);
        assertThat(split.getTotalValue()).isEqualTo(8);

        assertThat(splittableHand.getBetAmount()).isEqualTo(BET);
        assertThat(split.getBetAmount()).isEqualTo(BET);
    }

    @Test
    void split_notSplittable_throws() {
        HandState handState = new HandState(new Hand(SEVEN), BET);

        assertThrows(InvalidSplitException.class, handState::split);
    }

    @Test
    void split_ifAlreadyFinishedHand_throws() {
        HandState handState = new HandState(new Hand(TWO_EIGHTS), BET);
        handState.stand();

        assertThrows(IllegalStateException.class, handState::split);
    }

    @Test
    void stand() {
        HandState handState = new HandState(new Hand(SEVEN), BET);

        handState.stand();

        assertThat(handState.getTotalValue()).isEqualTo(7);
        assertThat(handState.isFinished()).isTrue();
    }

    @Test
    void stand_ifAlreadyFinishedHand_throws() {
        HandState handState = new HandState(new Hand(SEVEN), BET);
        handState.stand();

        assertThrows(IllegalStateException.class, handState::stand);
    }

    @Test
    void surrender_surrenderable_surrendersFinishesTotalZero() {
        HandState handState = new HandState(new Hand(SEVEN), BET);

        handState.surrender();

        assertThat(handState.isSurrendered()).isTrue();
        assertThat(handState.isFinished()).isTrue();
        assertThat(handState.getTotalValue()).isEqualTo(0);
    }

    @Test
    void surrender_ifAlreadyFinishedHand_throws() {
        HandState handState = new HandState(new Hand(SEVEN), BET);
        handState.stand();

        assertThrows(IllegalStateException.class, handState::surrender);
    }

    @Test
    void surrender_ifMoveAlreadyMade_throws() {
        HandState handState = new HandState(new Hand(SEVEN), BET);
        handState.hit(Card.TEN);

        assertThrows(IllegalStateException.class, handState::surrender);
    }

    @Test
    void hit_addsCard() {
        HandState handState = new HandState(new Hand(SEVEN), BET);

        handState.hit(Card.TEN);

        assertThat(handState.getTotalValue()).isEqualTo(17);
    }

    @Test
    void hit_ifAlreadyFinishedHand_throws() {
        HandState handState = new HandState(new Hand(SEVEN), BET);
        handState.stand();

        assertThrows(IllegalStateException.class, () -> handState.hit(Card.TEN));
    }

    @Test
    void isSoft_softHand_true() {
        HandState softHand = new HandState(new Hand(SOFT_SEVENTEEN), BET);

        assertThat(softHand.isSoft()).isTrue();
    }

    @Test
    void isSoft_hardHand_isFalse() {
        HandState hardHand = new HandState(new Hand(SEVEN), BET);

        assertThat(hardHand.isSoft()).isFalse();
    }

    @Test
    void isSurrendered_surrendered_true() {
        HandState handState = new HandState(new Hand(SEVEN), BET);

        handState.surrender();

        assertThat(handState.isSurrendered()).isTrue();
    }

    @Test
    void isSurrendered_notSurrendered_false() {
        HandState handState = new HandState(new Hand(SEVEN), BET);

        assertThat(handState.isSurrendered()).isFalse();
    }

    @Test
    void isBust_overTwentyOne_true() {
        HandState handState = new HandState(new Hand(SEVENTEEN), BET);

        handState.hit(Card.TEN);

        assertThat(handState.isBust()).isTrue();
    }

    @Test
    void isBust_underTwentyOne_false() {
        HandState handState = new HandState(new Hand(SEVEN), BET);

        assertThat(handState.isBust()).isFalse();
    }

    @Test
    void isBlackJack_blackJack_true() {
        HandState handState = new HandState(new Hand(ACE_TEN), BET);

        assertThat(handState.isBlackJack()).isTrue();
    }

    @Test
    void isBlackJack_notBlackJack_false() {
        HandState handState = new HandState(new Hand(SEVEN), BET);

        assertThat(handState.isBlackJack()).isFalse();
    }

    @Test
    void canSplit_splittable_true() {
        HandState handState = new HandState(new Hand(TWO_EIGHTS), BET);

        assertThat(handState.canSplit()).isTrue();
    }

    @Test
    void canSplit_notSplittable_false() {
        HandState handState = new HandState(new Hand(SEVEN), BET);

        assertThat(handState.canSplit()).isFalse();
    }

    @Test
    void justSplit_afterSplit_true() {
        HandState handState = new HandState(new Hand(TWO_EIGHTS), BET);

        HandState split = handState.split();

        assertThat(handState.justSplit()).isTrue();
        assertThat(split.justSplit()).isTrue();
    }

    @Test
    void justSplit_twoCards_false() {
        HandState handState = new HandState(new Hand(SEVENTEEN), BET);

        assertThat(handState.justSplit()).isFalse();
    }

    @Test
    void justSplit_formerlySplit_false() {
        HandState handState = new HandState(new Hand(TWO_EIGHTS), BET);

        handState.split();
        handState.hit(Card.TEN);

        assertThat(handState.justSplit()).isFalse();
    }

    @Test
    void isInitialHand_beforeHit_true() {
        HandState handState = new HandState(new Hand(SEVENTEEN), BET);

        assertThat(handState.isInitialHand()).isTrue();
    }

    @Test
    void isInitialHand_afterHit_false() {
        HandState handState = new HandState(new Hand(SEVEN), BET);

        handState.hit(Card.TEN);

        assertThat(handState.isInitialHand()).isFalse();

    }

    @Test
    void getSplitCard_splitable_returnsCard() {
        HandState handState = new HandState(new Hand(TWO_EIGHTS), BET);

        assertThat(handState.getSplitCard()).isEqualTo(Card.EIGHT);
    }

    @Test
    void getSplitCard_notSplittable_throws() {
        HandState handState = new HandState(new Hand(SEVENTEEN), BET);

        assertThrows(InvalidSplitException.class, handState::getSplitCard);
    }

    @Test
    void getSplitCard_splitable_doesNotModifyHand() {
        HandState handState = new HandState(new Hand(TWO_EIGHTS), BET);

        handState.getSplitCard();

        assertThat(handState.getTotalValue()).isEqualTo(16);
    }

    @Test
    void getFaceUpCard() {
        HandState handState = new HandState(new Hand(ACE_TEN), BET);

        assertThat(handState.getFaceUpCard()).isEqualTo(Card.ACE);
    }
}