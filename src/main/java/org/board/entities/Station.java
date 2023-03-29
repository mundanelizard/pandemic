package org.board.entities;

import org.board.utils.Utils;

import java.util.ArrayList;

public class Station {
    public static final int STATION_BOARD_STATE_INDEX = 0;

    private final int id;
    private int city = -1;


    private static ArrayList<Station> stations = new ArrayList<>();

    private Station(int id) {
        this.id = id;
    }

    public void remove() {
        this.city = -1;
    }

    public boolean empty() {
        return this.city == -1;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) throws Exception {
        if (city != -1) {
            throw new Exception("Station " + id + " is already placed on the board.");
        }

        this.city = city;
    }

    public int getId() {
        return id;
    }


    /* Static Methods */

    public static void addCube(int id) {
        stations.add(new Station(id));
    }

    public ArrayList<Station> getStations() throws Exception {
        var newStations = new ArrayList<Station>();

        for (var station : stations) {
            new Station(station.getId());
            station.setCity(station.getCity());
            newStations.add(station);
        }

        return newStations;
    }

    public static Station getEmptyStation() {
        for (var station : stations) {
            if (station.empty()) {
                return station;
            }
        }

        return null;
    }

    public static void place(int[][][] boardState, int cityId) throws Exception {
        var station = getEmptyStation();

        if (station == null) {
            throw new Exception("Out of stations.");
        }

        Utils.insert(boardState, cityId, STATION_BOARD_STATE_INDEX,  station.getId());
    }
}
