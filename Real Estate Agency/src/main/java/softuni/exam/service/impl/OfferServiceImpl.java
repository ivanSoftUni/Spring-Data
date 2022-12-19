package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.model.dtos.OfferImportDto;
import softuni.exam.model.dtos.OfferImportWrapperDto;
import softuni.exam.model.entities.Agent;
import softuni.exam.model.entities.Apartment;
import softuni.exam.model.entities.Offer;
import softuni.exam.repository.AgentRepository;
import softuni.exam.repository.ApartmentRepository;
import softuni.exam.repository.OfferRepository;
import softuni.exam.service.OfferService;
import softuni.exam.utils.ValidationUtils;
import softuni.exam.utils.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static softuni.exam.constants.ApartmentType.three_rooms;
import static softuni.exam.constants.Messages.INVALID_OFFER;
import static softuni.exam.constants.Messages.SUCCESSFULLY_IMPORT_OFFER;
import static softuni.exam.constants.Paths.OFFERS_XML_PATH;

@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final AgentRepository agentRepository;
    private final ApartmentRepository apartmentRepository;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;


    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository, AgentRepository agentRepository, ApartmentRepository apartmentRepository, ValidationUtils validationUtils, ModelMapper modelMapper) {
        this.offerRepository = offerRepository;
        this.agentRepository = agentRepository;
        this.apartmentRepository = apartmentRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
    }


    @Override
    public boolean areImported() {
        return this.offerRepository.count() > 0;
    }

    @Override
    public String readOffersFileContent() throws IOException {
        return Files.readString(OFFERS_XML_PATH);
    }

    @Override
    public String importOffers() throws IOException, JAXBException {

        StringBuilder sb = new StringBuilder();
        final File file = OFFERS_XML_PATH.toFile();
        OfferImportWrapperDto offerImportWrapperDto = XmlParser.fromFile(file, OfferImportWrapperDto.class);
        List<OfferImportDto> dtoOffers = offerImportWrapperDto.getOffers();

        for (OfferImportDto dtoOffer : dtoOffers) {
            boolean isValid = validationUtils.isValid(dtoOffer);

            if (isValid) {
                Optional<Agent> agentByFirstName = this.agentRepository.findAgentByFirstName(dtoOffer.getAgent().getName());
                if (!agentByFirstName.isPresent()) {
                    sb.append(INVALID_OFFER);
                } else {
                    modelMapper.addConverter(mappingContext ->
                                    LocalDate.parse(mappingContext.getSource(), DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            String.class,
                            LocalDate.class);
                    Offer offerToDb = this.modelMapper.map(dtoOffer, Offer.class);
                    Apartment apartmentById = this.apartmentRepository.findById(dtoOffer.getApartment().getId()).get();
                    offerToDb.setAgent(agentByFirstName.get());
                    offerToDb.setApartment(apartmentById);
                    this.offerRepository.saveAndFlush(offerToDb);
                    sb.append(String.format(SUCCESSFULLY_IMPORT_OFFER, offerToDb.getPrice()));
                }
            } else {
                sb.append(INVALID_OFFER);
            }
        }
        return sb.toString();
    }

    @Override
    public String exportOffers() {

        StringBuilder sb = new StringBuilder();
        List<Offer> offers = this.offerRepository.findByApartment_ApartmentTypeOrderByApartment_AreaDescPriceAsc(three_rooms);
        for (Offer offer : offers) {
            sb.append(offer.toString());
        }
        return sb.toString();
    }
}
