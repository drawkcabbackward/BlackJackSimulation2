package com.drawkcab.blackjack.simulation;

import com.drawkcab.blackjack.game.Deck;

import com.drawkcab.blackjack.game.Hand;
import com.drawkcab.blackjack.game.HandEvaluator;
import com.drawkcab.blackjack.game.HandOutcome;

import com.drawkcab.blackjack.player.*;
import com.drawkcab.blackjack.simulation.modules.MinBet;
import com.google.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Simulates a game of Blackjack between a player and a dealer.
 *
 * <p>This class manages round setup, player and dealer turns, hand evaluations, and payouts.
 * The simulation runs until the player can no longer afford the minimum bet.</p>
 */
public class GameSimulator {
    private final Player player;
    private final Dealer dealer;
    private final Deck deck;
    private final BigDecimal minBet;
    private final RoundSimulator roundSimulator;

    @Inject
    public GameSimulator(RoundSimulator roundSimulator,
                         Player player,
                         Dealer dealer,
                         Deck deck,
                         @MinBet BigDecimal minBet) {
        this.roundSimulator = roundSimulator;
        this.player = player;
        this.dealer = dealer;
        this.deck = deck;
        this.minBet = minBet;
    }

    /**
     * Simulates multiple rounds of Blackjack until the player cannot afford the minimum bet.
     *
     * @return the number of rounds successfully played
     */
    public long playGame() {
        long numberOfRoundsPlayed = 0;

        while (playerHasMinBet()) {
            // Shuffle at the start of each round to ensure we don't run out of cards.
            // TODO(brandon): investigate if this is a bottleneck in our simulation and if so
            // only do this if we are getting close to the end of the deck.
            deck.shuffle();

            roundSimulator.playRound(player, dealer, deck, minBet);
            numberOfRoundsPlayed++;
        }

        reset();
        return numberOfRoundsPlayed;
    }

    private void reset() {
        player.reset();
        dealer.reset();
    }

    private boolean playerHasMinBet() {
        return player.getBank().compareTo(minBet) >= 0;
    }
}
