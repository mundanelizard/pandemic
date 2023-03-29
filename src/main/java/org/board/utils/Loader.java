package org.board.utils;

import org.board.entities.*;
import org.board.enumerables.Card;
import org.board.enumerables.Colour;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Loader {
    static final private String FILENAME = "map.txt";
    static private ArrayList<City> cities = new ArrayList<>();

    static public ArrayList<Player> loadPlayers() {
        // assign the user a random role
        // assign a reference card - not needed

        return new ArrayList<>();
    }

    static public ArrayList<Cube> loadCubes() throws Exception {
        for (var colour : Colour.values()) {
            var padding = colour.ordinal() * 24;
            for (int i = 0; i < 24; i++) {
                var id = padding + i;
                Cube.addCube(id, colour);
            }
        }

        return Cube.getCubes();
    }

    static public int[][][] loadEmptyBoardState() {
        int[][][] boardState = new int[48][3][];

        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState[i].length; i++) {
                Arrays.fill(boardState[i][j], -1);
            }
        }

        return boardState;
    }

    static public ArrayList<PlayerCard> loadPlayerCards() {
        var playerCards = PlayerCard.getCards();

        if (playerCards.size() == 59) {
            return playerCards;
        }

        for (var city : cities) {
            PlayerCard.addCard(city.getId(), Card.Regular);
        }

        for (int i = 0; i < 5; i++) {
            PlayerCard.addCard(-1, Card.Epidemic);
        }

        for (int i = 0; i < 6; i++) {
            PlayerCard.addCard(-1, Card.Epidemic);
        }

        return PlayerCard.getCards();
    }

    static public ArrayList<InfectionCard> loadInfectionCards() {
        var infectionCards = InfectionCard.getCards();

        if (infectionCards.size() == 48) {
            return infectionCards;
        }

        // 48 cards representing each city
        for (var city : cities) {
            InfectionCard.addCard(city.getId());
        }

        // loaded infection cards
        return InfectionCard.getCards();
    }


    static public ArrayList<City> loadCityGraph() throws Exception {
        cities = City.getCities();

        if (cities.size() == 48) {
            return cities;
        }

        var start = 0;
        var handle = new File(FILENAME);
        var reader = new Scanner(handle);

        start = loadCities(reader, start);
        loadConnections(reader, start);

        return City.getCities();
    }

    private static int loadCities(Scanner reader, int start) {
        while(reader.hasNextLine()) {
            start += 1;
            var line = reader.nextLine();
            if (line.equals("--")) break;

            var segments = line.split(";");

            var name = segments[0];
            var colour = segments[1];

            City.addCity(start, name, Colour.getColour(colour));
        }

        return start;
    }

    private static void loadConnections(Scanner reader, int start) throws Exception {
        while(reader.hasNextLine()) {
            start += 1;
            var line = reader.nextLine();
            if (line.equals("--")) break;

            var segments = line.split(";");

            var firstCity = segments[0];
            var secondCity = segments[0];

            City.connect(firstCity, secondCity);
        }

    }
}
