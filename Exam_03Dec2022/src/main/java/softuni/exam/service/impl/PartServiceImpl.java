package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.PartsImportDto;
import softuni.exam.models.entity.Part;
import softuni.exam.repository.PartRepository;
import softuni.exam.service.PartService;
import softuni.exam.util.ValidationUtils;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import static softuni.exam.constants.Messages.INVALID_PART;
import static softuni.exam.constants.Messages.SUCCESSFULLY_IMPORT_PART;
import static softuni.exam.constants.Paths.PARTS_JSON_IMPORT_PATH;

@Service
public class PartServiceImpl implements PartService {
    private final PartRepository partRepository;
    private final ValidationUtils validationUtils;
    private final Gson gson;
    private final ModelMapper modelMapper;


    @Autowired
    public PartServiceImpl(PartRepository partRepository, ValidationUtils validationUtils, Gson gson, ModelMapper modelMapper) {
        this.partRepository = partRepository;
        this.validationUtils = validationUtils;
        this.gson = gson;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.partRepository.count() > 0;
    }

    @Override
    public String readPartsFileContent() throws IOException {
        return Files.readString(PARTS_JSON_IMPORT_PATH);
    }

    @Override
    public String importParts() throws IOException {
        StringBuilder sb = new StringBuilder();
        FileReader fileReader = new FileReader(PARTS_JSON_IMPORT_PATH.toFile());
        PartsImportDto[] partsImportDtos = gson.fromJson(fileReader, PartsImportDto[].class);
        for (PartsImportDto dto : partsImportDtos) {
            boolean isValid = validationUtils.isValid(dto);

            if (isValid) {
                Optional<Part> byPartName = this.partRepository.findByPartName(dto.getPartName());

                if (byPartName.isPresent()) {
                    sb.append(INVALID_PART);
                } else {
                    Part partToDb = this.modelMapper.map(dto, Part.class);
                    this.partRepository.saveAndFlush(partToDb);
                    sb.append(String.format(SUCCESSFULLY_IMPORT_PART, partToDb.getPartName(), partToDb.getPrice()));
                }
            } else {
                sb.append(INVALID_PART);
            }
        }
        return sb.toString();
    }
}
