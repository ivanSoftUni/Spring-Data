package softuni.exam.models.dto;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CountryImportDto {

    @Expose
    @Size(min = 2, max = 60)
    @NotNull
    private String countryName;

    @Expose
    @Size(min = 2, max = 20)
    @NotNull
    private String currency;

}
