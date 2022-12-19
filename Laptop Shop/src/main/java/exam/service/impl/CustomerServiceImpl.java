package exam.service.impl;

import com.google.gson.Gson;
import exam.model.dtos.CustomerImportDto;
import exam.model.entities.Customer;
import exam.model.entities.Town;
import exam.repository.CustomerRepository;
import exam.repository.TownRepository;
import exam.service.CustomerService;
import exam.utils.ValidationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static exam.constants.Messages.INVALID_CUSTOMER;
import static exam.constants.Messages.SUCCESSFULLY_IMPORT_CUSTOMER;
import static exam.constants.Paths.CUSTOMERS_JSON_PATH;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final TownRepository townRepository;
    private final Gson gson;

    private final ModelMapper modelMapper;
    private final ValidationUtils validationUtils;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository,
                               TownRepository townRepository,
                               Gson gson,
                               ValidationUtils validationUtils,
                               ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.townRepository = townRepository;
        this.gson = gson;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
    }


    @Override
    public boolean areImported() {
        return this.customerRepository.count() > 0;
    }

    @Override
    public String readCustomersFileContent() throws IOException {
        return Files.readString(CUSTOMERS_JSON_PATH);
    }

    @Override
    public String importCustomers() throws IOException {

        StringBuilder sb = new StringBuilder();

        final FileReader fileReader = new FileReader(CUSTOMERS_JSON_PATH.toFile());
        CustomerImportDto[] dtoList = gson.fromJson(fileReader, CustomerImportDto[].class);

        for (CustomerImportDto customer : dtoList) {
            boolean isValid = this.validationUtils.isValid(customer);
            if (isValid) {
                Customer customerToDb = this.modelMapper.map(customer, Customer.class);
                Optional<Customer> byEmail = this.customerRepository.findByEmail(customerToDb.getEmail());
                if (byEmail.isPresent()) {
                    sb.append(String.format(INVALID_CUSTOMER));
                } else {
                    Town town = this.townRepository.findByName(customerToDb.getTown().getName())
                            .orElseThrow(NoSuchElementException::new);

                    customerToDb.setTown(town);

                    this.customerRepository.save(customerToDb);
                    sb.append(String.format(SUCCESSFULLY_IMPORT_CUSTOMER, customerToDb.getFirstName(), customerToDb.getLastName(), customerToDb.getEmail()));
                }
            } else {
                sb.append(String.format(INVALID_CUSTOMER));
            }
        }
        return sb.toString();
    }
}
