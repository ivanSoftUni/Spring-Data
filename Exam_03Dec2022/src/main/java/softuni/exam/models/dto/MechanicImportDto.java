package softuni.exam.models.dto;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MechanicImportDto {

    @Expose
    @Size(min = 2)
    @NotNull
    private String firstName;

    @Expose
    @Size(min = 2)
    @NotNull
    private String lastName;

    @Expose
    @Email
    @NotNull
    private String email;

    @Expose
    @Size(min = 2)
    private String phone;
}
