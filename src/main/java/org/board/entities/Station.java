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
        if (!empty()) {
            throw new Exception("Station " + id + " is already placed on the board.");
        }

        this.city = city;
    }

    public int getId() {
        return id;
    }


    /* Static Methods */

    public static void addStation(int id) {
        stations.add(new Station(id));
    }

    public static ArrayList<Station> getStations() throws Exception {
        return getStations(stations);
    }

    public static ArrayList<Station> getStations(ArrayList<Station> stations) throws Exception {
        var newStations = new ArrayList<Station>();

        for (var station : stations) {
            new Station(station.getId());
            station.setCity(station.getCity());
            newStations.add(station);
        }

        return newStations;
    }

    public static Station getEmptyStation(ArrayList<Station> stations) {
        for (var station : stations) {
            if (station.empty()) {
                return station;
            }
        }

        return null;
    }
}
