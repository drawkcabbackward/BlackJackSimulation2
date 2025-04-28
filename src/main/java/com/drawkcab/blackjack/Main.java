package com.drawkcab.blackjack;

import com.drawkcab.blackjack.player.strategy.BookPlayerStrategy;
import com.drawkcab.blackjack.simulation.MonteCarloSimulator;
import com.drawkcab.blackjack.simulation.MonteCarloSimulator.SimulationResult;
import com.drawkcab.blackjack.simulation.modules.BlackJackSimulationModule;
import com.drawkcab.blackjack.simulation.modules.BlackJackSimulationModule.SimulationConfiguration;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point for running a Monte Carlo Blackjack simulation.
 *
 * <p>Initializes logging, configures the simulation parameters, sets up dependency
 * injection via Guice, and runs a simulation of a specified number of rounds.</p>
 *
 * <p>The results of the simulation are printed to standard output.</p>
 */
public class Main {
    public static void main(String[] args) {
        setupLogging(Level.WARNING);

        // MODIFY SIMULATION CONFIGURATION HERE
        SimulationConfiguration simulationConfiguration =
                new SimulationConfiguration(
                        6, // numDecks
                        new BigDecimal("100.00"), // startingBank
                        new BigDecimal("10.00"), // minBet
                        new BookPlayerStrategy() // playerStrategy
                );

        Injector injector = Guice.createInjector(new BlackJackSimulationModule(simulationConfiguration));
        MonteCarloSimulator simulator = injector.getInstance(MonteCarloSimulator.class);

        SimulationResult result = simulator.run(10_000);

        System.out.printf("Simulation Results = [%s]", result);
    }

    private static void setupLogging(Level level) {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(level);
        for (var handler : rootLogger.getHandlers()) {
            handler.setLevel(level);
        }
    }
}