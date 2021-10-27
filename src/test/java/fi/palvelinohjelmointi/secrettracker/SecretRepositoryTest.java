package fi.palvelinohjelmointi.secrettracker;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fi.palvelinohjelmointi.secrettracker.domain.LocationRepository;
import fi.palvelinohjelmointi.secrettracker.domain.Secret;
import fi.palvelinohjelmointi.secrettracker.domain.SecretRepository;
import fi.palvelinohjelmointi.secrettracker.domain.Tool;
import fi.palvelinohjelmointi.secrettracker.domain.ToolRepository;
import fi.palvelinohjelmointi.secrettracker.domain.ToolType;
import fi.palvelinohjelmointi.secrettracker.domain.ToolTypeRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SecretRepositoryTest {
	@Autowired
	private SecretRepository secretRepository;
	
	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private ToolRepository toolRepository;
	
	@Autowired
	private ToolTypeRepository toolTypeRepository;
	
	@Test
	public void createSecret() {
		Secret secret = new Secret("testiSalaisuus", false, locationRepository.findByLocation("Testilokaatio1").get(0), toolRepository.findByTool("Testityökalu1").get(0));
		secretRepository.save(secret);
		assertThat(secret.getSecretId()).isNotNull();
	}
	
	@Test
	public void findByToolIdShouldReturnListOfSecrets() {
		List<Secret> secrets = secretRepository.findByToolId(toolRepository.findByTool("Testityökalu1").get(0));
		
		assertThat(secrets).hasSizeGreaterThan(0);
		
		for(Secret secret: secrets) {
			assertThat(secret.getToolId().getTool()).isEqualTo("Testityökalu1");
		}
	}
	
	@Test
	public void findByToolIdShouldNotReturnSecretsIfToolIsNotUsed() {
		
		List<Secret> secrets = secretRepository.findByToolId(toolRepository.findByTool("Testityökalu2").get(0));
		assertThat(secrets).hasSize(0);
	}
}
