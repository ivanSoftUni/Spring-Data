package exam.service.impl;


import exam.model.dtos.TownImportDto;
import exam.model.dtos.TownImportWrapperDto;
import exam.model.entities.Town;
import exam.repository.TownRepository;
import exam.service.TownService;
import exam.utils.ValidationUtils;
import exam.utils.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static exam.constants.Messages.*;
import static exam.constants.Paths.TOWNS_XML_PATH;

@Service
public class TownServiceImpl implements TownService {

    private final TownRepository townRepository;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;


    @Autowired
    public TownServiceImpl(TownRepository townRepository, ValidationUtils validationUtils, ModelMapper modelMapper) {
        this.townRepository = townRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        return Files.readString(TOWNS_XML_PATH);
    }

    @Override
    public String importTowns() throws JAXBException, FileNotFoundException {

        final StringBuilder sb = new StringBuilder();

        final File file = TOWNS_XML_PATH.toFile();

        final TownImportWrapperDto townImportWrapperDto = XmlParser.fromFile(file, TownImportWrapperDto.class);

        final List<TownImportDto> towns = townImportWrapperDto.getTowns();

        for (TownImportDto town : towns) {
            boolean isValid = validationUtils.isValid(town);

            if (isValid) {
                if (!townRepository.findByName(town.getName()).isPresent()) {
                    final Town townToDb = this.modelMapper.map(town, Town.class);
                    sb.append(String.format(SUCCESSFULLY_IMPORT_TOWN, townToDb.getName()));
                    this.townRepository.save(townToDb);
                }
            } else {
                sb.append(String.format(INVALID_TOWN));
            }
        }

        return sb.toString();
    }
}
