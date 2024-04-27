package project.apartment.repository;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.apartment.domain.Subscription;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepository {

    private final EntityManager em;

    public void save(Subscription subscription) {
        em.persist(subscription);
    }

    public Subscription findOne(Long id) {
        return em.find(Subscription.class, id);
    }

    public List<Subscription> findByMemberId(Long memId) {
        return em.createQuery("select s from Subscription s where s.member.id = :memId", Subscription.class)
                .setParameter("memId", memId)
                .getResultList();
    }

}
