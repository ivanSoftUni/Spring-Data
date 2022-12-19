package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.model.entities.Apartment;
import softuni.exam.model.entities.Town;

import java.util.Optional;

// TODO:
@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {

    Optional<Apartment> findByArea(Double area);

    Optional<Apartment> findApartmentByTownAndArea(Optional<Town> existTown, Double area);
}
