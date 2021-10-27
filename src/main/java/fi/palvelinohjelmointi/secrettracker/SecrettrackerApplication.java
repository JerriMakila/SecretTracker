package fi.palvelinohjelmointi.secrettracker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import fi.palvelinohjelmointi.secrettracker.domain.Location;
import fi.palvelinohjelmointi.secrettracker.domain.LocationRepository;
import fi.palvelinohjelmointi.secrettracker.domain.Secret;
import fi.palvelinohjelmointi.secrettracker.domain.SecretRepository;
import fi.palvelinohjelmointi.secrettracker.domain.Tool;
import fi.palvelinohjelmointi.secrettracker.domain.ToolRepository;
import fi.palvelinohjelmointi.secrettracker.domain.ToolType;
import fi.palvelinohjelmointi.secrettracker.domain.ToolTypeRepository;
import fi.palvelinohjelmointi.secrettracker.domain.User;
import fi.palvelinohjelmointi.secrettracker.domain.UserRepository;

@SpringBootApplication
public class SecrettrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecrettrackerApplication.class, args);
	}
	
	// Adding some information to the database. This will be of no use when PostgreSQL is being used
	@Bean
	public CommandLineRunner secretDemo(ToolTypeRepository tpRepo, LocationRepository lRepo, ToolRepository tRepo, SecretRepository sRepo, UserRepository uRepo) {
		return(args) ->{
			tpRepo.save(new ToolType("Testityyppi1"));
			tpRepo.save(new ToolType("Testityyppi2"));
			lRepo.save(new Location("Testilokaatio1"));
			lRepo.save(new Location("Testilokaatio2"));
			tRepo.save(new Tool("Testityökalu1", tpRepo.findByToolType("Testityyppi1").get(0)));
			tRepo.save(new Tool("Testityökalu2", tpRepo.findByToolType("Testityyppi1").get(0)));
			sRepo.save(new Secret("Testisalaisuus1", false, lRepo.findByLocation("Testilokaatio1").get(0), tRepo.findByTool("Testityökalu1").get(0)));
			sRepo.save(new Secret("Testisalaisuus2", false, lRepo.findByLocation("Testilokaatio1").get(0), tRepo.findByTool("Testityökalu1").get(0)));
			uRepo.save(new User("userName", "password", "USER"));
		};
	}
}
