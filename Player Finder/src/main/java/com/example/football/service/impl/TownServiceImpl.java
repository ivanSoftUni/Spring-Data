package com.example.football.service.impl;

import com.example.football.models.dto.TownImportDto;
import com.example.football.models.entity.Town;
import com.example.football.repository.TownRepository;
import com.example.football.service.TownService;
import com.example.football.util.ValidationUtils;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import static com.example.football.constants.Messages.INVALID_TOWN;
import static com.example.football.constants.Messages.SUCCESSFULLY_IMPORT_TOWN;
import static com.example.football.constants.Paths.TOWNS_JSON_IMPORT_PATH;


@Service
public class TownServiceImpl implements TownService {
    private final TownRepository townRepository;
    private final ValidationUtils validationUtils;
    private final Gson gson;
    private final ModelMapper modelMapper;


    @Autowired
    public TownServiceImpl(TownRepository townRepository, ValidationUtils validationUtils, Gson gson, ModelMapper modelMapper) {
        this.townRepository = townRepository;
        this.validationUtils = validationUtils;
        this.gson = gson;
        this.modelMapper = modelMapper;
    }


    @Override
    public boolean areImported() {
        return this.townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        return Files.readString(TOWNS_JSON_IMPORT_PATH);
    }

    @Override
    public String importTowns() throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        FileReader fileReader = new FileReader(TOWNS_JSON_IMPORT_PATH.toFile());
        TownImportDto[] townImportDtos = gson.fromJson(fileReader, TownImportDto[].class);

        for (TownImportDto importDto : townImportDtos) {
            boolean isValid = validationUtils.isValid(importDto);

            if (isValid) {
                Optional<Town> existTown = this.townRepository.findByName(importDto.getName());
                if (existTown.isPresent()) {
                    sb.append(INVALID_TOWN);
                } else {
                    Town townToDb = this.modelMapper.map(importDto, Town.class);
                    this.townRepository.saveAndFlush(townToDb);
                    sb.append(String.format(SUCCESSFULLY_IMPORT_TOWN, townToDb.getName(), townToDb.getPopulation()));
                }
            } else {
                sb.append(INVALID_TOWN);
            }
        }
        return sb.toString();
    }
}
