package softuni.exam.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.model.entities.Agent;
import softuni.exam.model.entities.Apartment;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "offer")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferImportDto {

    @XmlElement
    @Positive
    private BigDecimal price;

    @XmlElement(name = "agent")
    @NotNull
    private AgentNameDto agent;

    @XmlElement(name = "apartment")
    @NotNull
    private ApartmentIdDto apartment;

    @XmlElement
    @NotNull
    private String publishedOn;


}
