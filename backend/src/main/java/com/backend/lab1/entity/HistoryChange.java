package com.backend.lab1.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "history_change")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryChange {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    @NotNull
    @NotBlank
    private String login;

    @Column(name="human_being_id", nullable = false)
    @NotNull
    private Long humanBeingId;

    @Column(name="updated_at", nullable = false)
    @NotNull
    private java.time.LocalDateTime updatedAt;
}
