package org.board.entities;

import org.board.enumerables.Colour;
import java.util.ArrayList;

/**
 * Represents a city on the board.
 */
public class City {
    /* id of the city - ideally this matches the location of the city in the arraylist*/
    final int id;

    /* the name of the city */
    final String name;

    /* the colour of the city */
    final Colour colour;

    /* the neighbouring cities id */
    final ArrayList<Integer> neighbours;

    /* temporary holder while building cities */
    final private static ArrayList<City> cities = new ArrayList<>();

    /**
     * Represents a city
     * @param id the id of the city
     * @param name the name of the city
     * @param colour the colour of the city
     */
     private City (int id, String name, Colour colour) {
        this.id = id;
        this.name = name;
        this.colour = colour;
        neighbours = new ArrayList<>();
    }

    /**
     * Creates a copy of all the cities
     * @return an array list of cities
     */
    public static ArrayList<City> getCities() {
        return cities;
    }

    /**
     * Gets the colour of the city
     * @return the city colour
     */
    public Colour getColour() {
        return colour;
    }

    /**
     * Gets all the neighbours of a city
     * @return a list of city ids
     */
    public ArrayList<Integer> getNeighbours() {
        return neighbours;
    }

    /**
     * Gets the city id which is its index in the array list.
     * @return city id (index in the array)
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of the city
     * @return the city name
     */
    public String getName() {
        return name;
    }

    /**
     * Adds an edges to a city.
     * @param id the id of the city
     * @throws Exception when city already exists as neighbour
     */
    public void addEdge(int id) throws Exception {
        if(neighbours.contains(id)) {
            System.out.println(neighbours);
            System.out.println(id);
            throw new Exception("Neighbour has already been attached to this city");
        }

        neighbours.add(id);
    }

    /**
     * Connects two cities together
     * @param firstCity the first city to connect
     * @param secondCity the second city to connect
     * @throws Exception if the cities are null
     */
    public static void connect(City firstCity, City secondCity) throws Exception {
         if (firstCity == null || secondCity == null) {
             throw new NullPointerException("first or second city is null");
         }

         // connecting both cities togther
         firstCity.addEdge(secondCity.getId());
         secondCity.addEdge(firstCity.getId());
    }

    /**
     * Connects two cities together
     * @param firstCityName the name of the first city
     * @param secondCityName the name of the second city
     * @throws Exception if the cities doesn't exist
     */
    public static void connect(String firstCityName, String secondCityName) throws Exception {
        connect(getCityByName(cities, firstCityName), getCityByName(cities, secondCityName));
    }

    /**
     * Adds a city to the static city list
     * @param id id of the city
     * @param name the name of the city
     * @param colour the colour of the city
     */
    public static void addCity(int id, String name, Colour colour) {
        cities.add(new City(id, name, colour));
    }

    /**
     * Retrieves a city by the name.
     * @param cities a list of cities
     * @param name the name of the city to retrieve
     * @return the city or a null pointer
     */
    public static City getCityByName(ArrayList<City> cities, String name) {
         for (var city : cities) {
             if(city.name.equals(name))
                 return city;
         }

         return null;
    }
}

















