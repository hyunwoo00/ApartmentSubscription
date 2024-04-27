package project.apartment.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import project.apartment.domain.enums.SubscriptionStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
public class Subscription {

    @Id
    @GeneratedValue
    @Column(name = "subscription_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    private LocalDateTime subDate;

    @Enumerated(value = EnumType.STRING)
    private SubscriptionStatus status;

    //==연관 관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getSubscriptionList().add(this);
    }


    //==생성 메서드==//
    /**
     * 구독
     */
    public static Subscription createSubscription(Member member, Apartment apartment, LocalDateTime dateTime) {
        Subscription subscription = new Subscription();
        subscription.setMember(member);
        subscription.setApartment(apartment);
        subscription.setSubDate(dateTime);
        subscription.setStatus(SubscriptionStatus.SUBSCRIBE);

        return subscription;
    }

    //==비즈니스 로직==//

    /**
     * 구독 취소
     */
    public void removeSubscription() {
        if (status == SubscriptionStatus.SUBSCRIBE) {
            this.setStatus(SubscriptionStatus.CANCEL);
        }
    }


}
