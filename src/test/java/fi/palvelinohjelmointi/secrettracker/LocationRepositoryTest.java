package fi.palvelinohjelmointi.secrettracker;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fi.palvelinohjelmointi.secrettracker.domain.Location;
import fi.palvelinohjelmointi.secrettracker.domain.LocationRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LocationRepositoryTest {
	@Autowired
	private LocationRepository locationRepository;
	
	@Test
	public void createLocation() {
		Location location = new Location("testLocation");
		locationRepository.save(location);
		assertThat(location.getLocationId()).isNotNull();
	}
	
	@Test
	public void findByLocationShouldReturnLocation(){
		List<Location> locations = locationRepository.findByLocation("Testilokaatio1");
		
		assertThat(locations).hasSizeGreaterThan(0);
		
		for(Location location: locations) {
			assertThat(location.getLocation()).isEqualTo("Testilokaatio1");
		}
	}
	
	@Test
	public void findLocationShouldNotFindLocationsThatDoNotExist() {
		List<Location> locations = locationRepository.findByLocation("Does not exist");
		
		assertThat(locations).hasSize(0);
	}
}
