package com.drawkcab.blackjack.simulation.modules;

import com.drawkcab.blackjack.game.Deck;
import com.drawkcab.blackjack.player.Dealer;
import com.drawkcab.blackjack.player.Player;
import com.drawkcab.blackjack.player.strategy.DealerStrategy;
import com.drawkcab.blackjack.player.strategy.Strategy;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.math.BigDecimal;

public class BlackJackSimulationModule extends AbstractModule {
    private final SimulationConfiguration simulationConfiguration;

    public BlackJackSimulationModule(SimulationConfiguration simulationConfiguration) {
        this.simulationConfiguration = simulationConfiguration;
    }

    @Override
    protected void configure() {
        bind(BigDecimal.class).annotatedWith(MinBet.class)
                .toInstance(simulationConfiguration.minBet());
    }

    @Provides
    @Singleton
    Deck provideDeck() {
        return new Deck(simulationConfiguration.numDecks());
    }

    @Provides
    @Singleton
    Player providePlayer() {
        return new Player(simulationConfiguration.playerStrategy(),
                simulationConfiguration.startingBank());
    }

    @Provides
    @Singleton
    Dealer provideDealer() {
        Strategy dealerStrategy = new DealerStrategy();
        return new Dealer(dealerStrategy);
    }

    public record SimulationConfiguration(int numDecks, BigDecimal startingBank, BigDecimal minBet,
                                          Strategy playerStrategy) {
    }
}