package org.board.enumerables;

public enum Role {
    ContingencyPlanner,
    Dispatcher,
    Medic,
    OperationsExpert,
    QuarantineSpecialist,
    Researcher,
    Scientist;

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

    static public Role getRole(String role) {
        return Role.valueOf(String.join("", role.split("\\s")));
    }
}
