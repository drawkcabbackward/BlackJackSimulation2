package com.drawkcab.blackjack.player.strategy;

import com.drawkcab.blackjack.game.Card;
import com.drawkcab.blackjack.player.Move;
import com.drawkcab.blackjack.player.HandState;

import java.math.BigDecimal;

public class BookPlayerStrategy implements Strategy {
    @Override
    public Move getNextMove(HandState hand, Card dealerFaceUpCard, BigDecimal bank) {
        int currentValue = hand.getTotalValue();
        int dealerValue = dealerFaceUpCard.getMaxValue();

        // Order of these checks matter. There are hands that we should split over double and
        // that is implicitly baked into the ordering.
        if (hand.isBlackJack()) {
            return Move.STAND;
        }

        if (hand.isInitialHand() && shouldSurrender(currentValue, hand.isSoft(), dealerValue)) {
            return Move.SURRENDER;
        }

        if (canSplit(hand, bank) &&  shouldSplit(hand.getSplitCard(),
                dealerValue)) {
            return Move.SPLIT;
        }

        if (canDouble(hand, bank) && shouldDoubleDown(currentValue, hand.isSoft(),
                dealerValue)) {
            return Move.DOUBLE_DOWN;
        }

        if (shouldStand(currentValue, hand.isSoft(), dealerValue)) {
            return Move.STAND;
        }

        return Move.HIT;
    }

    private boolean canSplit(HandState hand, BigDecimal bank) {
        return hand.canSplit() && canAffordBet(hand, bank);
    }

    private boolean canDouble(HandState hand, BigDecimal bank) {
        return hand.isInitialHand() && canAffordBet(hand, bank);
    }

    private boolean canAffordBet(HandState hand, BigDecimal bank) {
        return bank.compareTo(hand.getBetAmount()) >= 0;
    }

    private boolean shouldSurrender(int currentValue, boolean isSoft, int dealerValue) {
        if (isSoft) {
            return false;
        }

        return switch (currentValue) {
            case 15 -> dealerValue == 10;
            case 16 -> dealerValue >= 9 && dealerValue <= 11;
            default -> false;
        };
    }

    private boolean shouldSplit(Card splitCard, int dealerValue) {
        return switch (splitCard) {
            case ACE, EIGHT -> true;
            case SEVEN, THREE, TWO -> dealerValue >= 2 && dealerValue <= 7;
            case NINE ->
                    dealerValue >= 2 && dealerValue <= 6 || dealerValue == 8 || dealerValue == 9;
            case SIX -> dealerValue >= 2 && dealerValue <= 6;
            case FOUR -> dealerValue == 5 || dealerValue == 6;
            default -> false;
        };
    }

    private boolean shouldDoubleDown(int currentValue, boolean isSoft, int dealerValue) {
        if (isSoft) {
            return switch (currentValue) {
                case 13, 14 -> dealerValue == 5 || dealerValue == 6;
                case 15, 16 -> dealerValue >= 4 && dealerValue <= 6;
                case 17, 18 -> dealerValue >= 3 && dealerValue <= 6;
                default -> false;
            };
        }

        return switch (currentValue) {
            case 9 -> dealerValue >= 3 && dealerValue <= 6;
            case 10 -> dealerValue <= 9;
            case 11 -> dealerValue <= 10;
            default -> false;
        };
    }

    private boolean shouldStand(int currentValue, boolean isSoft, int dealerValue) {
        if (isSoft) {
            return switch (currentValue) {
                case 18 -> dealerValue <= 8;
                case 19, 20 -> true;
                default-> false;
            };
        }

        return switch (currentValue) {
            case 12 -> dealerValue >= 4 && dealerValue <= 6;
            case 13, 14, 15, 16 -> dealerValue <=6;
            case 17, 18, 19, 20, 21 -> true;
            default -> false;
        };
    }
}
