package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ForecastImportDto;
import softuni.exam.models.dto.ForecastImportWrapperDto;
import softuni.exam.models.entity.City;
import softuni.exam.models.entity.Forecast;
import softuni.exam.repository.CityRepository;
import softuni.exam.repository.CountryRepository;
import softuni.exam.repository.ForecastRepository;
import softuni.exam.service.ForecastService;
import softuni.exam.util.ValidationUtils;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static softuni.exam.constants.Messages.INVALID_FORECAST;
import static softuni.exam.constants.Messages.SUCCESSFULLY_IMPORT_FORECAST;
import static softuni.exam.constants.Paths.FORECASTS_XML_IMPORT_PATH;

@Service
public class ForecastServiceImpl implements ForecastService {
    private final ForecastRepository forecastRepository;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;

    @Autowired
    public ForecastServiceImpl(ForecastRepository forecastRepository,
                               CountryRepository countryRepository,
                               CityRepository cityRepository,
                               ValidationUtils validationUtils,
                               ModelMapper modelMapper) {
        this.forecastRepository = forecastRepository;
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.forecastRepository.count() > 0;
    }

    @Override
    public String readForecastsFromFile() throws IOException {
        return Files.readString(FORECASTS_XML_IMPORT_PATH);
    }

    @Override
    public String importForecasts() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        File file = FORECASTS_XML_IMPORT_PATH.toFile();
        ForecastImportWrapperDto forecastImportWrapperDto = XmlParser.fromFile(file, ForecastImportWrapperDto.class);
        List<ForecastImportDto> forecasts = forecastImportWrapperDto.getForecasts();
        for (ForecastImportDto dto : forecasts) {
            boolean isValid = validationUtils.isValid(dto);
            if (isValid) {
                Optional<Forecast> doesExistForecast = this.forecastRepository.findByDayOfWeekAndCity_Id(dto.getDayOfWeek(), dto.getCity());
                if (doesExistForecast.isPresent()) {
                    sb.append(INVALID_FORECAST);
                } else {
                    Forecast forecastToDb = this.modelMapper.map(dto, Forecast.class);
                    City city = this.cityRepository.findById(dto.getCity()).get();
                    forecastToDb.setCity(city);
                    this.forecastRepository.saveAndFlush(forecastToDb);
                    sb.append(String.format(SUCCESSFULLY_IMPORT_FORECAST, forecastToDb.getDayOfWeek(), forecastToDb.getMaxTemperature()));
                }
            } else {
                sb.append(INVALID_FORECAST);
            }
        }
        return sb.toString();
    }

    @Override
    public String exportForecasts() {
        StringBuilder sb = new StringBuilder();

        this.forecastRepository.findForecastsOrderByMaxTemperatureDescIdAsc()
                .forEach(forecast -> sb.append(forecast.toString()));

        return sb.toString();
    }
}
