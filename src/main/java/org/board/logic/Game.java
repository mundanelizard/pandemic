package org.board.logic;

import org.board.utils.*;


public class Game {
    State state = new State();

    private final Agent agent = new Agent();

    public Game() throws Exception {}


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

    private void handleGamePlay() throws Exception {
        var player = state.getCurrentPlayer();

        if (player.getPawn() == agent.getPlayer().getPawn()) {
            agent.play(state);
            return;
        }

        switch (IO.getPlayerChoice(player)) {
            case PerformAction -> handlePerformAction();
            case ViewCards -> handleViewCards();
            case ConsultAgent -> handleConsultAgent();
            case ViewBoardState -> handleViewBoardState();
            case QuitGame -> handleQuitGame();
            default -> throw new Exception("Invalid choice");
        }
    }

    private void handleQuitGame() {
        state.setRunning(false);
    }

    private void handleViewBoardState() {
        
    }

    private void handleConsultAgent() {

    }

    private void handleViewCards() {

    }

    private void handlePerformAction() throws Exception {
        // travels all the possible state for the current game
        for (int i = 1; i <= 4 && state.isRunning(); i++) {
            var options = state.getAllPossibleActions();
            var choice = IO.getPlayerActionChoice(options, state.getCurrentPlayer(), i);
            state.performAction(choice, i);
        }
    }
}
