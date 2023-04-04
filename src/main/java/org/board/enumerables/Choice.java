package org.board.enumerables;

/**
 * Represents choices of card in first round
 */
public enum Choice {
    PerformAction,
    ViewCards,
    ConsultAgent,
    ViewBoardState,
    QuitGame;

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
