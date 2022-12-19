package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CarsImportDto;
import softuni.exam.models.dto.CarsImportWrapperDto;
import softuni.exam.models.entity.Car;
import softuni.exam.repository.CarRepository;
import softuni.exam.service.CarService;
import softuni.exam.util.ValidationUtils;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static softuni.exam.constants.Messages.INVALID_CAR;
import static softuni.exam.constants.Messages.SUCCESSFULLY_IMPORT_CAR;
import static softuni.exam.constants.Paths.CARS_XML_IMPORT_PATH;

@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;


    @Autowired
    public CarServiceImpl(CarRepository carRepository, ValidationUtils validationUtils, ModelMapper modelMapper) {
        this.carRepository = carRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.carRepository.count() > 0;
    }

    @Override
    public String readCarsFromFile() throws IOException {
        return Files.readString(CARS_XML_IMPORT_PATH);
    }
    @Override
    public String importCars() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();

        File file = CARS_XML_IMPORT_PATH.toFile();
        CarsImportWrapperDto carsImportWrapperDto = XmlParser.fromFile(file, CarsImportWrapperDto.class);
        List<CarsImportDto> cars = carsImportWrapperDto.getCars();

        for (CarsImportDto dto : cars) {
            boolean isValid = validationUtils.isValid(dto);
            if (isValid) {
                Optional<Car> doesExistCar = this.carRepository.findByPlateNumber(dto.getPlateNumber());
                if (doesExistCar.isPresent()) {
                    sb.append(INVALID_CAR);
                } else {
                    Car carToDb = this.modelMapper.map(dto, Car.class);
                    this.carRepository.saveAndFlush(carToDb);
                    sb.append(String.format(SUCCESSFULLY_IMPORT_CAR, carToDb.getCarMake(), carToDb.getCarModel()));
                }
            } else {
                sb.append(INVALID_CAR);
            }
        }

        return sb.toString();
    }
}
