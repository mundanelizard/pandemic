package org.example.objects;


import org.example.enumerables.Colour;

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

    public void addEdge(int id) throws Exception {
        if(neighbours.contains(id)) {
            throw new Exception("Neighbour has already been attached to this city");
        }

        neighbours.add(id);
    }

    public int getId() {
        return id;
    }

    public static void addCity(int id, String name, Colour colour) {
         cities.add(new City(id, name, colour));
    }

    public static ArrayList<City> getCities() {
         return cities;
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

















