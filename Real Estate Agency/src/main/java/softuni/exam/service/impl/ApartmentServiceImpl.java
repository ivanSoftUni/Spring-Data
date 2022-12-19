package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.model.dtos.ApartmentImportDto;
import softuni.exam.model.dtos.ApartmentImportWrapperDto;
import softuni.exam.model.entities.Apartment;
import softuni.exam.model.entities.Town;
import softuni.exam.repository.ApartmentRepository;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.ApartmentService;
import softuni.exam.utils.ValidationUtils;
import softuni.exam.utils.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static softuni.exam.constants.Messages.INVALID_APARTMENT;
import static softuni.exam.constants.Messages.SUCCESSFULLY_IMPORT_APARTMENT;
import static softuni.exam.constants.Paths.APARTMENTS_XML_PATH;

@Service
public class ApartmentServiceImpl implements ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final TownRepository townRepository;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;


    @Autowired
    public ApartmentServiceImpl(ApartmentRepository apartmentRepository, TownRepository townRepository, ValidationUtils validationUtils, ModelMapper modelMapper) {
        this.apartmentRepository = apartmentRepository;
        this.townRepository = townRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
    }


    @Override
    public boolean areImported() {
        return this.apartmentRepository.count() > 0;
    }

    @Override
    public String readApartmentsFromFile() throws IOException {
        return Files.readString(APARTMENTS_XML_PATH);
    }

    @Override
    public String importApartments() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        File file = APARTMENTS_XML_PATH.toFile();

        ApartmentImportWrapperDto apartmentImportWrapperDto = XmlParser.fromFile(file, ApartmentImportWrapperDto.class);
        List<ApartmentImportDto> apartments = apartmentImportWrapperDto.getApartments();

        for (ApartmentImportDto apartmentDto : apartments) {
            boolean isValid = this.validationUtils.isValid(apartmentDto);
            if (isValid) {
                Optional<Town> existTown = this.townRepository.findTownByTownName(apartmentDto.getTown());
                Optional<Apartment> apartmentByTownAndArea = this.apartmentRepository.findApartmentByTownAndArea(existTown, apartmentDto.getArea());

                if (apartmentByTownAndArea.isPresent()) {
                    sb.append(INVALID_APARTMENT);
                } else {
                    Apartment apartmentToDb = this.modelMapper.map(apartmentDto, Apartment.class);
                    Town town = this.townRepository.findTownByTownName(apartmentDto.getTown()).get();

                    apartmentToDb.setTown(town);
                    this.apartmentRepository.save(apartmentToDb);

                    sb.append(String.format(SUCCESSFULLY_IMPORT_APARTMENT,
                            apartmentToDb.getApartmentType(),
                            apartmentToDb.getArea()));
                }
            } else {
                sb.append(INVALID_APARTMENT);
            }
        }
        return sb.toString();
    }
}
