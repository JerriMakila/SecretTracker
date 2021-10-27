package fi.palvelinohjelmointi.secrettracker;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fi.palvelinohjelmointi.secrettracker.domain.ToolType;
import fi.palvelinohjelmointi.secrettracker.domain.ToolTypeRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ToolTypeRepositoryTest {
	@Autowired
	private ToolTypeRepository tooltypeRepository;
	
	@Test
	public void createToolType() {
		ToolType toolType = new ToolType("TestiTyökalutyyppi");
		tooltypeRepository.save(toolType);
		assertThat(toolType.getToolTypeId()).isNotNull();
	}
	
	@Test
	public void findByToolTypeShouldReturnToolType() {
		List<ToolType> tooltypes = tooltypeRepository.findByToolType("Testityyppi1");
		
		assertThat(tooltypes).hasSizeGreaterThan(0);
		
		for(ToolType tooltype: tooltypes) {
			assertThat(tooltype.getToolType()).isEqualTo("Testityyppi1");
		}
	}
	
	@Test
	public void findByToolTypeShouldNotReturnToolTypeIfToolTypeDoesNotExist() {
		List<ToolType> tooltypes = tooltypeRepository.findByToolType("Does Not Exist");
		assertThat(tooltypes).hasSize(0);
	}
}
