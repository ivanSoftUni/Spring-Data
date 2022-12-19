package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.model.dtos.TownImportDto;
import softuni.exam.model.entities.Town;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.TownService;
import softuni.exam.utils.ValidationUtils;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

import static softuni.exam.constants.Messages.INVALID_TOWN;
import static softuni.exam.constants.Messages.SUCCESSFULLY_IMPORT_TOWN;
import static softuni.exam.constants.Paths.TOWNS_JSON_PATH;

@Service
public class TownServiceImpl implements TownService {

    private final TownRepository townRepository;

    private final ModelMapper modelMapper;
    private final Gson gson;
    private final ValidationUtils validationUtils;


    @Autowired
    public TownServiceImpl(TownRepository townRepository, ModelMapper modelMapper, Gson gson, ValidationUtils validationUtils) {
        this.townRepository = townRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validationUtils = validationUtils;
    }


    @Override
    public boolean areImported() {
        return this.townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        return Files.readString(TOWNS_JSON_PATH);
    }

    @Override
    public String importTowns() throws IOException {

        StringBuilder sb = new StringBuilder();
        final FileReader fileReader = new FileReader(TOWNS_JSON_PATH.toFile());
        final TownImportDto[] townImportDtos = gson.fromJson(fileReader, TownImportDto[].class);

        for (TownImportDto town : townImportDtos) {
            boolean isValid = validationUtils.isValid(town);

            if (isValid) {
                Town townToDb = this.modelMapper.map(town, Town.class);
                this.townRepository.saveAndFlush(townToDb);
                sb.append(String.format(SUCCESSFULLY_IMPORT_TOWN, townToDb.getTownName(), townToDb.getPopulation()));
            } else {
                sb.append(INVALID_TOWN + System.lineSeparator());
            }
        }
        return sb.toString();
    }
}
