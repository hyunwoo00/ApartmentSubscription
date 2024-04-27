package project.apartment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.apartment.domain.Address;
import project.apartment.domain.Apartment;
import project.apartment.domain.ApartmentSale;
import project.apartment.domain.Region;
import project.apartment.domain.enums.ApiFactor;
import project.apartment.exception.apartment.RegisteredAPTException;
import project.apartment.exception.apartment.RequestExceedException;
import project.apartment.repository.ApartmentRepository;
import project.apartment.repository.RegionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static project.apartment.domain.Apartment.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final RegionRepository regionRepository;

    /**
     * 아파트 매매정보 저장
     * 아파트의 등록여부에 따라 데이터베이스에서 가져오거나 아파트 Entity를 생성하고 매매정보를 저장.
     * 아파트의 Id를 넘기지 않고 아파트의 정보들을 넘긴 것은 공공데이터 API를 통해 아파트 매매 정보를 저장할 때
     * 아파트 객체를 만들어서 파라미터로 넘겨줘야하기 때문이다.
     * @param address 아파트 주소
     * @param name 아파트 이름
     * @param apartmentSale 아파트 매매 정보
     * return 아파트 등록시 null, 미등록시 새로운 아파트 엔티티
     */

    @Transactional
    public Apartment storeApartmentSale(Address address, String name, ApartmentSale apartmentSale) {
        //엔티티 조회
        Apartment apartment;
        if(checkingApartmentRegistration(address, name)){
            apartment = apartmentRepository.findByNameAndDong(name, address.getEmd());
            //매매 저장
            apartment.addApartmentSale(apartmentSale);
            return null;
        }
        else{//등록된 아파트가 없으면 새로 생성.
            apartment = createApartment(name, address);
            //매매 저장
            apartment.addApartmentSale(apartmentSale);
            return apartment;
        }

    }

    /**
     * 등록된 아파트인지 확인.
     * @param address 아파트 주소
     * @param name 아파트 이름
     * {name, address}는 unique하기 때문에 2개 이상의 값이 나오지 않는다.
     * @return
     */
    public boolean checkingApartmentRegistration(Address address, String name) {

        try {
            apartmentRepository.findByNameAndDong(name, address.getEmd());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    @Transactional
    public Long registerApartment(Apartment apartment) {
        Address address = apartment.getAddress();
        String name = apartment.getName();

        if (checkingApartmentRegistration(address, name)) {
            throw new RegisteredAPTException("이미 등록된 아파트입니다.");
        }

        apartmentRepository.save(apartment);
        return apartment.getId();
    }

    public List<Apartment> getApartmentListBySggMonth(Address address, LocalDateTime localDateTime) {
        List<Apartment> regionApartmentList = apartmentRepository.findBySiDoSgg(address.getSiDo(), address.getSgg());

        List<Apartment> result = new ArrayList<>();
        for (int i = 0; i < regionApartmentList.size(); i++) {
            Apartment apartment = regionApartmentList.get(i);
            List<ApartmentSale> apartmentSaleList = apartment.getApartmentSaleList();
            apartmentSaleList.stream()
                    .filter(a ->
                            a.getDate().getYear() == localDateTime.getYear() &&
                                    a.getDate().getMonth() == localDateTime.getMonth()
                    )
                    .findAny()
                    .ifPresent(a -> result.add(apartment));
        }

        return result;
    }
    @Transactional
    public Map<String, Apartment> getApartmentList(JSONObject xmlJSONObj, int day) throws JsonProcessingException {


        String xmlJSONObjString = xmlJSONObj.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(xmlJSONObjString, new TypeReference<Map<String,Object>>(){});
        Map<String, Object> dataResponse = (Map<String, Object>) map.get("response");
        Map<String, Object> header = (Map<String, Object>) dataResponse.get("header");
        Map<String, Object> body = (Map<String, Object>) dataResponse.get("body");


        //결과를 저장할 리스트
        Map<String, Apartment> apartmentMap = new HashMap<>();
        List<Apartment> apartmentList = new ArrayList<>();

        int resultCode = Integer.parseInt(header.get("resultCode").toString());

        //api 호출 수치를 넘었을 경우 = 99, 정상 호출 = 00
        if(resultCode == 99){
            throw new RequestExceedException("API 호출 수치가 한계를 넘었습니다.");
        }

        //body가 존재하지 않거나 아파트 거래내역이 존재하지 않다면 빈 Map을 return
        if (body == null || body.get("totalCount").toString().equals("0")) {
            return apartmentMap;
        }
        LinkedHashMap<String, Object> items = (LinkedHashMap<String, Object>)body.get("items");




        int size = Integer.parseInt(body.get("totalCount").toString());

        //totalCount가 1인 경우 item은 LinkedHashMap, 그 이상은 List
        List<Object> apartmentSales = null;
        if(size > 1) {
            apartmentSales = (ArrayList<Object>)items.get("item");
        }
        else{
            apartmentSales = new ArrayList<Object>();
            apartmentSales.add((LinkedHashMap<String, Object>)items.get("item"));
        }

        for (int i = 0; i < size; i++) {
            LinkedHashMap<String, Object> apartmentSale = (LinkedHashMap<String, Object>)apartmentSales.get(i);

            ApartmentSale createdApartmentSale = createApartmentSaleByMap(apartmentSale);

            if(createdApartmentSale.getDate().getDayOfMonth() < day) continue;

            int code = Integer.parseInt(getXMLValue(apartmentSale, ApiFactor.CODE.getName()));

            Region region = regionRepository.findByCode(code);
            String siDo = region.getSido();
            String sgg = region.getSgg();
            String emd = getXMLValue(apartmentSale, ApiFactor.EMD.getName());

            Address address = Address.createAddress(siDo, sgg, emd);

            String name = getXMLValue(apartmentSale, ApiFactor.NAME.getName());


            //DB에 존재할 경우
            if (checkingApartmentRegistration(address, name)) {
                Apartment apt = apartmentRepository.findByNameAndDong(name, address.getEmd());
                apt.addApartmentSale(createdApartmentSale);
            }
            //DB에 존재하지 않고 Map에 존재하는 경우
            else if (apartmentMap.containsKey(name)) {
                apartmentMap.get(name).addApartmentSale(createdApartmentSale);
            }
            //DB, Map에 존재하지 않는 경우
            else{
                apartmentMap.put(name, createApartment(name, address));
            }

        }

        return apartmentMap;

    }

    @Transactional
    public List<ApartmentSale> getApartmentSales(JSONObject xmlJSONObj) throws JsonProcessingException {


        String xmlJSONObjString = xmlJSONObj.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map;
        map = objectMapper.readValue(xmlJSONObjString, new TypeReference<Map<String,Object>>(){});
        Map<String, Object> dataResponse = (Map<String, Object>) map.get("response");
        Map<String, Object> body = (Map<String, Object>) dataResponse.get("body");

        //결과를 저장할 리스트
        List<ApartmentSale> apartmentSaleList = new ArrayList<>();

        LinkedHashMap<String, Object> items = (LinkedHashMap<String, Object>)body.get("items");
        List<Object> apartmentSales = (ArrayList<Object>)items.get("item");

        int size = Integer.parseInt(body.get("totalCount").toString());

        for (int i = 0; i < size; i++) {
            LinkedHashMap<String, Object> apartmentSale = (LinkedHashMap<String, Object>)apartmentSales.get(i);

            ApartmentSale createdApartmentSale = createApartmentSaleByMap(apartmentSale);

            int code = Integer.parseInt(getXMLValue(apartmentSale, ApiFactor.CODE.getName()));

            Region region = regionRepository.findByCode(code);
            String siDo = region.getSido();
            String sgg = region.getSgg();
            String emd = getXMLValue(apartmentSale, ApiFactor.EMD.getName());

            Address address = Address.createAddress(siDo, sgg, emd);

            String name = getXMLValue(apartmentSale, ApiFactor.NAME.getName());

            storeApartmentSale(address, name, createdApartmentSale);


            apartmentSaleList.add(createdApartmentSale);

        }

        return apartmentSaleList;

    }

    public ApartmentSale createApartmentSaleByMap(LinkedHashMap<String, Object> apartmentSale) {

        Long price = Long.parseLong(getXMLValue(apartmentSale, ApiFactor.PRICE.getName()).replaceAll(",", "")) * 10000;
        int year = Integer.parseInt(getXMLValue(apartmentSale, ApiFactor.YEAR.getName()));
        int month = Integer.parseInt(getXMLValue(apartmentSale, ApiFactor.MONTH.getName()));
        int day = Integer.parseInt(getXMLValue(apartmentSale, ApiFactor.DAY.getName()));
        LocalDate date = LocalDate.of(year, month, day);
        Double area = Double.parseDouble(getXMLValue(apartmentSale, ApiFactor.AREA.getName()));
        int floor = Integer.parseInt(getXMLValue(apartmentSale, ApiFactor.FLOOR.getName()));



        return ApartmentSale.createApartmentSale(price, date, area, floor);

    }

    public String getXMLValue(LinkedHashMap<String, Object> item, String key) {
        return item.get(key).toString();
    }


}
