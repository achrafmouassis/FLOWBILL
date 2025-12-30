package com.flowbill.project.dto;

import lombok.Data;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

@Data
public class CreateTasksBatchRequest {
    @NotEmpty
    @Valid
    private List<CreateTaskRequest> tasks;
}
