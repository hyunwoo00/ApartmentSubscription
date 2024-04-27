package project.apartment.batch;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import project.apartment.domain.Apartment;
import project.apartment.domain.Region;
import project.apartment.exception.region.RegionExceedException;
import project.apartment.repository.RegionRepository;
import project.apartment.service.ApartmentService;
import project.apartment.service.OpenApi;

import java.util.Map;

@Slf4j
public class CustomApartmentItemReader implements ItemReader<Map<String, Apartment>> {

    private final OpenApi openApi;
    private final RegionRepository regionRepository;
    private final ApartmentService apartmentService;

    public CustomApartmentItemReader(OpenApi openApi, RegionRepository regionRepository, ApartmentService apartmentService) {
        this.openApi = openApi;
        this.regionRepository = regionRepository;
        this.apartmentService = apartmentService;
    }

    //배치 시작할 지역id 1 ~ 250
    private Long currentRegionId = 1L;

    //배치 시작할 년, 월, 일
    private int currentYear = 2024;
    private int currentMonth = 3;
    private int currentDay = 1;
    private final int endYear = 2024;
    private final int endMonth = 3;

    @Override
    public Map<String, Apartment> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        //법정동코드 250개를 호출하고 Exception을 발생시켜 batch를 중단한다.
        Region region = regionRepository.findOne(currentRegionId);
        if (region == null) {
            throw new RegionExceedException("법정동 250개 호출 완료");
        }
        int lawDCode = region.getCode();

        int currentYM = currentYear * 100 + currentMonth;

        log.debug("법정동코드 = " + lawDCode + ",년월 = " + currentYM);

        JSONObject result = openApi.ApartmentSalesApi(lawDCode, currentYM);

        if(currentYear == endYear && currentMonth == endMonth){
            currentRegionId++;
            currentYear = 2024;
            currentMonth = 2;
        }
        else if(currentMonth == 12){
            currentYear++;
            currentMonth = 2;
        } else{
            currentMonth++;
        }


        return apartmentService.getApartmentList(result, currentDay);


    }
}
