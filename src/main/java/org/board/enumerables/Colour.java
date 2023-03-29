package org.board.enumerables;

public enum Colour {
    Red,
    Blue,
    Yellow,
    Black;

    static public Colour getColour(String role) {
        return Colour.valueOf(String.join("", role.split("\\s")));
    }

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
