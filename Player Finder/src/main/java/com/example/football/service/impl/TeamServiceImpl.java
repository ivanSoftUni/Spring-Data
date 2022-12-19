package com.example.football.service.impl;

import com.example.football.models.dto.TeamImportDto;
import com.example.football.models.entity.Team;
import com.example.football.models.entity.Town;
import com.example.football.repository.TeamRepository;
import com.example.football.repository.TownRepository;
import com.example.football.service.TeamService;
import com.example.football.util.ValidationUtils;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import static com.example.football.constants.Messages.INVALID_TEAM;
import static com.example.football.constants.Messages.SUCCESSFULLY_IMPORT_TEAM;
import static com.example.football.constants.Paths.TEAMS_JSON_IMPORT_PATH;

@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final TownRepository townRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtils validationUtils;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository, TownRepository townRepository, Gson gson, ModelMapper modelMapper, ValidationUtils validationUtils) {
        this.teamRepository = teamRepository;
        this.townRepository = townRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;

        this.validationUtils = validationUtils;
    }

    @Override
    public boolean areImported() {
        return this.teamRepository.count() > 0;
    }

    @Override
    public String readTeamsFileContent() throws IOException {
        return Files.readString(TEAMS_JSON_IMPORT_PATH);
    }

    @Override
    public String importTeams() throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        FileReader fileReader = new FileReader(TEAMS_JSON_IMPORT_PATH.toFile());
        TeamImportDto[] teamImportDtos = gson.fromJson(fileReader, TeamImportDto[].class);

        for (TeamImportDto dto : teamImportDtos) {
            boolean isValid = validationUtils.isValid(dto);
            if (isValid) {
                Optional<Team> existTeam = this.teamRepository.findByName(dto.getName());
                if (existTeam.isPresent()) {
                    sb.append(INVALID_TEAM);
                } else {
                    Team teamToDb = this.modelMapper.map(dto, Team.class);
                    Town town = this.townRepository.findByName(dto.getTownName()).get();
                    teamToDb.setTown(town);
                    this.teamRepository.saveAndFlush(teamToDb);
                    sb.append(String.format(SUCCESSFULLY_IMPORT_TEAM, teamToDb.getName(), teamToDb.getFanBase()));
                }
            } else {
                sb.append(INVALID_TEAM);
            }
        }

        return sb.toString();
    }
}
