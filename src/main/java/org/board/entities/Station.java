package org.board.entities;

import java.util.ArrayList;

/**
 * Represents a station in the game.
 */
public class Station {
    private final int id;
    private int city = -1;

    private final static ArrayList<Station> stations = new ArrayList<>();

    /**
     * Sets station id;
     * @param id id of the station
     */
    private Station(int id) {
        this.id = id;
    }

    /**
     * Check if the station isn't on the board.
     * @return true if it is off the board.
     */
    public boolean empty() {
        return this.city == -1;
    }

    /**
     * Gets the city
     * @return city id
     */
    public int getCity() {
        return city;
    }

    /**
     * Sets the city of the station.
     * @param city city id
     * @throws Exception if city is already set
     */
    public void setCity(int city) throws Exception {
        if (!empty()) {
            throw new Exception("Station " + id + " is already placed on the board.");
        }

        this.city = city;
    }

    /**
     * Gets the station id.
     * @return the id of the station.
     */
    public int getId() {
        return id;
    }


    /* Static Methods */

    /**
     * Adds a new station to the initialisation stations list
     * @param id the id of the station.
     */
    public static void addStation(int id) {
        stations.add(new Station(id));
    }

    /**
     * Gets the stations from the loading process.
     * @return the list of station created during loading
     */
    public static ArrayList<Station> getStations() {
        return stations;
    }

    /**
     * Clones a list of stations.
     * @param stations list of stations.
     */
    public static ArrayList<Station> getStations(ArrayList<Station> stations) {
        var newStations = new ArrayList<Station>();

        for (var station : stations) {
            var newStation = new Station(station.getId());
            newStation.city = station.getCity();
            newStations.add(newStation);
        }

        return newStations;
    }

    /**
     * Gets empty station.
     * @param stations a list of empty stations.
     * @return null if there isn't any empty station.
     */
    public static Station getEmptyStation(ArrayList<Station> stations) {
        for (var station : stations) {
            if (station.empty()) {
                return station;
            }
        }

        var station = stations.get(0);
        station.empty();

        return station;
    }
}
