package org.board.enumerables;

public enum Action {
    DriveOrFerry,
    DirectFlight,
    CharterFlight,
    ShuttleFlight;

    static public Action getAction(String action) {
        return Action.valueOf(String.join("", action.split("\\s")));
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
