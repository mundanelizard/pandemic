package org.board.logic;

import jdk.jshell.execution.Util;
import org.board.entities.*;
import org.board.utils.Index;
import org.board.utils.Utils;

import java.sql.Array;
import java.util.ArrayList;

public class Action {

    public static class PlayerAction {

    }


    public static ArrayList<PlayerAction> getAllPossibleActions(int[][][] boardState, ArrayList<City> cities, Player player) {
        var actions = new ArrayList<PlayerAction>();
        var cards = player.getHand();
        var neighbours = cities.get(player.getCity()).getNeighbours();

        var cubes = getDiseaseCubes(boardState, player.getCity());

        if (cubes.length > 0) {
            // todo => check if it has been cured.
            // you can remove a disease cube from the board or all if it has been cured.
            // if it is the last cube of a curred disease it is eradicated
        }

        for (var city : neighbours) {
            // you can move via ferry to any city you are connected to
        }

        for (var card : cards) {
            if(card.getCity() != -1) {
                // you can discard card to teleport to this city
            }

            if (card.getCity() == player.getCity()) {
                // you can discard card to move to any city.
                // you can discard this card to build a research station
            }

            // you can transfer card to other players.
        }

        var stations = getCityStations(boardState, player.getCity());

        if (stations.length > 0) {
            addStationsToPossibleActions(actions, stations);
        }



        return actions;
    }



    public static boolean drawCardsAndInfectCities(int[][][] boardState, ArrayList<PlayerCard> cards, Player player) {


        return true;
    }



    public static void dealPlayersCardsToPlayer(ArrayList<Player> players, ArrayList<PlayerCard> playerCards, Index playerCardIndex) {
        for (var player : players) {
            var dealCount = 2;

            if (players.size() == 2) {
                dealCount = 4;
            } else if (players.size() == 3) {
                dealCount = 3;
            }

            for (int i = 0; i < dealCount; i++) {
                player.addCard(dealPlayerCard(playerCards, playerCardIndex));
            }
        }
    }

    public static PlayerCard dealPlayerCard(ArrayList<PlayerCard> playerCards, Index playerCardIndex) {
        var playerCard = playerCards.get(playerCardIndex.getIndex());
        playerCardIndex.increment();
        return playerCard;
    }

    public static boolean dealInfectionCardAndInfectCity(int[][][] boardState, ArrayList<City> cities, ArrayList<Cube> cubes, ArrayList<InfectionCard> infectionCards, Index infectionCardIndex)  throws Exception {
        return dealInfectionCardAndInfectCity(boardState, cities, cubes, infectionCards, infectionCardIndex, 1);
    }

    public static boolean dealInfectionCardAndInfectCity(int[][][] boardState, ArrayList<City> cities, ArrayList<Cube> cubes, ArrayList<InfectionCard> infectionCards, Index infectionCardIndex, int n) throws Exception {
        var card = dealInfectionCard(infectionCards, infectionCardIndex);
        var city = cities.get(card.getId());

        for (int i = 0; i < n; i++) {
            var cube = Cube.getEmptyCube(cubes, city.getColour());

            if (cube == null) {
                return false;
            }

            cube.setCity(city.getId());
            Utils.insert(boardState, city.getId(), Game.BOARD_STATE_DISEASE_CUBE_INDEX, cube.getId());
        }

        return true;
    }

    public static void placePawn(int[][][] boardState, Player player, int city) {
        player.setCity(city);
        Utils.insert(boardState, city, Game.BOARD_STATE_PAWN_CUBE_INDEX, player.getPawn());
    }

    public static void placeStation(int[][][] boardState, ArrayList<Station> stations, int city) throws Exception {
        var station = Station.getEmptyStation(stations);

        if (station == null) {
            throw new Exception("Out of stations>");
        }

        station.setCity(city);
        Utils.insert(boardState, city, Game.BOARD_STATE_STATION_CUBE_INDEX, station.getId());
    }

    public static void epidemicInfectionCardsReset(ArrayList<InfectionCard> infectionCards, Index infectionCardIndex) {
        Utils.shuffle(infectionCards, infectionCardIndex.getIndex());
        infectionCardIndex.reset();
    }

    public static InfectionCard dealInfectionCard(ArrayList<InfectionCard> infectionCards, Index infectionCardIndex) {
        var infectionCard = infectionCards.get(infectionCardIndex.getIndex());
        infectionCardIndex.increment();
        return infectionCard;
    }

}
