package softuni.exam.repository;

import org.springframework.beans.PropertyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.constants.DayOfWeek;
import softuni.exam.models.entity.Forecast;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast, Long> {

    Optional<Forecast> findByDayOfWeekAndCity_Id(DayOfWeek dayOfWeek, Long city);

    @Query("select f from Forecast f where f.dayOfWeek like 'SUNDAY' And f.city.population<150000" +
            " order by f.maxTemperature DESC , f.id ASC")
    List<Forecast> findForecastsOrderByMaxTemperatureDescIdAsc();
}
