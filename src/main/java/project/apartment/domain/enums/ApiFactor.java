package project.apartment.domain.enums;

import lombok.Getter;


public enum ApiFactor {

    FLOOR("층"),
    MONTH("월"),
    YEAR("년"),
    DAY("일"),
    AREA("전용면적"),
    CODE("지역코드"),
    PRICE("거래금액"),
    EMD("법정동"),
    NAME("아파트");

    final private String name;

    public String getName() {
        return name;
    }

    private ApiFactor(String name) {
        this.name = name;
    }

}
