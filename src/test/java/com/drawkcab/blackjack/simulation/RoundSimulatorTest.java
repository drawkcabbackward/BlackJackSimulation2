package com.drawkcab.blackjack.simulation;

import com.drawkcab.blackjack.game.Card;
import com.drawkcab.blackjack.game.Deck;
import com.drawkcab.blackjack.game.HandEvaluator;
import com.drawkcab.blackjack.game.HandOutcome;
import com.drawkcab.blackjack.player.Dealer;
import com.drawkcab.blackjack.player.Move;
import com.drawkcab.blackjack.player.Player;
import com.drawkcab.blackjack.player.strategy.Strategy;

import com.google.inject.Inject;

import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.google.common.truth.Truth.assertThat;
import static com.google.inject.Guice.createInjector;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoundSimulatorTest {
    private static final BigDecimal STARTING_BANK = BigDecimal.TEN;

    @Bind @Mock HandEvaluator mockHandEvaluator;
    @Mock Strategy playerStrategy;
    @Mock Strategy dealerStrategy;

    @Mock Player mockPlayer;

    @Inject
    private RoundSimulator roundSimulator;

    Player player;
    Dealer dealer;
    Deck deck;

    @BeforeEach
    void setUp() {
        createInjector(BoundFieldModule.of(this)).injectMembers(this);
        player = new Player(playerStrategy, STARTING_BANK);
        dealer = new Dealer(dealerStrategy);
        deck = new Deck(1);

        when(playerStrategy.getNextMove(any(), any(), any())).thenReturn(Move.STAND);
        when(dealerStrategy.getNextMove(any(), any(), any())).thenReturn(Move.STAND);
    }

    @Test
    public void playRound_playerWins_bankIncreases() {
        when(mockHandEvaluator.getOutcome(any(), any())).thenReturn(HandOutcome.WIN);

        roundSimulator.playRound(player, dealer, deck, BigDecimal.ONE);

        assertThat(player.getBank()).isEqualTo(BigDecimal.valueOf(11));
    }

    @Test
    void playRound_blackjackWin_paysOnePointFive() {
        when(mockHandEvaluator.getOutcome(any(), any())).thenReturn(HandOutcome.BLACKJACK_WIN);

        roundSimulator.playRound(player, dealer, deck, BigDecimal.ONE);

        // BLACKJACK_WIN pays 2.5x total (2.5x1 = 2.5)
        assertThat(player.getBank()).isEqualTo(BigDecimal.valueOf(11.5));
    }

    @Test
    void playRound_push_playerGetsBetBack() {
        when(mockHandEvaluator.getOutcome(any(), any())).thenReturn(HandOutcome.PUSH);

        roundSimulator.playRound(player, dealer, deck, BigDecimal.ONE);

        // PUSH means player simply gets their original bet back
        assertThat(player.getBank()).isEqualTo(STARTING_BANK);
    }

    @Test
    void playRound_loss_playerLosesBet() {
        when(mockHandEvaluator.getOutcome(any(), any())).thenReturn(HandOutcome.LOSS);

        roundSimulator.playRound(player, dealer, deck, BigDecimal.ONE);

        // LOSS: player does not get anything back
        BigDecimal expectedBank = STARTING_BANK.subtract(BigDecimal.ONE);
        assertThat(player.getBank()).isEqualTo(expectedBank);
    }

    @Test
    void playRound_surrender_halfBetReturned() {
        when(mockHandEvaluator.getOutcome(any(), any())).thenReturn(HandOutcome.SURRENDER);

        roundSimulator.playRound(player, dealer, deck, new BigDecimal("1.00"));

        // SURRENDER returns half the bet
        assertThat(player.getBank()).isEqualTo(new BigDecimal("9.50"));
    }

    @Test
    void playRound_multipleHands_resolvesEachHandSeparately() {
        when(playerStrategy.getNextMove(any(), any(), any()))
                .thenReturn(Move.SPLIT)  // First call: split
                .thenReturn(Move.STAND)  // Then stand on first split hand
                .thenReturn(Move.STAND); // Then stand on second split hand

        when(dealerStrategy.getNextMove(any(), any(), any()))
                .thenReturn(Move.STAND);

        when(mockHandEvaluator.getOutcome(any(), any()))
                .thenReturn(HandOutcome.WIN)
                .thenReturn(HandOutcome.WIN);

        Deck mockDeck = mock(Deck.class);
        when(mockDeck.getNextCard()).thenReturn(Card.EIGHT);

        roundSimulator.playRound(player, dealer, mockDeck, BigDecimal.ONE);

        assertThat(player.getBank()).isEqualTo(STARTING_BANK.add(BigDecimal.TWO));
    }
}