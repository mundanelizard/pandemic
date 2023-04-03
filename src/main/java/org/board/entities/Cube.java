package org.board.entities;

import org.board.enumerables.Colour;

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
        if (!empty()) {
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
        this.city = -1;
    }

    public boolean empty() {
        return this.city == -1;
    }

    /* Static Methods */

    public static void addCube(int id, Colour colour) {
        cubes.add(new Cube(id, colour));
    }

    public static ArrayList<Cube> getCubes() throws Exception {
        return getCubes(cubes);
    }

    public static ArrayList<Cube> getCubes(ArrayList<Cube> cubes) throws Exception {
        var newCubes = new ArrayList<Cube>();

        for (var cube : cubes) {
            new Cube(cube.getId(), cube.getColour());
            cube.setCity(cube.getCity());
            newCubes.add(cube);
        }

        return newCubes;
    }

    public static Cube getEmptyCube(ArrayList<Cube> cubes, Colour colour) {
        for (var cube : cubes) {
            if (cube.colour == colour && cube.empty()) {
                return cube;
            }
        }

        return null;
    }
}
