package com.flowbill.project.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class CreateStoryRequest {
    @NotBlank
    @Size(min = 10, max = 255, message = "Title must be between 10 and 255 characters")
    private String title;

    @NotBlank
    @Size(min = 50, max = 5000, message = "Description must be between 50 and 5000 characters")
    private String description;

    @NotNull(message = "Estimation is required")
    @Min(value = 1, message = "Estimation must be at least 1")
    private Integer estimation; // Story Points

    @NotNull(message = "MoSCoW priority is required")
    private String moscowPriority; // MUST_HAVE, SHOULD_HAVE, COULD_HAVE, WONT_HAVE

    // WSJF (optional)
    @Min(1)
    @Max(10)
    private Integer businessValue;
    @Min(1)
    @Max(10)
    private Integer timeCriticality;
    @Min(1)
    @Max(10)
    private Integer riskReduction;

    @NotEmpty(message = "At least one acceptance criteria is required")
    private List<String> acceptanceCriteria;

    private List<String> competencies;
    private List<Long> dependencyIds; // IDs of blocking stories
}
