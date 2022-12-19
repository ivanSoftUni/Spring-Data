package exam.service.impl;

import exam.model.dtos.ShopImportDto;
import exam.model.dtos.ShopImportWrapperDto;
import exam.model.entities.Shop;
import exam.model.entities.Town;
import exam.repository.ShopRepository;
import exam.repository.TownRepository;
import exam.service.ShopService;
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
import java.util.NoSuchElementException;

import static exam.constants.Messages.*;
import static exam.constants.Paths.SHOPS_XML_PATH;

@Service
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final TownRepository townRepository;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;


    @Autowired
    public ShopServiceImpl(ShopRepository shopRepository, TownRepository townRepository, ValidationUtils validationUtils, ModelMapper modelMapper) {
        this.shopRepository = shopRepository;
        this.townRepository = townRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.shopRepository.count() > 0;
    }

    @Override
    public String readShopsFileContent() throws IOException {
        return Files.readString(SHOPS_XML_PATH);
    }

    @Override
    public String importShops() throws JAXBException, FileNotFoundException {

        final StringBuilder sb = new StringBuilder();
        final File file = SHOPS_XML_PATH.toFile();
        final ShopImportWrapperDto shopImportWrapperDto = XmlParser.fromFile(file, ShopImportWrapperDto.class);
        final List<ShopImportDto> shops = shopImportWrapperDto.getShops();
        for (ShopImportDto shop : shops) {
            boolean isValid = validationUtils.isValid(shop);
            if (isValid) {
                if (shopRepository.findShopByName(shop.getName()).isPresent()) {
                    sb.append(INVALID_SHOP);
                } else {
                    Shop shopToDb = this.modelMapper.map(shop, Shop.class);
                    Town town = this.townRepository.findByName(shopToDb.getTown().getName())
                            .orElseThrow(NoSuchElementException::new);

                    shopToDb.setTown(town);

                    sb.append(String.format(SUCCESSFULLY_IMPORT_SHOP, shop.getName(), shop.getIncome()));
                    this.shopRepository.save(shopToDb);
                }
            } else {
                sb.append(INVALID_SHOP);
            }
        }

        return sb.toString();
    }
}
