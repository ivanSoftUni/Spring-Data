package softuni.exam.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.constants.DayOfWeek;

import javax.persistence.*;
import java.sql.Time;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "forecasts")
public class Forecast extends BaseEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private Double maxTemperature;

    @Column(nullable = false)
    private Double minTemperature;

    @Column(nullable = false)
    private Time sunrise;

    @Column(nullable = false)
    private Time sunset;

    @ManyToOne
    private City city;

    @Override
    public String toString() {
        return String.format("City: %s:%n" +
                        "-min temperature: %.2f%n" +
                        "--max temperature: %.2f%n" +
                        "---sunrise: %s%n" +
                        "----sunset: %s%n",
                this.city.getCityName(),
                this.minTemperature,
                this.maxTemperature,
                this.sunrise,
                this.sunset);
    }
}
