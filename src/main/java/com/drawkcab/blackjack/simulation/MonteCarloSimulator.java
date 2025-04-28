package com.drawkcab.blackjack.simulation;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class MonteCarloSimulator {
    private final GameSimulator game;

    @Inject
    public MonteCarloSimulator(GameSimulator game) {
        this.game = game;
    }

    public SimulationResult run(int numberOfRuns) {
        List<Long> games = new ArrayList<>();

        for (int i = 0; i < numberOfRuns; i++) {
            games.add(game.playGame());
        }

        return calculateResults(games);
    }

    private SimulationResult calculateResults(List<Long> games) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        games.forEach(stats::addValue);

        Median medianCalculator = new Median();
        double median = medianCalculator.evaluate(games.stream().mapToDouble(k -> k).toArray());

        return new SimulationResult(
                median,
                stats.getMean(),
                stats.getStandardDeviation());

    }

    public record SimulationResult(double median, double mean, double standardDeviation) {}
}