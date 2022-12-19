package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.constants.ApartmentType;
import softuni.exam.model.entities.Offer;

import java.util.List;

// TODO:
@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    //    @Query(value = "SELECT concat_WS(' ', a.first_name, a.last_name) , o.id, a2.area, t.town_name\n" +
//            " FROM offers o\n" +
//            "         left join agents a on a.id = o.agent_id\n" +
//            "         left join apartments a2 on a2.id = o.apartment_id\n" +
//            "         left join towns t on t.id = a2.town_id\n" +
//            " where a2.apartment_type = 'three_rooms'\n" +
//            " order by a2.area DESC, o.price", nativeQuery = true)
    List<Offer> findByApartment_ApartmentTypeOrderByApartment_AreaDescPriceAsc(ApartmentType apartmentType);
}
