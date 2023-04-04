package org.board.entities;

import org.board.enumerables.Colour;

import java.util.ArrayList;

/**
 * Represents a cube in the game.
 */
public class Cube {
    /* the colour of the cube*/
    private final Colour colour;

    /* id of the cube (matches the index in the array)*/
    private final int id;

    /* the id of the city the cube is on (-1 represents no city) */
    private int city = -1;

    /* List of cubes used during the building stage */
    private static final ArrayList<Cube> cubes = new ArrayList<>();

    /**
     * Sets the id and the colour of the cube
     * @param id the id of the cube (which is the index in the list)
     * @param colour the colour of the cube
     */
    private Cube(int id, Colour colour) {
        this.id = id;
        this.colour = colour;
    }

    /**
     * Gets the colour of the cube
     * @return the colour of the cube
     */
    public Colour getColour() {
        return colour;
    }

    /**
     * Gets the city id
     * @return the city id
     */
    public int getCity() {
        return city;
    }

    /**
     * Gets the id of the cube
     * @return the cube id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets all the cubes on the board
     * @return the old cubes on the board
     */
    public static ArrayList<Cube> getCubes() {
        return cubes;
    }

    /**
     * Creates a clone of the cubes.
     * @param cubes cubes to clone
     * @return a clone of the cubes
     * @throws Exception if there is an error setting cities
     */
    public static ArrayList<Cube> getCubes(ArrayList<Cube> cubes) throws Exception {
        var newCubes = new ArrayList<Cube>();

        for (var cube : cubes) {
            var newCube = new Cube(cube.getId(), cube.getColour());
            newCube.setCity(cube.getCity());
            newCubes.add(newCube);
        }

        return newCubes;
    }

    /**
     * Gets an empty cube from a list of cubes
     * @param cubes list of cubes
     * @param colour colour of empty cube
     * @return an empty cube or a null pointer
     */
    public static Cube getEmptyCube(ArrayList<Cube> cubes, Colour colour) {
        for (var cube : cubes) {
            if (cube.colour == colour && cube.empty()) {
                return cube;
            }
        }

        return null;
    }

    /**
     * Sets a city
     * @param city the id of the city
     * @throws Exception the cube already has a city.
     */
    public void setCity(int city) throws Exception {
        if (!empty()) {
            throw new Exception("Cube " + id + " is already placed on the board.");
        }

        this.city = city;
    }

    /**
     * Removes a city from the cube (removing the cube off the board)
     */
    public void remove() {
        this.city = -1;
    }

    /**
     * Checks if the cube isn't on the board
     * @return true if the city is empty
     */
    public boolean empty() {
        return this.city == -1;
    }

    /**
     * Adds a new cube to the internal cube builder
     * @param id the id of the cubes
     * @param colour the colour of the cube
     */
    public static void addCube(int id, Colour colour) {
        cubes.add(new Cube(id, colour));
    }

    /**
     * Converts cube to a human-readable format.
     * @return a string representation of a cube
     */
    @Override
    public String toString() {
        return "Cube " + id + " city " + city;
    }
}
