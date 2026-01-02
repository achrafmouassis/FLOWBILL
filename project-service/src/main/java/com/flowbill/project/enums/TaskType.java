package com.flowbill.project.enums;

/**
 * Task type classification in Agile workflow.
 */
public enum TaskType {
    STORY("User Story"),
    TASK("Technical Task"),
    BUG("Bug Fix");

    private final String displayName;

    TaskType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TaskType fromString(String value) {
        if (value == null) {
            return TASK; // Default
        }
        for (TaskType type : TaskType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid TaskType: " + value);
    }
}
