package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CountryImportDto;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CountryService;
import softuni.exam.util.ValidationUtils;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import static softuni.exam.constants.Messages.INVALID_COUNTRY;
import static softuni.exam.constants.Messages.SUCCESSFULLY_IMPORT_COUNTRY;
import static softuni.exam.constants.Paths.COUNTRIES_JSON_IMPORT_PATH;

@Service
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;
    private final Gson gson;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository, ValidationUtils validationUtils, ModelMapper modelMapper, Gson gson) {
        this.countryRepository = countryRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }


    @Override
    public boolean areImported() {
        return this.countryRepository.count() > 0;
    }

    @Override
    public String readCountriesFromFile() throws IOException {
        return Files.readString(COUNTRIES_JSON_IMPORT_PATH);
    }

    @Override
    public String importCountries() throws IOException {
        StringBuilder sb = new StringBuilder();
        FileReader fileReader = new FileReader(COUNTRIES_JSON_IMPORT_PATH.toFile());
        CountryImportDto[] countryImportDtos = gson.fromJson(fileReader, CountryImportDto[].class);
        for (CountryImportDto dto : countryImportDtos) {
            boolean isValid = validationUtils.isValid(dto);

            if (isValid) {
                Country countryToDb = this.modelMapper.map(dto, Country.class);
                Optional<Country> doesExistCountry = this.countryRepository.findByCountryName(countryToDb.getCountryName());
                if (doesExistCountry.isPresent()) {
                    sb.append(INVALID_COUNTRY);
                } else {
                    this.countryRepository.saveAndFlush(countryToDb);
                    sb.append(String.format(SUCCESSFULLY_IMPORT_COUNTRY, countryToDb.getCountryName(), countryToDb.getCurrency()));
                }
            } else {
                sb.append(INVALID_COUNTRY);
            }
        }
        return sb.toString();
    }
}
