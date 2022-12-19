package exam.service.impl;

import com.google.gson.Gson;
import exam.model.dtos.LaptopImportDto;
import exam.model.entities.Laptop;
import exam.model.entities.Shop;
import exam.repository.LaptopRepository;
import exam.repository.ShopRepository;
import exam.service.LaptopService;
import exam.utils.ValidationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static exam.constants.Messages.*;
import static exam.constants.Paths.LAPTOPS_JSON_PATH;

@Service
public class LaptopServiceImpl implements LaptopService {
    private final LaptopRepository laptopRepository;
    private final ShopRepository shopRepository;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;
    private final Gson gson;

    @Autowired
    public LaptopServiceImpl(LaptopRepository laptopRepository, ShopRepository shopRepository, ValidationUtils validationUtils, ModelMapper modelMapper, Gson gson) {
        this.laptopRepository = laptopRepository;
        this.shopRepository = shopRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }


    @Override
    public boolean areImported() {
        return this.laptopRepository.count() > 0;
    }

    @Override
    public String readLaptopsFileContent() throws IOException {
        return Files.readString(LAPTOPS_JSON_PATH);
    }

    @Override
    public String importLaptops() throws IOException {
        StringBuilder sb = new StringBuilder();
        final FileReader fileReader = new FileReader(LAPTOPS_JSON_PATH.toFile());
        LaptopImportDto[] laptopImportDtos = gson.fromJson(fileReader, LaptopImportDto[].class);

        for (LaptopImportDto laptopImportDto : laptopImportDtos) {
            boolean isValid = validationUtils.isValid(laptopImportDto);
            if (isValid) {
                Laptop laptopToDb = this.modelMapper.map(laptopImportDto, Laptop.class);
                Optional<Laptop> laptopByMac = this.laptopRepository.findByMacAddress(laptopToDb.getMacAddress());
                if (laptopByMac.isPresent()) {
                    sb.append(String.format(INVALID_LAPTOP));
                } else {
                    Shop shop = this.shopRepository.findShopByName(laptopToDb.getShop().getName())
                            .orElseThrow(NoSuchElementException::new);

                    laptopToDb.setShop(shop);
                    this.laptopRepository.save(laptopToDb);
                    sb.append(String.format(SUCCESSFULLY_IMPORT_LAPTOP,
                            laptopToDb.getMacAddress(),
                            laptopToDb.getCpuSpeed(),
                            laptopToDb.getRam(),
                            laptopToDb.getStorage()));
                }
            } else {
                sb.append(String.format(INVALID_LAPTOP));
            }
        }
        return sb.toString();
    }

    @Override
    public String exportBestLaptops() {
        StringBuilder sb = new StringBuilder();

        List<Laptop> bestLaptops = this.laptopRepository.findAllByOrderByCpuSpeedDescRamDescMacAddress().orElseThrow(NoSuchElementException::new);
        for (Laptop laptop : bestLaptops) {
            sb.append(String.format(LAPTOP_EXPORT_FORMAT,
                    laptop.getMacAddress(),
                    laptop.getCpuSpeed(),
                    laptop.getRam(),
                    laptop.getStorage(),
                    laptop.getPrice(),
                    laptop.getShop().getName(),
                    laptop.getShop().getTown().getName()));
        }
        return sb.toString();
    }
}
