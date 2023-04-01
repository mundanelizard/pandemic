package org.board.enumerables;

public enum Choice {
    PerformAction,
    ViewCards,
    ConsultAgent,
    ViewBoardState,
    QuitGame;

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
