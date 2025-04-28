
# Blackjack Simulation

A Blackjack simulation application designed to empirically calculate how many rounds of blackjack
someone can play before they lose their seed money.

This simulator was built in Java, using Guice for dependency injection, Mockito for
unit testing, and Google's FluentLogger for logging.

This project attempts to demonstrate good abstractions, reasonable design decisions, and thorough
testing, with the caveat that I wrote it in a weekend and was definitely time crunched by the end.
Earlier code (Saturday) tends to be in /game and parts of /player. With more time, my top thing 
would be to take another pass at Player and clean it up. I'm not thrilled with where the 
abstraction and responsibilities landed for it.

---

## ✨ Features

- Full simulation of **Blackjack** games, including:
  - Hitting, standing, doubling down, splitting, surrendering
  - Soft and hard hands
  - Blackjack payout rules
- **Players** and **dealers** modeled with configurable strategies
- **Monte Carlo simulator** to analyze:
  - Median number of rounds survived
  - Standard deviation across simulations
- Clean, modular architecture with unit-tested components
- Dependency Injection via **Google Guice**
- Fluent structured logging via **Google FluentLogger**

---

## 🛠 Project Structure

| Package | Description                                                     |
|:--------|:----------------------------------------------------------------|
| `game` | Core game logic: Deck, Card, Hand, HandEvaluator                |
| `player` | Player and Dealer modeling: Player, Dealer, Strategy, HandState |
| `player.strategy` | Strategy interface and dealer/player strategies                 |
| `simulation` | Monte Carlo simulation, Game simulation, Round simulation       |

---

## If I had more time, what would I do next
- Revisit the round simulator and its interaction with the Player class. It works by asking the 
  player for their next move, and then confirming the move with them. That might be how the real 
  world works, but since it's a program, I should be able to trust my player doesn't cheat or 
  break the rules. Especially since my round simulator isn't verifying any rules itself. That 
  might make it more obvious what "Player" is actually responsible for, and with it simplified, 
  maybe better breakdowns will become obvious.
- Standardize BlackJackSimulationModule, right now its using several different strategies for 
  managing my injection needs, and it really doesn't need to. I should pick one and stick to it.
- Split RoundState out of Player, and test it independently. There's a lot of logic buried in 
  there, and while its reasonable that nothing outside of Player should have access, for unit 
  testing purposes its probably worth breaking that open a bit.
- Add more strategies and tests for strategies. The heart of this fun is seeing how different 
  strategies perform and comparing them against each other.
- Add more nobs and controls for the various rules that a casino might actually have. I 
  basically hardcoded all the most friendly player rules, but could pull that out and make it 
  configurable.
- Make the simulation multithreaded. This would be purely for my own experience. Performance 
  isn't currently a concern and I don't think running many order of magnitude more runs will 
  change the numbers. They seem to converge pretty quickly.
- Add more advanced metrics beyond median, mean, standard deviation. Perhaps track peaks, runs, etc.
- Consider adding multiple players to the table. Mostly just to better mimic the real world.
