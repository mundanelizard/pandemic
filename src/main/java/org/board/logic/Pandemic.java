package org.board.logic;

import org.board.entities.*;
import org.board.utils.Loader;

import java.util.ArrayList;
import java.util.Arrays;

public class Pandemic {
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


    ArrayList<City> cities = new ArrayList<>();
    ArrayList<PlayerCard> playerCards = new ArrayList<>();
    ArrayList<InfectionCard> infectionCards = new ArrayList<>();
    ArrayList<Cube> cubes = new ArrayList<>();
    ArrayList<Player> players = new ArrayList<>();


    private void initialise() throws Exception {
        cities = Loader.loadCityGraph();
        infectionCards = Loader.loadInfectionCards();
        playerCards = Loader.loadPlayerCards();

        boardState = Loader.loadEmptyBoardState();
        cubes = Loader.loadCubes();

        // Places a research station in Atlanta
        initialiseStation();
        outbreakMarkerState = 0;
        Arrays.fill(cureIndicatorState, 0);


        players = Loader.loadPlayers();

        dealPlayersCards();
    }


    private void initialiseStation() throws Exception {
        var atlanta = City.getCityByName("Atlanta");

        if (atlanta == null) {
            throw new Exception("Atlanta couldn't be found.");
        }

        Station.place(boardState, atlanta.getId());
    }



    void start() throws Exception {
        boolean isRunning = true;
        initialise();

        // You lose if:
        // - 8 outbreaks occur,
        // - not enough disease cubes are left when needed (a disease spreads too much), or,
        // - not enough player cards are left when needed (your team runs out of time).
    }
}
