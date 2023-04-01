package org.board.logic;

import org.board.entities.*;
import org.board.enumerables.*;
import org.board.utils.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Game {

    static final int BOARD_STATE_DISEASE_CUBE_INDEX = 1;
    static final int BOARD_STATE_PAWN_CUBE_INDEX = 2;
    static final int BOARD_STATE_STATION_CUBE_INDEX = 0;
    /**
     * Coding
     * x - is the city
     * state[x][0] = station
     * state[x][1] = pawn
     * state[x][2] = disease cube
     */
    private int[][][] boardState;
    private int outbreakMarkerState = 0;
    private int[] cureIndicatorState = new int[4];
    /**
     * Infection Deck 0 - 7;
     * ----------------------
     * 0. infection rate is 2
     * 1. infection rate is 2
     * 2. infection rate is 2
     * 3. infection rate is 3
     * 4. infection rate is 3
     * 5. infection rate is 4
     * 6. infection rate is 4
     */
    private final Index infectionIndex = new Index();

    ArrayList<PlayerCard> playerCards = new ArrayList<>();
    private final Index playerCardIndex = new Index();

    ArrayList<InfectionCard> infectionCards = new ArrayList<>();
    private final Index infectionCardIndex = new Index();

    ArrayList<Player> players = new ArrayList<>();
    int turn = 0;

    ArrayList<City> cities = new ArrayList<>();
    ArrayList<Cube> cubes = new ArrayList<>();

    ArrayList<Station> stations = new ArrayList<>();


    final private Agent agent = new Agent();

    private boolean running = true;


    private void initialise() throws Exception {
        cities = Loader.loadCityGraph();
        infectionCards = Loader.loadInfectionCards();
        playerCards = Loader.loadPlayerCards();
        boardState = Loader.loadEmptyBoardState();
        cubes = Loader.loadCubes();
        stations = Loader.loadStations();

        infectionCardIndex.reset();
        infectionIndex.reset();
        playerCardIndex.reset();

        // Places a research station in Atlanta
        initialiseStation();
        outbreakMarkerState = 0;
        Arrays.fill(cureIndicatorState, 0);

        players = Loader.loadPlayers();
        agent.setPlayer(players.get(players.size() - 1));
        initialisePawns();

        Utils.shuffle(playerCards, playerCardIndex.getIndex());
        Utils.shuffle(infectionCards, infectionCardIndex.getIndex());

        Action.dealPlayersCardsToPlayer(players, playerCards, playerCardIndex);
        dealInfectionCardAndInfectionCities();

        running = true;
    }


    private void dealInfectionCardAndInfectionCities() throws Exception {
        Action.dealInfectionCardAndInfectCity(boardState, cities, cubes, infectionCards, infectionCardIndex, 3);
        Action.dealInfectionCardAndInfectCity(boardState, cities, cubes, infectionCards, infectionCardIndex, 3);
        Action.dealInfectionCardAndInfectCity(boardState, cities, cubes, infectionCards, infectionCardIndex, 3);

        Action.dealInfectionCardAndInfectCity(boardState, cities, cubes, infectionCards, infectionCardIndex, 2);
        Action.dealInfectionCardAndInfectCity(boardState, cities, cubes, infectionCards, infectionCardIndex, 2);
        Action.dealInfectionCardAndInfectCity(boardState, cities, cubes, infectionCards, infectionCardIndex, 2);

        Action.dealInfectionCardAndInfectCity(boardState, cities, cubes, infectionCards, infectionCardIndex);
        Action.dealInfectionCardAndInfectCity(boardState, cities, cubes, infectionCards, infectionCardIndex);
        Action.dealInfectionCardAndInfectCity(boardState, cities, cubes, infectionCards, infectionCardIndex);
    }


    private void handleEndgame() {
        // You lose if:
        // - 8 outbreaks occur,
        // - not enough disease cubes are left when needed (a disease spreads too much), or,
        // - not enough player cards are left when needed (your team runs out of time).
    }

    private void initialisePawns() throws Exception {
        var atlanta = City.getCityByName("Atlanta");

        if (atlanta == null) {
            throw new Exception("Atlanta couldn't be found.");
        }

        for (var player : players) {
            Action.placePawn(boardState, player, atlanta.getId());
        }
    }

    private void initialiseStation() throws Exception {
        var atlanta = City.getCityByName("Atlanta");

        if (atlanta == null) {
            throw new Exception("Atlanta couldn't be found.");
        }

        Action.placeStation(boardState, stations, atlanta.getId());
    }

    public void start() throws Exception {
        initialise();

        while(running) {
            handleGamePlay();
        }
    }

    private void handleGamePlay() throws Exception {
        var player = players.get(turn);

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
        running = false;
    }

    private void handleViewBoardState() {
        
    }

    private void handleConsultAgent() {

    }

    private void handleViewCards() {

    }

    private void handlePerformAction() throws Exception {
        var player = players.get(turn);
        increaseTurn();

        // travels all the possible state for the current game
        for (int i = 0; i < 4 && running; i++) {
            var options = Action.getAllPossibleActions(boardState, cureIndicatorState, cities, players, player);
            var choice = IO.getPlayerActionChoice(options, player, i + 1);
            running = Action.performAction(boardState, players, stations, player, choice);
        }

        if (!running) {
            return;
        }

//        running = Action.drawCardsAndInfectCities(boardState, playerCards, player);
    }

    void increaseTurn() {
        turn = (turn + 1) % players.size();
    }
}
