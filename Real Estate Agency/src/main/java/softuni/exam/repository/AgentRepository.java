package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.model.entities.Agent;

import java.util.Optional;

// TODO:
@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

    Optional<Agent> findAgentByFirstName(String firstName);

    Optional<Agent> findAgentByEmail(String email);
}
