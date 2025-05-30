package salimlgh.culturedigitalplatform;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.config.ContextFunctionCatalogAutoConfiguration;
import org.springframework.context.annotation.Bean;

import salimlgh.culturedigitalplatform.repository.CourseRepository;
import salimlgh.culturedigitalplatform.repository.FormationRepository;

@SpringBootApplication(exclude = {
    ContextFunctionCatalogAutoConfiguration.class
})
public class CultureDigitalPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(CultureDigitalPlatformApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(
            FormationRepository formationRepository,
            CourseRepository courseRepository) {
        return args -> {



            System.out.println("Base de données initialisée avec des données d'exemple");
        };
    }
}
