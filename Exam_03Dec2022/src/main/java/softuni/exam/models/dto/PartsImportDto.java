package softuni.exam.models.dto;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartsImportDto {

    @Expose
    @NotNull
    @Size(min = 2, max = 19)
    private String partName;

    @Expose
    @NotNull
    @Min(10)
    @Max(2000)
    private Double price;

    @Expose
    @NotNull
    @Positive
    private Integer quantity;
}
