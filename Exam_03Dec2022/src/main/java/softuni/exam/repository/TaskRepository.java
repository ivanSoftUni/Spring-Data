package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.constants.CarType;
import softuni.exam.models.entity.Task;

import java.util.List;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByCar_CarTypeLikeOrderByPriceDesc(CarType carType);

}
