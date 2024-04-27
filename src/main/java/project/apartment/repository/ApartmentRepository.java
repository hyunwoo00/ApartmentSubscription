package project.apartment.repository;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.apartment.domain.Apartment;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApartmentRepository {

    private final EntityManager em;

    public void save(Apartment apartment) {
        em.persist(apartment);
    }

    public Apartment findOne(Long id) {
        return em.find(Apartment.class, id);
    }

    public List<Apartment> findByDong(String dong){ //읍면동으로 아파트 조회하기.
        return em.createQuery("select a from Apartment  a where a.address.emd = :dong", Apartment.class)
                .setParameter("dong", dong)
                .getResultList();
    }

    public List<Apartment> findBySiDoSgg(String siDo, String sgg) {
        return em.createQuery("select a from Apartment a where a.address.siDo = :siDO and a.address.sgg = :sgg", Apartment.class)
                .setParameter("siDO", siDo)
                .setParameter("sgg", sgg)
                .getResultList();
    }

    public Apartment findByNameAndDong(String name, String dong) {
        return em.createQuery("select a from Apartment  a where a.address.emd = :dong and a.name = :name", Apartment.class)
                .setParameter("dong", dong)
                .setParameter("name", name)
                .getSingleResult();
    }


}

