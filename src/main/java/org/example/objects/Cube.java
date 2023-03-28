package org.example.objects;

import org.example.enumerables.Colour;

import java.util.ArrayList;

public class Cube {
    private final Colour colour;
    private final int id;
    private int city = -1;


    private static ArrayList<Cube> cubes = new ArrayList<>();


    private Cube(int id, Colour colour) {
        this.id = id;
        this.colour = colour;
    }

    public void setCity(int city) throws Exception {
        if (city != -1) {
            throw new Exception("Cube " + id + " is already placed on the board.");
        }

        this.city = city;
    }

    public Colour getColour() {
        return colour;
    }

    public int getCity() {
        return city;
    }

    public int getId() {
        return id;
    }

    public void remove() {
        this.city = 0;
    }


    public static void addCube(int id, Colour colour) {
        cubes.add(new Cube(id, colour));
    }

    public ArrayList<Cube> getCubes() throws Exception {
        var newCubes = new ArrayList<Cube>();

        for (var cube : cubes) {
            new Cube(cube.getId(), cube.getColour());
            cube.setCity(cube.getCity());
            newCubes.add(cube);
        }

        return newCubes;
    }
}
