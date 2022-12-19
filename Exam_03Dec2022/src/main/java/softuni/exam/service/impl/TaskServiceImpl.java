package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.constants.CarType;
import softuni.exam.models.dto.TasksImportDto;
import softuni.exam.models.dto.TasksImportWrapperDto;
import softuni.exam.models.entity.Car;
import softuni.exam.models.entity.Mechanic;
import softuni.exam.models.entity.Part;
import softuni.exam.models.entity.Task;
import softuni.exam.repository.CarRepository;
import softuni.exam.repository.MechanicRepository;
import softuni.exam.repository.PartRepository;
import softuni.exam.repository.TaskRepository;
import softuni.exam.service.TaskService;
import softuni.exam.util.ValidationUtils;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static softuni.exam.constants.Messages.INVALID_TASK;
import static softuni.exam.constants.Messages.SUCCESSFULLY_IMPORT_TASK;
import static softuni.exam.constants.Paths.TASKS_XML_IMPORT_PATH;

@Service
public class TaskServiceImpl implements TaskService {
    private final CarRepository carRepository;
    private final TaskRepository taskRepository;
    private final MechanicRepository mechanicRepository;
    private final PartRepository partRepository;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;


    @Autowired
    public TaskServiceImpl(CarRepository carRepository,
                           TaskRepository taskRepository,
                           MechanicRepository mechanicRepository,
                           PartRepository partRepository,
                           ValidationUtils validationUtils,
                           ModelMapper modelMapper) {
        this.carRepository = carRepository;
        this.taskRepository = taskRepository;
        this.mechanicRepository = mechanicRepository;
        this.partRepository = partRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
    }


    @Override
    public boolean areImported() {
        return this.taskRepository.count() > 0;
    }

    @Override
    public String readTasksFileContent() throws IOException {
        return Files.readString(TASKS_XML_IMPORT_PATH);
    }

    @Override
    public String importTasks() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        File file = TASKS_XML_IMPORT_PATH.toFile();
        TasksImportWrapperDto tasksImportWrapperDto = XmlParser.fromFile(file, TasksImportWrapperDto.class);
        List<TasksImportDto> tasks = tasksImportWrapperDto.getTasks();
        for (TasksImportDto dto : tasks) {
            boolean isValid = validationUtils.isValid(dto);
            if (isValid) {
                Optional<Mechanic> existMechanic = this.mechanicRepository.findByFirstName(dto.getMechanic().getFirstName());
                if (existMechanic.isPresent()) {
                    Task taskToDb = this.modelMapper.map(dto, Task.class);
                    Car car = this.carRepository.findById(dto.getCar().getId()).get();
                    Part part = this.partRepository.findById(dto.getPart().getId()).get();
                    taskToDb.setCar(car);
                    taskToDb.setMechanic(existMechanic.get());
                    taskToDb.setPart(part);
                    this.taskRepository.saveAndFlush(taskToDb);
                    sb.append(String.format(SUCCESSFULLY_IMPORT_TASK, taskToDb.getPrice()));
                } else {
                    sb.append(INVALID_TASK);
                }
            } else {
                sb.append(INVALID_TASK);
            }
        }
        return sb.toString();
    }

    @Override
    public String getCoupeCarTasksOrderByPrice() {
        StringBuilder sb = new StringBuilder();

        List<Task> tasks = this.taskRepository.findAllByCar_CarTypeLikeOrderByPriceDesc(CarType.coupe);
        for (Task task : tasks) {
            sb.append(task.toString());
        }
        return sb.toString();
    }
}
