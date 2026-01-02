package com.flowbill.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoryBriefDTO {
    private Long id;
    private String title;
    private Integer storyPoints;
}
