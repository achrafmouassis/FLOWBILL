package com.flowbill.project.enums;

/**
 * General task priority levels.
 */
public enum TaskPriority {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    CRITICAL("Critical", 4);

    private final String displayName;
    private final int level;

    TaskPriority(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    public static TaskPriority fromString(String value) {
        if (value == null) {
            return MEDIUM; // Default
        }
        for (TaskPriority priority : TaskPriority.values()) {
            if (priority.name().equalsIgnoreCase(value)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid TaskPriority: " + value);
    }
}
