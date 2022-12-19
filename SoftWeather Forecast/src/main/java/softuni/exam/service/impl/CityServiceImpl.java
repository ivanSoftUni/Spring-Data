package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CityImportDto;
import softuni.exam.models.entity.City;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.CityRepository;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CityService;
import softuni.exam.util.ValidationUtils;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import static softuni.exam.constants.Messages.INVALID_CITY;
import static softuni.exam.constants.Messages.SUCCESSFULLY_IMPORT_CITY;
import static softuni.exam.constants.Paths.CITIES_JSON_IMPORT_PATH;

@Service
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;
    private final Gson gson;

    @Autowired
    public CityServiceImpl(CityRepository cityRepository,
                           CountryRepository countryRepository,
                           ValidationUtils validationUtils,
                           ModelMapper modelMapper,
                           Gson gson) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return this.cityRepository.count() > 0;
    }

    @Override
    public String readCitiesFileContent() throws IOException {
        return Files.readString(CITIES_JSON_IMPORT_PATH);
    }

    @Override
    public String importCities() throws IOException {
        StringBuilder sb = new StringBuilder();
        FileReader fileReader = new FileReader(CITIES_JSON_IMPORT_PATH.toFile());
        CityImportDto[] cityImportDtos = gson.fromJson(fileReader, CityImportDto[].class);

        for (CityImportDto dto : cityImportDtos) {
            boolean isValid = validationUtils.isValid(dto);

            if (isValid) {
                City cityToDb = this.modelMapper.map(dto, City.class);
                Optional<City> doesExistCity = this.cityRepository.findByCityName(cityToDb.getCityName());
                if (doesExistCity.isPresent()) {
                    sb.append(INVALID_CITY);
                } else {
                    Country country = this.countryRepository.findById(dto.getCountry()).get();
                    cityToDb.setCountry(country);
                    this.cityRepository.saveAndFlush(cityToDb);
                    sb.append(String.format(SUCCESSFULLY_IMPORT_CITY, cityToDb.getCityName(), cityToDb.getPopulation()));
                }
            } else {
                sb.append(INVALID_CITY);
            }
        }
        return sb.toString();
    }
}
