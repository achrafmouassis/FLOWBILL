package com.flowbill.project.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateStoryRequest {
    @NotBlank
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @NotBlank
    @Size(min = 1, max = 5000, message = "Description must be between 1 and 5000 characters")
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

    private List<String> acceptanceCriteria;

    private List<Long> blockerIds; // IDs of blocking stories (matches frontend)

    @NotNull(message = "Assigned user is required")
    private Long assignedUserId;
}
