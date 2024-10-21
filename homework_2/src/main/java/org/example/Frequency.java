package org.example;

public enum Frequency {
    DAILY("Ежедневно"),
    WEEKLY("Еженедельно");

    private final String displayName;

    Frequency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Frequency fromDisplayName(String displayName) {
        for (Frequency frequency : Frequency.values()) {
            if (frequency.getDisplayName().equals(displayName)) {
                return frequency;
            }
        }
        throw new IllegalArgumentException("Нет перечисления для: " + displayName);
    }
}

