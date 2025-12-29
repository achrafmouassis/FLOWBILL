package com.flowbill.reporting.dto;

import java.util.List;

public class AlertResponse {
    private String id;
    private String type; // SPRINT_AT_RISK, OVERLOAD, BLOCKED_TASK, etc.
    private String severity; // CRITICAL, WARNING, INFO
    private String message;
    private Long referenceId; // Sprint ID, Task ID, or User ID
    private List<String> actions;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public AlertResponse(String id, String type, String severity, String message, Long refId, List<String> actions) {
        this.id = id;
        this.type = type;
        this.severity = severity;
        this.message = message;
        this.referenceId = refId;
        this.actions = actions;
    }
}
