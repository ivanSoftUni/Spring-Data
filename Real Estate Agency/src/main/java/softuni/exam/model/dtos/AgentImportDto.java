package softuni.exam.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgentImportDto {

    @Size(min = 2)
    private String firstName;

    @Size(min = 2)
    private String lastName;

    private String town;

    @Email
    private String email;


}
