package org.board.enumerables;

/**
 * All the colour in the application.
 */
public enum Colour {
    Invalid,
    Red,
    Blue,
    Yellow,
    Black;

    /**
     * Converts a string to colour.
     * @param colour the string version of the colour
     * @return colour
     */
    static public Colour getColour(String colour) {
        return Colour.valueOf(String.join("", colour.split("\\s")));
    }

    /**
     * Converts to human-readable string
     * @return a human-readable string.
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (char rune : this.name().toCharArray()) {
            if (rune < 97 && !builder.isEmpty()) {
                builder.append(" ");
            }

            builder.append(rune);
        }

        return builder.toString();
    }
}
