package org.board.enumerables;

/**
 * Represents roles in the game
 */
public enum Role {
    ContingencyPlanner,
    Dispatcher,
    Medic,
    OperationsExpert,
    QuarantineSpecialist,
    Researcher,
    Scientist;

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

    /**
     * Converts role to colour
     * @param role the user role
     * @return the role.
     */
    static public Role getRole(String role) {
        return Role.valueOf(String.join("", role.split("\\s")));
    }
}
