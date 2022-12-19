package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.model.entities.Town;

import java.util.Optional;

// TODO:
@Repository
public interface TownRepository extends JpaRepository<Town, Long> {

    Optional<Town> findTownByTownName(String name);
}
