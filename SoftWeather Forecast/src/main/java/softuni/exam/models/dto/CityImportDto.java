package softuni.exam.models.dto;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.models.entity.Country;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CityImportDto {

    @Expose
    @Size(min = 2, max = 60)
    @NotNull
    private String cityName;

    @Expose
    @Size(min = 2)
    private String description;

    @Expose
    @Min(500)
    @NotNull
    private Integer population;

    @Expose
    @NotNull
    private Long country;
}
