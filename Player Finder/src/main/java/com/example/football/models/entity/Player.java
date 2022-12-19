package com.example.football.models.entity;

import com.example.football.constants.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "players")
public class Player extends BaseEntity {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Position position;

    @ManyToOne
    private Town town;

    @ManyToOne
    private Team team;

    @ManyToOne
    private Stat stat;

    @Override
    public String toString() {
        return String.format("Player - %s %s%n" +
                        "      Position - %s%n" +
                        "      Team - %s%n" +
                        "      Stadium - %s%n",
                this.firstName,
                this.lastName,
                this.position,
                this.team.getName(),
                this.team.getStadiumName());
    }
}
