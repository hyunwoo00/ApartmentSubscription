package project.apartment.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.apartment.domain.Apartment;
import project.apartment.domain.Member;
import project.apartment.domain.Subscription;
import project.apartment.domain.enums.SubscriptionStatus;
import project.apartment.repository.ApartmentRepository;
import project.apartment.repository.MemberRepository;
import project.apartment.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;

import static project.apartment.domain.Subscription.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final MemberRepository memberRepository;
    private final ApartmentRepository apartmentRepository;

    /**
     * 구독
     */
    @Transactional
    public Long subscribe(Long memberId, Long aptId) {
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Apartment apt = apartmentRepository.findOne(aptId);

        //구독 생성
        LocalDateTime time = LocalDateTime.now();
        Subscription subscription = createSubscription(member, apt, time);

        subscriptionRepository.save(subscription);
        return subscription.getId();
    }

    /**
     * 구독 취소
     */
    @Transactional
    public void cancelSubscription(Long subId) {
        Subscription sub = subscriptionRepository.findOne(subId);
        sub.removeSubscription();
    }

    /**
     * 구독 내역 조회
     */
    public List<Subscription> findSubscriptions(Long memberId) {
        return subscriptionRepository.findByMemberId(memberId)
                .stream()
                //사용자의 구독취소 정보는 데이터베이스에 저장돼있기 때문에 구독된 정보만 필터링해서 가져옴.
                .filter(subscription -> subscription.getStatus().equals(SubscriptionStatus.SUBSCRIBE))
                //.collect(Collectors.toList())와 기능 동일. Java 16부터 지원.
                .toList();
    }
}
