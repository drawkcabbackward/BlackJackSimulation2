package com.drawkcab.blackjack.game;

import com.drawkcab.blackjack.game.exception.InvalidSplitException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class HandTest {

    @Test
    void addCard_newCard_addedToTotal() {
        Hand hand = new Hand(List.of(Card.JACK, Card.FIVE));

        hand.addCard(Card.SIX);

        assertThat(hand.totalValue()).isEqualTo(21);
    }

    @Test
    void justSplit_split_true() {
        Hand hand = new Hand(List.of(Card.TEN, Card.TEN));

        Hand splitHand = hand.split();

        assertThat(hand.justSplit()).isTrue();
        assertThat(splitHand.justSplit()).isTrue();
    }

    @Test
    void justSplit_notSplit_false() {
        Hand hand = new Hand(List.of(Card.TEN, Card.TEN));

        assertThat(hand.justSplit()).isFalse();
    }

    @Test
    void justSplit_splitPostHit_false() {
        Hand hand = new Hand(List.of(Card.TEN, Card.TEN));

        hand.split();
        hand.addCard(Card.ACE);

        assertThat(hand.justSplit()).isFalse();
    }

    @Test
    void isInitialHand_twoCards_true() {
        Hand hand = new Hand(List.of(Card.TEN, Card.ACE));

        assertThat(hand.isInitialHand()).isTrue();
    }

    @Test
    void isInitialHand_threeCards_false() {
        Hand hand = new Hand(List.of(Card.TEN, Card.TWO, Card.THREE));

        assertThat(hand.isInitialHand()).isFalse();
    }

    @Test
    void isInitialHand_postSplit_false() {
        Hand hand = new Hand(List.of(Card.TEN, Card.TEN));

        Hand splitHand = hand.split();

        assertThat(hand.isInitialHand()).isFalse();
        assertThat(splitHand.isInitialHand()).isFalse();
    }

    @Test
    void getFaceUpCard_returnsFirstCard() {
        Hand hand = new Hand(List.of(Card.TEN, Card.ACE));

        assertThat(hand.getFaceUpCard()).isEqualTo(Card.TEN);
    }

    @Test
    void getFaceUpCard_emptyHand_throws() {
        Hand hand = new Hand(List.of());

        assertThrows(IllegalStateException.class, hand::getFaceUpCard);
    }

    @Test
    void isBlackJack_blackJack_true() {
        Hand hand = new Hand(List.of(Card.TEN, Card.ACE));

        assertThat(hand.isBlackJack()).isTrue();
    }

    @Test
    void isBlackJack_notBlackJack_false() {
        Hand hand = new Hand(List.of(Card.FIVE, Card.ACE));

        assertThat(hand.isBlackJack()).isFalse();
    }

    @Test
    void isSoft_soft_true() {
        Hand hand = new Hand(List.of(Card.FIVE, Card.ACE));

        assertThat(hand.isSoft()).isTrue();
    }

    @Test
    void isSoft_hard_false() {
        Hand hand = new Hand(List.of(Card.FIVE, Card.TEN));

        assertThat(hand.isSoft()).isFalse();
    }

    @Test
    void isBust_over21_true() {
        Hand hand = new Hand(List.of(Card.JACK, Card.TEN, Card.FIVE));

        assertThat(hand.isBust()).isTrue();
    }

    @Test
    void isBust_under21_true() {
        Hand hand = new Hand(List.of(Card.JACK, Card.FIVE));

        assertThat(hand.isBust()).isFalse();
    }

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