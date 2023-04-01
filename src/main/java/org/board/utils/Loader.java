package org.board.utils;

import org.board.entities.*;
import org.board.enumerables.Card;
import org.board.enumerables.Colour;
import org.board.enumerables.Role;
import org.board.logic.Agent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Loader {
    static Random random = new Random(1);
    static final private String FILENAME = "map.txt";
    static private ArrayList<City> cities = new ArrayList<>();

    static public ArrayList<Player> loadPlayers() throws Exception {
        Player.reset();
        var players = -1;

        do {
            System.out.println("How many players do you want (1 - 3)? The agent is always a player in each game.");
            System.out.print("> ");

            players = IO.shell.nextInt();
        } while(players < 1 || players > 3);

        int i;
        for (i = 0; i < players; i++) {
            System.out.println("What's player " + (i + 1) + " name?");
            System.out.print("> ");
            String name = IO.shell.next();

            var roles = Player.getAvailableRoles();
            var role = roles[random.nextInt(roles.length - 1)];

            Player.addPlayer(name, i, Role.getRole(role));
        }

        System.out.println();
        System.out.println("Your Agent name is 'Rupert'");
        Player.addPlayer(Agent.NAME, i, Role.getRole(Player.getAvailableRoles()[0]));
        System.out.println();

        return Player.getPlayers();
    }

    static public ArrayList<Station> loadStations() throws Exception {
        var stations = Station.getStations();

        if (stations.size() == 6) {
            return stations;
        }

        for (int i = 0; i < 6; i++) {
            Station.addStation(i);
        }

        return Station.getStations();
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
        int[][][] boardState = new int[48][3][24];

        for (int[][] ints : boardState) {
            for (int[] anInt : ints) {
                Arrays.fill(anInt, -1);
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
            PlayerCard.addCard(city.getId(), Card.Regular, city.getColour());
        }

        for (int i = 0; i < 5; i++) {
            PlayerCard.addCard(-1, Card.Epidemic, Colour.Invalid);
        }

        for (int i = 0; i < 6; i++) {
            PlayerCard.addCard(-1, Card.Epidemic, Colour.Invalid);
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

        var handle = new File(FILENAME);
        var reader = new Scanner(handle);

        loadCities(reader);
        loadConnections(reader);

        cities = City.getCities();

        return cities;
    }

    private static void loadCities(Scanner reader) {
        for(int id = 0; reader.hasNextLine(); id++) {
            var line = reader.nextLine();
            if (line.equals("--")) break;

            var segments = line.split(";");

            var name = segments[0];
            var colour = segments[1];

            City.addCity(id, name, Colour.getColour(colour));
        }
    }

    private static void loadConnections(Scanner reader) throws Exception {
        while(reader.hasNextLine()) {
            var line = reader.nextLine();
            if (line.equals("--")) break;

            var segments = line.split(";");

            var firstCity = segments[0];
            var secondCity = segments[1];

            City.connect(firstCity, secondCity);
        }

    }
}
