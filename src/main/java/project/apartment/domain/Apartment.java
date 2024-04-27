package project.apartment.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueNameAndAddress", columnNames = {"name", "emd"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Apartment {

    @Id
    @GeneratedValue
    @Column(name = "apartment_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApartmentSale> apartmentSaleList = new ArrayList<>();


    //==연관 관계 메서드==//
    public void addApartmentSale(ApartmentSale apartmentSale) {
        apartmentSaleList.add(apartmentSale);
        apartmentSale.setApartment(this);
    }


    //==생성 메서드==//
    public static Apartment createApartment(String name, Address address) {
        Apartment apartment = new Apartment();
        apartment.setAddress(address);
        apartment.setName(name);
        return apartment;
    }
}
