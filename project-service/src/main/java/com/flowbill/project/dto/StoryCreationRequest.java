package com.flowbill.project.dto;

import lombok.Data;
import java.util.List;

@Data
public class StoryCreationRequest {
    private String title;
    private String description;
    private Integer estimation; // Story Points
    private String moscowPriority; // MUST_HAVE, SHOULD_HAVE, etc.

    private Integer businessValue;
    private Integer timeCriticality;
    private Integer riskReduction;

    private List<String> competencies; // e.g. "Java", "React"
    private List<String> acceptanceCriteria; // List of criteria descriptions
    private List<Long> blockerIds; // IDs of tasks that block this story

    private Long projectId;
    private Long sprintId;
}
