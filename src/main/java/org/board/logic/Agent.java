package org.board.logic;

import org.board.entities.Option;
import org.board.entities.Player;
import org.board.utils.IO;

import java.util.ArrayList;
import java.util.Comparator;


public class Agent {
    public record Outcome(Option option, double epidemics, double rating, double cards) {
        @Override
        public String toString() {
            return "Option: " + option.getName() + "\n\t\t rating -> " + rating + " epidemic -> " + epidemics + " cards " + cards;
        }
    }


    /* Weights for evaluation function */
    final int CUBES_FREED_WEIGHT = 20;
    final int INFECTION_RATE_WEIGHT = -50;
    final int CURE_WEIGHT = 200;
    final int EPIDEMICS_WEIGHT = -80;
    final int OUTBREAK_WEIGHT = -100;
    final int GAME_OVER_WEIGHT = -100;
    final int RESEARCH_STATION_WEIGHT = 60;
    final int WINNING_WEIGHT = 1000;


    /*  agent name */
    static final public String NAME = "Rupert";

    /* player associated to agent */
    private Player player;

    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Gets the player associated to the agent.
     * @return a player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Plays the game by picking the best outcome out of a series of outs.
     * @param state the state of the game.
     * @throws Exception whe the game state errors.
     */
    public void play(State state) throws Exception {
        System.out.println("Agent is performing his actions");

        for (int i = 1; i <= 4; i++) {
            var outcomes = getRankedBestOptions(state);
            var bestOutcome = outcomes.get(0);
            state.performAction(bestOutcome.option(), i);
        }
    }



    /**
     * Gets a list of all the outcomes ranked by their points.
     * @param state the state of the game.
     * @return a list of outcomes
     * @throws Exception when the game state encounters an error state.
     */
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

    /**
     * Traverse the game tree and returns an estimate of the performance of the game tree.
     * @param state the state of the board.
     * @return outcome of the action.
     * @throws Exception when the state fails.
     */
    public Outcome traverseGameTree(State state, Option option) throws Exception {
        return traverseGameTree(state, option,1);
    }

    /**
     * Traverse the game tree and returns an estimate of the performance of the game tree.
     * @param state the state of the board.
     * @param action the action to perform.
     * @param count what action count is it.
     * @return outcome of the action.
     * @throws Exception when the state fails.
     */
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

    /**
     * Handles agent consultation and gets user response on action to take.
     * @param state the game state
     * @param player the player whose turn it is.
     * @throws Exception when the game state errors during an action or traversal.
     */
    public void consult(State state, Player player) throws Exception {
        System.out.println("\nConsulting agent " + getPlayer().getName());

        for (int actionCount = 1; actionCount <= 4; actionCount++) {
            var outcomes = getRankedBestOptions(state);
            var choice = IO.getPlayerPreferredOutcome(outcomes, player.getName(), getPlayer().getName(), actionCount);
            state.performAction(choice.option(), actionCount);
        }
    }

    /**
     * Evaluation function for the board states.
     * @param beginState the beginning state
     * @param endState the end state
     * @return the rating of the action.
     */
    private int rateState(State beginState, State endState) {
        // higher better - lower worse

        // get number of free cubes;
        var cubesFreed = (endState.getFreeCubesCount() - beginState.getFreeCubesCount()) * CUBES_FREED_WEIGHT;

        // get infection rate difference
        var infectionRate = (endState.getInfectionRateMarkerState() - beginState.getInfectionRateMarkerState()) * INFECTION_RATE_WEIGHT;

        // get cure indicator difference
        var cure = (endState.getCureCount() - beginState.getCureCount()) * CURE_WEIGHT;

        // get epidemics difference
        var epidemics = (endState.getEpidemics() - beginState.getEpidemics()) * EPIDEMICS_WEIGHT;

        // check if the game is over difference
        var failed = endState.isFailed() ? GAME_OVER_WEIGHT : 0;
        var winning = !endState.isRunning() && !endState.isFailed() ? WINNING_WEIGHT : 0;

        // check outbreak marker difference
        var outbreaks = (endState.getOutbreakMarkerState() - beginState.getOutbreakMarkerState()) * OUTBREAK_WEIGHT;

        // research stations difference
        var stations = (endState.getResearchStationsCount() - beginState.getResearchStationsCount()) * RESEARCH_STATION_WEIGHT;

        return cubesFreed + infectionRate + cure + epidemics + failed + outbreaks + stations + winning;
    }
}
