package project.apartment.batch;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import project.apartment.domain.Apartment;
import project.apartment.repository.ApartmentRepository;
import project.apartment.repository.RegionRepository;
import project.apartment.service.ApartmentService;
import project.apartment.service.OpenApi;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfiguration{

    private final OpenApi openApi;
    private final RegionRepository regionRepository;
    private final ApartmentRepository apartmentRepository;
    private final ApartmentService apartmentService;
    private final EntityManagerFactory emf;
    private final DataSource dataSource;

    @Bean
    public Job ApartmentJob(JobRepository jobRepository, Step step) {

        return new JobBuilder("apt", jobRepository)
                //run.id를 증가시켜 동일한 job을 재실행하게 해준다.
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager jpaTransactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(emf);
        jpaTransactionManager.setDataSource(dataSource);
        return jpaTransactionManager;
    }

    @Bean
    public Step step1(JobRepository jobRepository) {
        return new StepBuilder("step1", jobRepository)
                .<Map<String, Apartment>, List<Apartment>>chunk(1, jpaTransactionManager())
                .reader(apartmentItemReader())
                .processor(apartmentProcessor())
                .writer(apartmentItemWriter())
                .build();

    }

    @Bean
    public ItemReader<Map<String, Apartment>> apartmentItemReader() {
        return new CustomApartmentItemReader(openApi, regionRepository, apartmentService);
    }

    @Bean
    public ItemProcessor<Map<String, Apartment>, List<Apartment>> apartmentProcessor() {
        return new CustomApartmentProcessor();
    }

    @Bean
    public ItemWriter<List<Apartment>> apartmentItemWriter() {
        return new CustomApartmentItemWriter(apartmentRepository);
    }





}
