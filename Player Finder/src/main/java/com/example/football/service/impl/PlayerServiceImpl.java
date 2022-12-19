package com.example.football.service.impl;

import com.example.football.models.dto.PlayerImportDto;
import com.example.football.models.dto.PlayerImportWrapperDto;
import com.example.football.models.entity.Player;
import com.example.football.models.entity.Stat;
import com.example.football.models.entity.Team;
import com.example.football.models.entity.Town;
import com.example.football.repository.PlayerRepository;
import com.example.football.repository.StatRepository;
import com.example.football.repository.TeamRepository;
import com.example.football.repository.TownRepository;
import com.example.football.service.PlayerService;
import com.example.football.util.ValidationUtils;
import com.example.football.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.example.football.constants.Messages.INVALID_PLAYER;
import static com.example.football.constants.Messages.SUCCESSFULLY_IMPORT_PLAYER;
import static com.example.football.constants.Paths.PLAYERS_XML_IMPORT_PATH;

@Service
public class PlayerServiceImpl implements PlayerService {
    private LocalDate after = LocalDate.parse("01/01/1995", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    private LocalDate before = LocalDate.parse("01/01/2003", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    private final PlayerRepository playerRepository;
    private final TownRepository townRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;
    private final StatRepository statRepository;
    private final ValidationUtils validationUtils;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository, TownRepository townRepository, TeamRepository teamRepository, ModelMapper modelMapper, StatRepository statRepository, ValidationUtils validationUtils) {
        this.playerRepository = playerRepository;
        this.townRepository = townRepository;
        this.teamRepository = teamRepository;
        this.modelMapper = modelMapper;
        this.statRepository = statRepository;
        this.validationUtils = validationUtils;
    }


    @Override
    public boolean areImported() {
        return this.playerRepository.count() > 0;
    }

    @Override
    public String readPlayersFileContent() throws IOException {
        return Files.readString(PLAYERS_XML_IMPORT_PATH);
    }

    @Override
    public String importPlayers() throws JAXBException, FileNotFoundException {
        StringBuilder sb = new StringBuilder();

        File file = PLAYERS_XML_IMPORT_PATH.toFile();
        PlayerImportWrapperDto playerImportWrapperDto = XmlParser.fromFile(file, PlayerImportWrapperDto.class);
        List<PlayerImportDto> dtoPlayers = playerImportWrapperDto.getPlayers();
        for (PlayerImportDto dto : dtoPlayers) {
            boolean isValid = validationUtils.isValid(dto);
            if (isValid) {
                Optional<Player> existPlayerByEmail = this.playerRepository.findByEmail(dto.getEmail());
                if (existPlayerByEmail.isPresent()) {
                    sb.append(INVALID_PLAYER);
                } else {
                    Town town = this.townRepository.findByName(dto.getTown().getName()).get();
                    Team team = this.teamRepository.findByName(dto.getTeam().getName()).get();
                    Stat stat = this.statRepository.findById(dto.getStat().getId()).get();
                    Player playerToDb = this.modelMapper.map(dto, Player.class);
                    playerToDb.setTown(town);
                    playerToDb.setTeam(team);
                    playerToDb.setStat(stat);
                    this.playerRepository.saveAndFlush(playerToDb);
                    sb.append(String.format(SUCCESSFULLY_IMPORT_PLAYER,
                            playerToDb.getFirstName(),
                            playerToDb.getLastName(),
                            playerToDb.getPosition()));
                }
            } else {
                sb.append(INVALID_PLAYER);
            }
        }
        return sb.toString();
    }

    @Override
    public String exportBestPlayers() {
        StringBuilder sb = new StringBuilder();

        List<Player> players = this.playerRepository.findAllByBirthDateBetweenOrderByStat_ShootingDescStat_PassingDescStat_EnduranceDescLastName(after, before);
        for (Player player : players) {
            sb.append(player.toString());
        }
        return sb.toString();
    }
}
