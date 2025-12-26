package com.flowbill.time.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "time_entries")
@Data
public class TimeEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long taskId;
    private Long userId;
    private Double hours;
    private LocalDateTime date;
    private boolean approved;
}
