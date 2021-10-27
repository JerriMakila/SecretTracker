package fi.palvelinohjelmointi.secrettracker;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fi.palvelinohjelmointi.secrettracker.domain.User;
import fi.palvelinohjelmointi.secrettracker.domain.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {
	@Autowired
	private UserRepository userRepository;
	
	@Test
	public void createUser() {
		User user = new User("Username", "password", "USER");
		userRepository.save(user);
		assertThat(user.getId()).isNotNull();
	}
	
	@Test
	public void findByUserNameShouldReturnUser() {
		User user = userRepository.findByUsername("userName");
		assertThat(user.getUsername()).isEqualTo("userName");
	}
}
