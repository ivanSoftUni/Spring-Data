package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.MechanicImportDto;
import softuni.exam.models.entity.Mechanic;
import softuni.exam.repository.MechanicRepository;
import softuni.exam.service.MechanicService;
import softuni.exam.util.ValidationUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import static softuni.exam.constants.Messages.INVALID_MECHANIC;
import static softuni.exam.constants.Messages.SUCCESSFULLY_IMPORT_MECHANIC;
import static softuni.exam.constants.Paths.MECHANICS_JSON_IMPORT_PATH;

@Service
public class MechanicServiceImpl implements MechanicService {
    private final MechanicRepository mechanicRepository;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;
    private final Gson gson;

    @Autowired
    public MechanicServiceImpl(MechanicRepository mechanicRepository,
                               ValidationUtils validationUtils,
                               ModelMapper modelMapper,
                               Gson gson) {
        this.mechanicRepository = mechanicRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return this.mechanicRepository.count() > 0;
    }

    @Override
    public String readMechanicsFromFile() throws IOException {
        return Files.readString(MECHANICS_JSON_IMPORT_PATH);
    }

    @Override
    public String importMechanics() throws IOException {
        StringBuilder sb = new StringBuilder();
        FileReader fileReader = new FileReader(MECHANICS_JSON_IMPORT_PATH.toFile());
        MechanicImportDto[] mechanicImportDtos = gson.fromJson(fileReader, MechanicImportDto[].class);
        for (MechanicImportDto dto : mechanicImportDtos) {
            boolean isValid = validationUtils.isValid(dto);

            if (isValid) {
                Optional<Mechanic> byEmail = this.mechanicRepository.findByEmail(dto.getEmail());
                if (byEmail.isPresent()) {
                    sb.append(INVALID_MECHANIC);
                } else {
                    Mechanic mechanicToDb = this.modelMapper.map(dto, Mechanic.class);
                    this.mechanicRepository.saveAndFlush(mechanicToDb);
                    sb.append(String.format(SUCCESSFULLY_IMPORT_MECHANIC,
                            mechanicToDb.getFirstName(),
                            mechanicToDb.getLastName()));
                }
            } else {
                sb.append(INVALID_MECHANIC);
            }
        }

        return sb.toString();
    }
}
