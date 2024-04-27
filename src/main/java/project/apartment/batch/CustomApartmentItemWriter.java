package project.apartment.batch;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.transaction.annotation.Transactional;
import project.apartment.domain.Apartment;
import project.apartment.repository.ApartmentRepository;
import project.apartment.service.ApartmentService;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class CustomApartmentItemWriter implements ItemWriter<List<Apartment>> {

    private final ApartmentRepository apartmentRepository;
    @Override
    public void write(Chunk<? extends List<Apartment>> chunk) throws Exception {
        List<Apartment> list = chunk.getItems().getFirst();
        for (Apartment apartment : list) {
            apartmentRepository.save(apartment);
        }

    }
}
