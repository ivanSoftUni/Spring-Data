package softuni.exam.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class ApplicationBeanConfiguration {
    @Bean
    public ModelMapper modelMapper() {

        ModelMapper mapper = new ModelMapper();

//        mapper.addConverter(new Converter<String, LocalDate>() {
//            @Override
//            public LocalDate convert(MappingContext<String, LocalDate> mappingContext) {
//
//                LocalDate parse = LocalDate
//                        .parse(mappingContext.getSource(),
//                                DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//
//                return parse;
//            }
//        });
        mapper.addConverter(new Converter<String, Time>() {
            @Override
            public Time convert(MappingContext<String, Time> mappingContext) {
                Time parse = Time.valueOf(mappingContext.getSource());
                return parse;
            }
        });

//        modelMapper.addConverter((Converter<String, LocalDateTime>) mappingContext -> {
//            LocalDateTime parse = LocalDateTime.parse(mappingContext.getSource(),
//                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//            return parse;
//        });

        return mapper;
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();
    }
}
