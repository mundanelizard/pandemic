package org.example.logic;


import org.example.enumerables.Colour;
import org.example.objects.City;

import java.io.File;
import java.util.Scanner;

public class Pandemic {
    private int remainingResearchStations = 6;

    // Research Stations
    final private int TOTAL_RESEARCH_STATIONS = 6;

    // Disease Cube
    final private int TOTAL_DISEASE_CUBES = 94;
    final private int TOTAL_DISEASE_CUBE_COLORS = 4;
    final private int TOTAL_CUBES_PER_COLOR = 24;

    final private String FILENAME = "map.txt";

    void load() {
        try {
            var start = 0;
            var handle = new File(FILENAME);
            var reader = new Scanner(handle);

            start = loadCityGraph(reader, start);
            start = loadInfectionDeck(reader, start);

            loadPlayerCards(reader, start);
        } catch(Exception ex) {
            System.out.println("An error occurred reading the city graph.");
            ex.printStackTrace();
        }
    }

    private void initialise() {
        infectCities();
    }

    void start() {
        boolean isRunning = true;
        initialise();
    }

    private int loadCityGraph(Scanner reader, int start) throws Exception {
        start = instantiateCities(reader, start);
        start = instantiateConnection(reader, start);

        return start;
    }

    private int instantiateCities(Scanner reader, int start) {
        while(reader.hasNextLine()) {
            start += 1;
            var line = reader.nextLine();
            if (line.equals("--")) break;

            var segments = line.split(";");

            var name = segments[0];
            var colour = segments[1];

            City.addCity(start, name, Colour.getColour(colour));
        }

        return start;
    }

    private int instantiateConnection(Scanner reader, int start) throws Exception {
        while(reader.hasNextLine()) {
            start += 1;
            var line = reader.nextLine();
            if (line.equals("--")) break;

            var segments = line.split(";");

            var firstCity = segments[0];
            var secondCity = segments[0];

            City.connect(firstCity, secondCity);
        }

        return start;
    }
}
