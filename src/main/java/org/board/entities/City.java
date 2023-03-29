package org.board.entities;


import org.board.enumerables.Colour;

import java.util.ArrayList;


public class City {
    final int id;
    final String name;
    final Colour colour;
    final ArrayList<Integer> neighbours;

    final private static ArrayList<City> cities = new ArrayList<>();

     private City (int id, String name, Colour colour) {
        this.id = id;
        this.name = name;
        this.colour = colour;
        neighbours = new ArrayList<>();
    }

    public Colour getColour() {
        return colour;
    }

    public ArrayList<Integer> getNeighbours() {
        return neighbours;
    }

    public void addEdge(int id) throws Exception {
        if(neighbours.contains(id)) {
            throw new Exception("Neighbour has already been attached to this city");
        }

        neighbours.add(id);
    }

    public int getId() {
        return id;
    }

    /* Static Methods */

    public static void addCity(int id, String name, Colour colour) {
         cities.add(new City(id, name, colour));
    }

    public static ArrayList<City> getCities() throws Exception {
        var newCities = new ArrayList<City>();

        for (var city : cities) {
            var newCity = new City(city.id, city.name, city.colour);

            for (var neighbour : city.neighbours) {
                newCity.addEdge(neighbour);
            }

            newCities.add(newCity);
        }

        return newCities;
    }

    public static void connect(City firstCity, City secondCity) throws Exception {
         if (firstCity == null || secondCity == null) {
             throw new NullPointerException("first or second city is null");
         }

         firstCity.addEdge(secondCity.getId());
         secondCity.addEdge(firstCity.getId());
    }

    public static void connect(String firstCityName, String secondCityName) throws Exception {
        connect(getCityByName(firstCityName), getCityByName(secondCityName));
    }


    public static City getCityByName(String name) {
         for (var city : cities) {
             if(city.name.equals(name))
                 return city;
         }

         return null;
    }
}

















