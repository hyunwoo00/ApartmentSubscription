package project.apartment.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "Apartment_Sales")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApartmentSale {
    @Id
    @GeneratedValue
    @Column(name = "aptSale_id")
    private Long id;

    //거래금액
    private Long price;
    //계약 체결일
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    //전용 면적
    private Double size;
    //층
    private int floor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;


    public static ApartmentSale createApartmentSale(Long price, LocalDate date, Double size, int floor) {
        ApartmentSale apartmentSale = new ApartmentSale();

        apartmentSale.setDate(date);
        apartmentSale.setFloor(floor);
        apartmentSale.setSize(size);
        apartmentSale.setPrice(price);

        return apartmentSale;
    }
}
