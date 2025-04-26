package com.drawkcab.blackjack.game;

import com.drawkcab.blackjack.game.exception.InvalidSplitException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class HandTest {

    @Nested
    class TotalValue {
        @Test
        void simpleNumbers() {
            Hand hand = new Hand(List.of(Card.TWO, Card.THREE, Card.FOUR));

            assertThat(hand.totalValue()).isEqualTo(9);
        }

        @Test
        void noAcesWithFaceCards() {
            Hand hand = new Hand(List.of(Card.TWO, Card.THREE, Card.KING));

            assertThat(hand.totalValue()).isEqualTo(15);
        }

        @Test
        void multipleFacesOverTwentyOne_exceedsTwentyOne() {
            Hand hand = new Hand(List.of(Card.JACK, Card.QUEEN, Card.KING));

            assertThat(hand.totalValue()).isEqualTo(30);
        }

        @Test
        void aceWithPromotionRoom_promotes() {
            Hand hand = new Hand(List.of(Card.TWO, Card.ACE));

            assertThat(hand.totalValue()).isEqualTo(13);
        }

        @Test
        void aceWithoutPromotionRoom_doesNotPromote() {
            Hand hand = new Hand(List.of(Card.THREE, Card.TEN, Card.ACE));

            assertThat(hand.totalValue()).isEqualTo(14);
        }

        @Test
        void multipleAcesWithRoom_onePromotes() {
            Hand hand = new Hand(List.of(Card.ACE, Card.NINE, Card.ACE));

            assertThat(hand.totalValue()).isEqualTo(21);
        }
    }

    @Nested
    class AddCard {
        @Test
        void newCard_addedToTotal() {
            Hand hand = new Hand(List.of(Card.JACK, Card.FIVE));

            hand.addCard(Card.SIX);

            assertThat(hand.totalValue()).isEqualTo(21);
        }
    }


    @Nested
    class CanSplit {
        @Test
        void sameCard_isTrue() {
            Hand hand = new Hand(List.of(Card.EIGHT, Card.EIGHT));

            assertThat(hand.canSplit()).isTrue();
        }

        @Test
        void differentCardSameValue_isTrue() {
            Hand hand = new Hand(List.of(Card.TEN, Card.KING));

            assertThat(hand.canSplit()).isTrue();
        }


        @Test
        void differentNumberCard_isFalse() {
            Hand hand = new Hand(List.of(Card.EIGHT, Card.SEVEN));

            assertThat(hand.canSplit()).isFalse();
        }

        @Test
        void threeSameCard_isFalse() {
            Hand hand = new Hand(List.of(Card.EIGHT, Card.EIGHT, Card.EIGHT));

            assertThat(hand.canSplit()).isFalse();
        }
    }

    @Nested
    class Split {
        @Test
        void splittable_splits() {
            Hand hand = new Hand(List.of(Card.EIGHT, Card.EIGHT));

            Hand splitHand = hand.split();

            assertThat(hand.totalValue()).isEqualTo(8);
            assertThat(splitHand.totalValue()).isEqualTo(8);
        }

        @Test
        void differentCards_throws() {
            Hand hand = new Hand(List.of(Card.EIGHT, Card.SEVEN));

            InvalidSplitException thrown = assertThrows(InvalidSplitException.class, hand::split);

            assertThat(thrown).hasMessageThat().contains("Hand is not eligible to be split");
        }

        @Test
        void moreThanTwoCards_throws() {
            Hand hand = new Hand(List.of(Card.EIGHT, Card.EIGHT, Card.EIGHT));

            InvalidSplitException thrown = assertThrows(InvalidSplitException.class, hand::split);

            assertThat(thrown).hasMessageThat().contains("Hand is not eligible to be split");
        }
    }




}