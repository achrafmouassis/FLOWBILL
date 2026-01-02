package com.flowbill.project.enums;

/**
 * Enum for task status workflow.
 * Represents the lifecycle of a task from creation to completion.
 */
public enum TaskStatus {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    DONE("Done"),
    BLOCKED("Blocked");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Parse string to enum, case-insensitive.
     * 
     * @param value the string value
     * @return the corresponding TaskStatus
     * @throws IllegalArgumentException if value is invalid
     */
    public static TaskStatus fromString(String value) {
        if (value == null) {
            return TODO; // Default
        }
        for (TaskStatus status : TaskStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid TaskStatus: " + value);
    }
}
