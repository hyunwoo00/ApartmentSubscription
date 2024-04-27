package project.apartment.batch;

import org.springframework.batch.item.ItemProcessor;
import project.apartment.domain.Apartment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomApartmentProcessor implements ItemProcessor<Map<String, Apartment>, List<Apartment>> {
    @Override
    public List<Apartment> process(Map<String, Apartment> item) throws Exception {
        List<Apartment> list = new ArrayList<>();
        for (String key : item.keySet()) {
            list.add(item.get(key));
        }
        return list;
    }
}
