package org.board.logic;

import org.board.utils.*;

public class Game {
    State state = new State();

    private final Agent agent = new Agent();

    public Game() throws Exception {}


    /**
     * Starts the game
     * @throws Exception when the game state errors.
     */
    public void start() throws Exception {
        var players = state.getPlayers();

        agent.setPlayer(players.get(players.size() - 1));

        while(state.isRunning()) {
            handleGamePlay();
        }

        if (state.isFailed()) {
            System.out.println("Game Over: " + state.getStatus());
        } else {
            System.out.println("Congratulations you've discovered the cure to all the disease!");
        }
    }

    /**
     * Request players and agent to play game.
     * @throws Exception when the game state errors
     */
    private void handleGamePlay() throws Exception {
        var player = state.getCurrentPlayer();

        agent.play(state);

//        if (player.getPawn() == agent.getPlayer().getPawn()) {
//            agent.play(state);
//            return;
//        }
//
//        switch (IO.getPlayerChoice(player)) {
//            case PerformAction -> handlePerformAction();
//            case ViewCards -> handleViewCards();
//            case ConsultAgent -> handleConsultAgent();
//            case ViewBoardState -> handleViewBoardState();
//            case QuitGame -> handleQuitGame();
//            default -> throw new Exception("Invalid choice");
//        }
    }

    /**
     * Handles quiting game
     */
    private void handleQuitGame() {
        System.exit(1);
    }

    /**
     * Prints out the board state
     */
    private void handleViewBoardState() {
//        state.printBoard();
    }

    /**
     * Handles the consulting agent logic
     */
    private void handleConsultAgent() {
//        agent.consult(state, state.getCurrentPlayer());
    }

    /**
     * Handle the view cards logic
     */
    private void handleViewCards() {
//        state.printAllPlayersCards();
    }

    /**
     * Asks users for action and performs actions.
     * @throws Exception when the game state errors.
     */
    private void handlePerformAction() throws Exception {
        for (int i = 1; i <= 4 && state.isRunning(); i++) {
            var options = state.getAllPossibleActions();
            var choice = IO.getPlayerActionChoice(options, state.getCurrentPlayer(), i);
            state.performAction(choice, i);
        }
    }
}
