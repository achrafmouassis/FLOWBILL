package com.flowbill.project.enums;

/**
 * MoSCoW prioritization method for Agile backlog management.
 * MUST have - Critical requirements
 * SHOULD have - Important but not vital
 * COULD have - Desirable but not necessary
 * WON'T have - Agreed will not be delivered
 */
public enum MoscowPriority {
    MUST_HAVE("Must Have", 1),
    SHOULD_HAVE("Should Have", 2),
    COULD_HAVE("Could Have", 3),
    WONT_HAVE("Won't Have", 4);

    private final String displayName;
    private final int sortOrder;

    MoscowPriority(String displayName, int sortOrder) {
        this.displayName = displayName;
        this.sortOrder = sortOrder;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public static MoscowPriority fromString(String value) {
        if (value == null) {
            return null;
        }
        for (MoscowPriority priority : MoscowPriority.values()) {
            if (priority.name().equalsIgnoreCase(value)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid MoscowPriority: " + value);
    }
}
