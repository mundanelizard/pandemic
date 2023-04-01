package org.board.logic;

import jdk.jshell.execution.Util;
import org.board.entities.*;
import org.board.enumerables.Colour;
import org.board.utils.Index;
import org.board.utils.Utils;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;


public class Action {
    enum Type {
        Invalid,
        DriveOrFerry,
        DirectFlight,
        CharterFlight,
        ShuttleFlight,
        BuildResearchStation,
        TreatDiseaseRemoveOneCube,
        TreatDiseaseRemoveAll,
        EradicateDisease,
        DiscoverACure,
        TransferCard,
    }

    public static class Option {
        int disposeCard = -1;
        int endCity = -1;
        int endPlayer = -1;
        int suit = -1;
        String name = "";
        Type type = Type.Invalid;

        public Option(String name) {
            this.name = name;
        }
    }


    public static ArrayList<Option> getAllPossibleActions(int[][][] boardState, int[] cureIndicatorState, ArrayList<City> cities, ArrayList<Player> players, Player player) {
        var actions = new ArrayList<Option>();
        var cards = player.getHand();
        var currentCity = cities.get(player.getCity());
        var neighbours = currentCity.getNeighbours();

        var cubes = getDiseaseCubes(boardState, player.getCity());

        if (cubes.size() > 0) {
            // you can remove a disease cube from the board or all if it has been cured.
            // if it is the last cube of a curred disease it is eradicated
            var curred = cureIndicatorState[currentCity.getColour().ordinal()];

            Option option;
            if (curred == 1) {
                option = new Option("Cure disease (remove all cubes because it has been cured)");
                option.type = Type.TreatDiseaseRemoveAll;
            } else {
                option = new Option("Cure disease (remove one from cube)");
                option.type = Type.TreatDiseaseRemoveOneCube;
            }
            actions.add(option);
        }

        for (var city : neighbours) {
            // you can move via ferry to any city you are connected to
            var name = cities.get(city).getName();
            var option = new Option("Move by ferry to " + name);
            option.endCity = city;
            option.type = Type.DriveOrFerry;
            actions.add(option);
        }

        for (var card : cards) {
            var city = cities.get(card.getCity());
            var cityName = city.getName();
            var cityColour = city.getColour();

            var cardIndex = cards.indexOf(card);

            if (card.getCity() == player.getCity()) {
                // you can discard card to move to any city.
                buildOptionsToFlyToAllCities(actions, cities, card, cardIndex);
                // you can discard this card to build a research station
                var option = new Option("Dispose card [" + card.getCity() + ", " + cityName +  ", " + cityColour + "] to research station in current city " + cityName);
                option.type = Type.BuildResearchStation;
                option.disposeCard = cardIndex;
                actions.add(option);
            } else if(card.getCity() != -1) {
                // you can discard card to fly directly to the city
                var option = new Option("Dispose [" + card.getCity() + ", " + cityName + ", " + cityColour + "] to teleport to " + cityName);
                option.type = Type.DirectFlight;
                option.disposeCard = cardIndex;
                option.endCity = card.getCity();
                actions.add(option);
            }

            // you can transfer card to other players.
            buildOptionsToTransferCardToPlayers(actions, players, cardIndex, player.getPawn());
        }

        // check if you can discover a cure if the players i greater than 20
        var cures = getDiseaseCures(cities, cards);

        for (var cure : cures) {
            // don't display the disease if it has been cured
            if (cureIndicatorState[cure] == 1) continue;

            var option = new Option("Cure disease of colour " + Colour.values()[cure]);
            option.suit = cure;
            option.type = Type.DiscoverACure;
            actions.add(option);
        }

        return actions;
    }

    private static void buildOptionsToTransferCardToPlayers(ArrayList<Option> actions, ArrayList<Player> players, int cardIndex, int pawn) {
        for(var player : players) {
            if (player.getPawn() == pawn) continue;

            var option = new Option("Transfer card to player [" + player.getName() + "]");
            option.type = Type.TransferCard;
            option.endPlayer = player.getPawn();
            option.disposeCard = cardIndex;
            actions.add(option);
        }
    }

    private static ArrayList<Integer> getDiseaseCures(ArrayList<City> cities, ArrayList<PlayerCard> cards) {
        var cures = new ArrayList<Integer>();

        if (cards.size() < 5) {
            return cures;
        }

        var map = new int[Colour.values().length];

        for (var card : cards) {
            // counting the number of cards of the same colour
            var colour = cities.get(card.getCity()).getColour();
            map[colour.ordinal()] += 1;
        }

        for (var i = 0; i < map.length; i++) {
            // filter colour that are up to five.
            if (map[i] < 5) continue;
            cures.add(i);
        }

        return cures;
    }

    private static void buildOptionsToFlyToAllCities(ArrayList<Option> actions, ArrayList<City> cities, PlayerCard card, int cardIndex) {
        var cardCity = cities.get(card.getCity());
        var cardCityName = cardCity.getName();
        var cardCityColour = cardCity.getColour();

        for (var city: cities) {
            var option = new Option("Dispose [" + card.getCity() + ", " + cardCityName + ", " + cardCityColour + "] to teleport to " + city.getName());
            option.disposeCard = cardIndex;
            option.endCity = city.getId();
            actions.add(option);
        }
    }

    public static ArrayList<Integer> getDiseaseCubes(int[][][] boardState, int city) {
        var cubes = new ArrayList<Integer>();
        var disease = boardState[city][Game.BOARD_STATE_DISEASE_CUBE_INDEX];

        for (int cube : disease) {
            if (cube == -1) continue;
            cubes.add(cube);
        }

        return cubes;
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
