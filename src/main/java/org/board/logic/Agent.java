package org.board.logic;

import org.board.entities.Option;
import org.board.entities.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.IntStream;

record Outcome(Option option, double epidemics, double rating, double cards) {
    @Override
    public String toString() {
        return "Option: " + option.getName() + "\n\t\t rating -> " + rating + " epidemic -> " + epidemics + " cards " + cards;
    }
}

public class Agent {
    final int CUBES_ON_BOARD_WEIGHT = 10;
    final int CUBES_FREE_CUBES_WEIGHT = 10;
    final int INFECTION_RATE_WEIGHT = 10;
    final int CURE_WEIGHT = 80;
    final int EPIDEMICS_WEIGHT = -10;
    final int OUTBREAK_WEIGHT = -50;
    final int GAME_OVER_WEIGHT = -100;
    final int RESEARCH_STATION_WEIGHT = 10;


    static final public String NAME = "Rupert";
    private Player player;

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void play(State state) throws Exception {
        System.out.println("Agent is performing is actions");

        for (int i = 1; i <= 4; i++) {
            var outcomes = getRankedBestOptions(state);
            var bestOutcome = outcomes.get(0);
            state.performAction(bestOutcome.option(), i);
        }
    }

    public void printOutcomes(ArrayList<Outcome> outcomes) {
        for(var outcome : outcomes) {
            System.out.println(outcome);
            System.out.println();
        }
    }

    public ArrayList<Outcome> getRankedBestOptions(State state) throws Exception {
        var ranking = new ArrayList<Outcome>();

        for (var option : state.getAllPossibleActions()) {
            var outcome = traverseGameTree(state, option);
            ranking.add(outcome);
        }

        // sort ranking based on rating and epidemics
        ranking.sort(Comparator.comparingDouble(o -> -o.rating()));

        return ranking;
    }

    public Outcome traverseGameTree(State state, Option option) throws Exception {
        return traverseGameTree(state, option,1);
    }

    public Outcome traverseGameTree(State state, Option action, int count) throws Exception {
        // clone the state
        var newState = state.deepClone();
        // turning off action logging
        newState.debug = false;
        // perform action for the current turn.
        newState.performAction(action, count);

        if (count == 4) {
            var rating = rateState(state, newState);
            var epidemics = newState.getEpidemics() - state.getEpidemics();
            var cards = newState.getPlayers().get(state.getTurn()).getHand().size();
            return new Outcome(action, epidemics, rating, cards);
        }

        double rating = 0;
        double epidemics = 0;
        double cards = 0;

        // get all possible board ratings
        var actions = newState.getAllPossibleActions();

        for (var option : actions) {
            var outcome = traverseGameTree(newState, option, count + 1);
            rating += outcome.rating();
            epidemics += outcome.epidemics();
            cards += outcome.cards();
        }

        rating = rating / actions.size();
        epidemics = epidemics / actions.size();
        cards = cards / actions.size();

        return new Outcome(action, epidemics, rating, cards);
    }


    private int rateState(State beginState, State endState) {
        // higher better - lower worse

        // get number of cubes on board difference
        var cubes = (beginState.getCubesOnBoardCount() - endState.getCubesOnBoardCount()) * CUBES_ON_BOARD_WEIGHT;

        // get number of free cubes;
        var freeCubes = (beginState.getFreeCubesCount() - endState.getFreeCubesCount()) * CUBES_FREE_CUBES_WEIGHT;

        // get infection rate difference
        var infectionRate = (beginState.getInfectionRateMarkerState() - endState.getInfectionRateMarkerState()) * INFECTION_RATE_WEIGHT;

        // get cure indicator difference
        var cure = (IntStream.of(beginState.getCureIndicatorState()).sum() - IntStream.of(endState.getCureIndicatorState()).sum()) * CURE_WEIGHT;

        // get epidemics difference
        var epidemics = (beginState.getEpidemics() - endState.getEpidemics()) * EPIDEMICS_WEIGHT;

        // check if the game is over difference
        var failed = endState.isFailed() ? GAME_OVER_WEIGHT : 0;

        // check outbreak marker difference
        var outbreaks = (beginState.getOutbreakMarkerState() - endState.getOutbreakMarkerState()) * OUTBREAK_WEIGHT;

        // research stations difference
        var stations = (beginState.getResearchStationsCount() - endState.getResearchStationsCount()) * RESEARCH_STATION_WEIGHT;

        return cubes + freeCubes + infectionRate + cure + epidemics + failed + outbreaks + stations;
    }
}
