package softuni.exam.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "car")
@XmlAccessorType(XmlAccessType.FIELD)
public class CarsImportDto {

    @XmlElement
    @NotNull
    private String carType;

    @XmlElement
    @NotNull
    @Size(min = 2, max = 30)
    private String carMake;

    @XmlElement
    @NotNull
    @Size(min = 2, max = 30)
    private String carModel;

    @XmlElement
    @NotNull
    @Positive
    private Integer year;

    @XmlElement
    @NotNull
    @Size(min = 2, max = 30)
    private String plateNumber;

    @XmlElement
    @NotNull
    @Positive
    private Integer kilometers;

    @XmlElement
    @NotNull
    @Min(1)
    private Double engine;
}
