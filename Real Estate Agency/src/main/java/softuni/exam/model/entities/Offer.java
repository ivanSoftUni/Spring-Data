package softuni.exam.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "offers")
public class Offer extends BaseEntity {

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDate publishedOn;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    private Apartment apartment;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    private Agent agent;

    @Override
    public String toString() {
        return String.format("Agent %s %s with offer №%d:%n" +
                        "  -Apartment area: %.2f%n" +
                        "  --Town: %s%n" +
                        "  ---Price: %.2f$%n",
                this.agent.getFirstName(),
                this.agent.getLastName(),
                this.getId(),
                this.apartment.getArea(),
                this.apartment.getTown().getTownName(),
                this.getPrice());
    }
}
