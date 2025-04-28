package com.drawkcab.blackjack.simulation;

import com.drawkcab.blackjack.game.*;
import com.drawkcab.blackjack.player.*;
import com.google.common.flogger.FluentLogger;
import com.google.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


/**
 * Simulates a **single round** of Blackjack for a player and dealer.
 */
public class RoundSimulator {
    private static final FluentLogger flogger = FluentLogger.forEnclosingClass();

    private final HandEvaluator handEvaluator;

    @Inject
    public RoundSimulator(HandEvaluator handEvaluator) {
        this.handEvaluator = handEvaluator;
    }

    /**
     * Plays a full round of Blackjack.
     *
     * @param player the player
     * @param dealer the dealer
     * @param deck   the deck to deal from
     * @param minBet the minimum bet amount
     */
    public void playRound(Player player, Dealer dealer, Deck deck, BigDecimal minBet) {
        initializeRound(player, dealer, deck, minBet);

        if (!dealer.hasBlackJack()) {
            simulatePlayer(player, deck);
            simulatePlayer(dealer, deck);
        }

        resolveRound(player, dealer);
    }

    private void initializeRound(Player player, Dealer dealer, Deck deck, BigDecimal minBet) {
        dealer.startRound(getInitialHand(deck));
        player.startRound(getInitialHand(deck), minBet, dealer.getFaceUpCard());
        flogger.atInfo().log("Initializing round.");
    }

    private Hand getInitialHand(Deck deck) {
        return new Hand(List.of(deck.getNextCard(), deck.getNextCard()));
    }

    private void simulatePlayer(Player player, Deck deck) {
        while (player.hasUnfinishedHands()) {
            Move move = player.getNextMove();
            flogger.atInfo().log("Move = [%s]", move);

            switch (move) {
                case HIT -> player.hit(deck.getNextCard());
                case STAND -> player.stand();
                case DOUBLE_DOWN -> player.doubleDown(deck.getNextCard());
                case SPLIT -> player.split();
                case SURRENDER -> player.surrender();
            }
        }
    }

    private void resolveRound(Player player, Dealer dealer) {
        List<HandState> playerHands = player.endRound();
        HandState dealerHand = dealer.endRound().getFirst();

        BigDecimal roundStartBank = player.getBank();
        for (HandState playerHand : playerHands) {
            HandOutcome outcome = handEvaluator.getOutcome(playerHand, dealerHand);
            payPlayer(player, outcome, playerHand.getBetAmount());
        }

        flogger.atInfo().log("Over the round the player's bank changed by [%s]",
                player.getBank().subtract(roundStartBank));
    }

    private void payPlayer(Player player, HandOutcome outcome, BigDecimal betAmount) {
        // By default, a player is debited a bet when they create a hand, so even in situations
        // where there are no winnings, we still need to "pay" them their original bet as long as
        // they didn't lose.
        switch (outcome) {
            case WIN -> player.pay(betAmount.multiply(BigDecimal.TWO));
            case BLACKJACK_WIN -> player.pay(betAmount.multiply(BigDecimal.valueOf(2.5)));
            case SURRENDER -> player.pay(betAmount.divide(BigDecimal.valueOf(2),
                    RoundingMode.HALF_DOWN));
            case PUSH -> player.pay(betAmount); // original
            // LOSS doesn't require paying anything
            default -> {}
        }
    }
}