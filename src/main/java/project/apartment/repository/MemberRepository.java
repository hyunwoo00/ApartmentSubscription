package project.apartment.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import project.apartment.domain.Member;
import project.apartment.redis.CacheNames;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class  MemberRepository{

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    //@Cacheable(cacheNames = CacheNames.USERBYEMAIL, key = "'login'+#p0", unless = "#result==null")
    public Member findByEmail(String email) {
        return em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getSingleResult();
    }


    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public void delete(Member member) {
        em.remove(member);
    }




}
