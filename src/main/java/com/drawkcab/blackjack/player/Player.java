package com.drawkcab.blackjack.player;

import com.drawkcab.blackjack.game.Card;
import com.drawkcab.blackjack.game.Hand;
import com.drawkcab.blackjack.player.strategy.Strategy;
import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in a game of Blackjack.
 *
 * <p>Handles decision-making (through a {@link Strategy}), manages the player's
 * bank, and interacts with per-round hand state via {@link RoundState}.</p>
 */
public class Player {
    // Player State
    private final Strategy strategy;
    private final BigDecimal seedAmount;

    // Game State
    private BigDecimal bank;

    // Round State
    private final RoundState roundState;

    /**
     * Constructs a new Player with a given strategy and starting bank amount.
     *
     * @param strategy   the playing strategy
     * @param seedAmount the initial bank amount
     */
    public Player(Strategy strategy, BigDecimal seedAmount) {
        this.strategy = strategy;
        this.seedAmount = seedAmount;
        this.roundState = new RoundState();
        reset();
    }

    /**
     * Starts a new round for the player with an initial hand and bet.
     *
     * @param hand the starting hand
     * @param minBet the minimum bet for the round
     * @param dealerFaceUpCard the dealer's visible face-up card
     */
    public void startRound(Hand hand, BigDecimal minBet, Card dealerFaceUpCard) {
        roundState.startRound(hand, minBet, dealerFaceUpCard);
        bank = bank.subtract(minBet);
    }

    /**
     * Ends the current round and returns the final states of all hands.
     *
     * @return a list of final hand states for this round
     */
    public List<HandState> endRound() {
        return roundState.endRound();
    }

    /**
     * Determines and returns the player's next move based on the strategy.
     *
     * @return the next {@link Move} for the player
     * @throws IllegalStateException if the round has not been started
     */
    public Move getNextMove() {
        if (!roundState.isRoundStarted()) {
            throw new IllegalStateException("Round not correctly initialized - startRound() " +
                    "must be called before calling getNextMove().");
        }

        // This enforces the game play that when a split happens, its automatically dealt a second
        // card.
        if (getActivePlayerHand().justSplit()) {
            return Move.HIT;
        }

        return strategy.getNextMove(getActivePlayerHand(), roundState.getDealerFaceUpCard(), bank);
    }

    /** Resets the player to their original state. */
    public void reset() {
        bank = seedAmount;
        roundState.reset();
    }


    /** Adds a card to the active hand (hit action). */
    public void hit(Card card) {
        roundState.hit(card);
    }

    /** Splits the active hand into two hands, adjusting the bank accordingly. */
    public void split() {
        HandState splitHand = roundState.split();
        bank = bank.subtract(splitHand.getBetAmount());
        roundState.addSplitHand(splitHand);
    }

    /** Doubles the bet and hits exactly one more card. */
    public void doubleDown(Card card) {
        bank = bank.subtract(getActivePlayerHand().getBetAmount());
        roundState.doubleDown(card);
    }

    /** Surrenders the current hand, forfeiting half the bet. */
    public void surrender() {
        roundState.surrender();
    }

    /** Stands on the current hand, taking no further action. */
    public void stand() {
        roundState.stand();
    }

    /** Pays the player a given amount (e.g., winnings + initial bet). */
    public void pay(BigDecimal amount) {
        bank = bank.add(amount);
    }

    /** @return the current bank balance */
    public BigDecimal getBank() {
        return bank;
    }

    /** @return {@code true} if the player has any unfinished hands */
    public boolean hasUnfinishedHands() {
        return roundState.hasUnfinishedHands();
    }

    HandState getActivePlayerHand() {
        return roundState.getActivePlayerHand();
    }

    /**
     * Manages the per-round state for a player in Blackjack.
     *
     * <p>This includes active hands, the dealer's visible card, and tracking
     * whether a round is currently active.</p>
     */
    private static class RoundState {
        // A player may have multiple active hands during a round due to splits.
        private final List<HandState> handStates;
        private int activeHandIndex;
        private Card dealerFaceUpCard;
        private boolean roundStarted;

        RoundState() {
            handStates = new ArrayList<>();
            reset();
        }

        void startRound(Hand hand, BigDecimal bet, Card dealerFaceUpCard) {
            if (roundStarted) {
                throw new IllegalStateException("Cannot start a new round without finishing the " +
                        "previous round.");
            }

            handStates.add(new HandState(hand, bet));
            activeHandIndex = 0;
            this.dealerFaceUpCard = dealerFaceUpCard;
            roundStarted = true;
        }

        /**
         * Ends the current round and returns the final state of the hands.
         *
         * @return a copy of the player's final hands
         */
        List<HandState> endRound() {
            ImmutableList<HandState> finalHands = ImmutableList.copyOf(handStates);
            reset();
            return finalHands;
        }

        /** Resets the current round state completely. */
        private void reset() {
            handStates.clear();
            activeHandIndex = -1;
            dealerFaceUpCard = null;
            roundStarted = false;
        }

         void hit(Card card) {
            getActivePlayerHand().hit(card);
            advanceHandIfFinished();
        }

        HandState split() {
            return getActivePlayerHand().split();
        }

        void doubleDown(Card card) {
            getActivePlayerHand().doubleDown(card);
            advanceHandIfFinished();
        }


        void surrender() {
            getActivePlayerHand().surrender();
            activeHandIndex++;
        }

        void stand() {
            getActivePlayerHand().stand();
            activeHandIndex++;
        }

        /**
         * Gets the currently active player hand.
         *
         * @return the active {@link HandState}
         * @throws IllegalStateException if no active hand exists (round not started)
         */
        HandState getActivePlayerHand() {
            if (activeHandIndex == -1) {
                throw new IllegalStateException("No active player hand exists. Did you call startRound()?");
            }
            return handStates.get(activeHandIndex);
        }

        void addSplitHand(HandState splitHand) {
            handStates.add(splitHand);
        }

        Card getDealerFaceUpCard() {
            return dealerFaceUpCard;
        }

        boolean isRoundStarted() {
            return roundStarted;
        }

        boolean hasUnfinishedHands() {
            return handStates.stream().anyMatch(playerHand -> !playerHand.isFinished());
        }

        private void advanceHandIfFinished() {
            if(getActivePlayerHand().isFinished()) {
                activeHandIndex++;
            }
        }
    }
}
