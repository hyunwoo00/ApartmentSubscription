package project.apartment.repository;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.apartment.domain.Region;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RegionRepository {


    private final EntityManager em;

    public void save(Region region) {
        em.persist(region);
    }

    public Region findOne(Long id) {
        return em.find(Region.class, id);
    }

    public Region findByCode(int code) {
        return em.createQuery("select r from Region r where r.code = :code", Region.class)
                .setParameter("code", code)
                .getSingleResult();
    }
    public List<Region> getRegion(){
        return em.createQuery("select r from Region r", Region.class)
                .getResultList();

    }


}
