package com.example.football.models.dto;


import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TownImportDto {

    @Expose
    @Size(min = 2)
    private String name;

    @Expose
    @Positive
    private Integer population;

    @Expose
    @Size(min = 10)
    private String travelGuide;
}
