package softuni.exam.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task extends BaseEntity {

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne
    @Fetch(FetchMode.JOIN)
    private Part part;

    @ManyToOne
    @Fetch(FetchMode.JOIN)
    private Mechanic mechanic;

    @ManyToOne
    @Fetch(FetchMode.JOIN)
    private Car car;

    @Override
    public String toString() {
        return String.format("Car %s %s with %dkm%n" +
                        "-Mechanic: %s %s - task №%d:%n" +
                        "--Engine: %.1f%n" +
                        "---Price: %.2f$%n",
                this.car.getCarMake(),
                this.car.getCarModel(),
                this.car.getKilometers(),
                this.mechanic.getFirstName(),
                this.mechanic.getLastName(),
                this.getId(),
                this.car.getEngine(),
                this.getPrice());
    }
}
