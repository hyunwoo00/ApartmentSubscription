package project.apartment.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region {
    @Id
    @GeneratedValue
    @Column(name = "region_id")
    private Long id;

    private String sido;
    private String sgg;
    private int code; //법정동에 따른 코드

    private void setSido(String sido) {
        this.sido = sido;
    }

    private void setSgg(String sgg) {
        this.sgg = sgg;
    }

    private void setCode(int code) {
        this.code = code;
    }

    //==생성 메서드==//
    public static Region createRegion(String sido, String sgg, int code) {
        Region region = new Region();
        region.setSido(sido);
        region.setSgg(sgg);
        region.setCode(code);

        return region;
    }
}
