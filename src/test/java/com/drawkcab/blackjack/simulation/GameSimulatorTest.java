package com.drawkcab.blackjack.simulation;

import com.drawkcab.blackjack.game.Deck;
import com.drawkcab.blackjack.player.Dealer;
import com.drawkcab.blackjack.player.Player;
import com.drawkcab.blackjack.simulation.modules.MinBet;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.google.inject.Guice.createInjector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameSimulatorTest {
    @Bind @Mock RoundSimulator roundSimulator;
    @Bind @Mock Player player;
    @Bind @Mock Dealer dealer;
    @Bind @Mock Deck deck;

    @Bind @MinBet BigDecimal minBet = BigDecimal.ONE;

    @Inject
    private GameSimulator gameSimulator;

    @BeforeEach
    void setUp() {
        createInjector(BoundFieldModule.of(this)).injectMembers(this);
    }

    @Test
    void playGame_playerCanAffordPlaysRoundsUntilBroke() {
        // Player can afford 3 rounds, then can't
        when(player.getBank())
                .thenReturn(BigDecimal.valueOf(5))
                .thenReturn(BigDecimal.valueOf(3))
                .thenReturn(BigDecimal.valueOf(1))
                .thenReturn(BigDecimal.valueOf(0)); // after 3 rounds, broke

        long roundsPlayed = gameSimulator.playGame();

        assertEquals(3, roundsPlayed);

        // Verifies we actually played 3 rounds
        verify(roundSimulator, times(3)).playRound(eq(player), eq(dealer), eq(deck), eq(minBet));
        verify(deck, times(3)).shuffle();
        verify(player).reset();
        verify(dealer).reset();
    }

    @Test
    void playGame_playerCannotAfford_initially_stopsImmediately() {
        // Player is broke right away
        when(player.getBank()).thenReturn(BigDecimal.ZERO);

        long roundsPlayed = gameSimulator.playGame();

        assertEquals(0, roundsPlayed);

        verify(roundSimulator, never()).playRound(any(), any(), any(), any());
        verify(player).reset();
        verify(dealer).reset();
    }
}