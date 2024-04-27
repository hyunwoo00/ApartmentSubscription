package project.apartment.service;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

@Component
public class OpenApi {

    /**
     * @param lawDCode: 법정동 코드
     * @param ym: 4자리 year + 2자리 month
     * @return xml 형식의 데이터를 JSONObject로 반환
     */
    public JSONObject ApartmentSalesApi(int lawDCode, int ym) throws Exception{

        StringBuilder urlBuilder = new StringBuilder("http://openapi.molit.go.kr:8081/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/getRTMSDataSvcAptTrade"); /*URL*/
        //encoding 서비스 키
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=%2FAazoQhWfUOgQDnxn8LXaxgBAJmiZLkWPqghF5p29pDJWw4e%2FMOO9Rf5EuN0g2ktxKIXHsUsqwMDWlz1NdS82g%3D%3D"); /*Service Key*/
        //법정동 코드
        urlBuilder.append("&" + URLEncoder.encode("LAWD_CD","UTF-8") + "=" + URLEncoder.encode(Integer.toString(lawDCode), "UTF-8")); /*각 지역별 코드*/
        //날짜
        urlBuilder.append("&" + URLEncoder.encode("DEAL_YMD","UTF-8") + "=" + URLEncoder.encode(Integer.toString(ym), "UTF-8")); /*월 단위 신고자료*/

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        org.json.JSONObject xmlJSONObj = org.json.XML.toJSONObject(sb.toString());


        return xmlJSONObj;

    }


}
