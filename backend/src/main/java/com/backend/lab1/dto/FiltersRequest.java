package com.backend.lab1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FiltersRequest {
    private String name;
    private String carName;
    private String mood;
    private String weaponType;
    private Integer page;
    private Integer size;
}
