package project.apartment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    private String siDo; //시, 도
    private String sgg; //시, 군, 구
    @Column(name = "emd")
    private String emd;// 읍, 면, 동

    /**
     * 외부에서 new Address()를 호출하고 Setter로 값을 설정하는 것을 막기 위함.
     * 아래 생성자를 통해서만 Address를 생성하게 만듬.
     */
    public static Address createAddress(String siDo, String sgg, String emd) {
        Address address = new Address();
        address.setSiDo(siDo);
        address.setSgg(sgg);
        address.setEmd(emd);

        return address;
    }
}
