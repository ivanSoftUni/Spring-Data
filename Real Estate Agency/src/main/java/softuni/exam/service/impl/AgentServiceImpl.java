package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.model.dtos.AgentImportDto;
import softuni.exam.model.entities.Agent;
import softuni.exam.model.entities.Town;
import softuni.exam.repository.AgentRepository;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.AgentService;
import softuni.exam.utils.ValidationUtils;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import static softuni.exam.constants.Messages.INVALID_AGENT;
import static softuni.exam.constants.Messages.SUCCESSFULLY_IMPORT_AGENT;
import static softuni.exam.constants.Paths.AGENTS_JSON_PATH;

@Service
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final TownRepository townRepository;
    private final ValidationUtils validationUtils;
    private final Gson gson;
    private final ModelMapper modelMapper;


    @Autowired
    public AgentServiceImpl(AgentRepository agentRepository, TownRepository townRepository, ValidationUtils validationUtils, Gson gson, ModelMapper modelMapper) {
        this.agentRepository = agentRepository;
        this.townRepository = townRepository;
        this.validationUtils = validationUtils;
        this.gson = gson;
        this.modelMapper = modelMapper;
    }


    @Override
    public boolean areImported() {
        return this.agentRepository.count() > 0;
    }

    @Override
    public String readAgentsFromFile() throws IOException {
        return Files.readString(AGENTS_JSON_PATH);
    }

    @Override
    public String importAgents() throws IOException {

        StringBuilder sb = new StringBuilder();
        final FileReader fileReader = new FileReader(AGENTS_JSON_PATH.toFile());
        AgentImportDto[] agentImportDtos = gson.fromJson(fileReader, AgentImportDto[].class);

        for (AgentImportDto dto : agentImportDtos) {
            boolean isValid = validationUtils.isValid(dto);
            if (isValid) {
                Agent agentToDb = this.modelMapper.map(dto, Agent.class);
                Optional<Agent> agentByFirstNameExist = this.agentRepository.findAgentByFirstName(agentToDb.getFirstName());
                Optional<Agent> agentByEmailExist = this.agentRepository.findAgentByEmail(agentToDb.getEmail());
                Optional<Town> town = this.townRepository.findTownByTownName(dto.getTown());

                if (agentByFirstNameExist.isPresent() || agentByEmailExist.isPresent() || !town.isPresent()) {
                    sb.append(INVALID_AGENT);
                } else {
                    agentToDb.setTown(town.get());
                    this.agentRepository.saveAndFlush(agentToDb);
                    sb.append(String.format(SUCCESSFULLY_IMPORT_AGENT, agentToDb.getFirstName(), agentToDb.getLastName()));
                }
            } else {
                sb.append(INVALID_AGENT);
            }
        }
        return sb.toString();
    }
}
