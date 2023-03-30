package org.board.enumerables;

public enum PlayerChoice {
    PerformAction,
    ViewCards,
    ConsultAgent,
    ViewBoardState,
    QuitGame;

    static public PlayerChoice getChoice(String role) {
        return PlayerChoice.valueOf(String.join("", role.split("\\s")));
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
