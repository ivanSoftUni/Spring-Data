package com.example.football.service.impl;

import com.example.football.models.dto.StatImportDto;
import com.example.football.models.dto.StatImportWrapperDto;
import com.example.football.models.entity.Stat;
import com.example.football.repository.StatRepository;
import com.example.football.service.StatService;
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
import java.util.List;
import java.util.Optional;

import static com.example.football.constants.Messages.INVALID_STAT;
import static com.example.football.constants.Messages.SUCCESSFULLY_IMPORT_STAT;
import static com.example.football.constants.Paths.STATS_XML_IMPORT_PATH;

@Service
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtils validationUtils;


    @Autowired
    public StatServiceImpl(StatRepository statRepository, ModelMapper modelMapper, ValidationUtils validationUtils) {
        this.statRepository = statRepository;
        this.modelMapper = modelMapper;
        this.validationUtils = validationUtils;
    }


    @Override
    public boolean areImported() {
        return this.statRepository.count() > 0;
    }

    @Override
    public String readStatsFileContent() throws IOException {
        return Files.readString(STATS_XML_IMPORT_PATH);
    }

    @Override
    public String importStats() throws JAXBException, FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        File file = STATS_XML_IMPORT_PATH.toFile();
        StatImportWrapperDto statImportWrapperDto = XmlParser.fromFile(file, StatImportWrapperDto.class);
        List<StatImportDto> dtoStats = statImportWrapperDto.getStats();
        for (StatImportDto dto : dtoStats) {
            boolean isValid = validationUtils.isValid(dto);
            if (isValid) {
                Stat statToDb = this.modelMapper.map(dto, Stat.class);
                Optional<Stat> existStat = this.statRepository.findByShootingAndPassingAndEndurance(
                        statToDb.getShooting(), statToDb.getPassing(), statToDb.getEndurance());
                if (existStat.isPresent()) {
                    sb.append(INVALID_STAT);
                } else {
                    this.statRepository.saveAndFlush(statToDb);
                    sb.append(String.format(SUCCESSFULLY_IMPORT_STAT,
                            statToDb.getShooting(),
                            statToDb.getPassing(),
                            statToDb.getEndurance()));
                }
            } else {
                sb.append(INVALID_STAT);
            }
        }
        return sb.toString();
    }
}
