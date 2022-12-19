package exam.model.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "towns")
@XmlAccessorType(XmlAccessType.FIELD)
public class TownImportWrapperDto {

    @XmlElement(name = "town")
    private List<TownImportDto> towns;

    public TownImportWrapperDto() {
    }

    public TownImportWrapperDto(List<TownImportDto> towns) {
        this.towns = towns;
    }

    public List<TownImportDto> getTowns() {
        return towns;
    }

    public void setTowns(List<TownImportDto> towns) {
        this.towns = towns;
    }
}
