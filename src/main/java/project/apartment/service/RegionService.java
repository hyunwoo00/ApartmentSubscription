package project.apartment.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.apartment.domain.Region;
import project.apartment.repository.RegionRepository;

import java.io.FileInputStream;
import java.util.Iterator;

import static project.apartment.domain.Region.createRegion;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    @Transactional
    public Long saveRegion(Region region) {

        regionRepository.save(region);
        return region.getId();
    }

    /**
     * 엑셀 파일 parsing을 통해 RegionRepository에 데이터 저장.
     */
    @Transactional
    public String storeRegions() throws Exception {
        FileInputStream file = new FileInputStream("C:\\Users\\ke269\\OneDrive\\바탕 화면\\Spring Study\\apartment\\src\\main\\resources\\법정동 기준 시군구 단위.xlsx");

        //.xlsx 파일을 읽을 수 있게 해줌.
        XSSFWorkbook workbook = new XSSFWorkbook(file);

        //시트 번호를 통해 시트를 가져옴.
        XSSFSheet sheet = workbook.getSheetAt(2);

        //Iterate through each rows one by one
        Iterator<Row> rowIterator = sheet.iterator();

        String sido = "";
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            //For each row, iterate through all the columns
            Iterator<Cell> cellIterator = row.cellIterator();

            /**
             * 엑셀에서 시/도와 시/군/구를 구별하기 위함.
             * 시/도의 경우 시/도 다음 셀에 문자가 존재하고
             * 시/군/구의 경우 시/군/구 다음 셀에 법정동 코드(숫자)가 존재한다.
             */
            int cnt = 0;
            String name = "";
            String sgg = "";
            int lawDCode = 0;

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                //Check the cell type and format accordingly

                switch (cell.getCellType()) {
                    case NUMERIC:
                        lawDCode = (int)cell.getNumericCellValue();
                        break;
                    case STRING:
                        cnt++;
                        if(cnt == 2) {
                            break;
                        }
                        /**
                         * name에는 시/도의 경우 시/도가 저장되고 시/군/구의 경우 시/군/구가 저장됨.
                         */
                        name = cell.getStringCellValue();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + cell.getCellType());
                }
            }
            if(cnt == 2){
                sido = name;
            }
            else{
                sgg = name;
                Region region = createRegion(sido, sgg, lawDCode);
                System.out.println("sido = " + sido + " sgg = " + sgg + " lawdcode = " + lawDCode);
                regionRepository.save(region);

            }
        }
        file.close();
        return "법정동 코드, 지역정보 저장 성공";
    }

}
