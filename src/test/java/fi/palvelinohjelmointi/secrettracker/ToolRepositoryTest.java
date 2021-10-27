package fi.palvelinohjelmointi.secrettracker;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fi.palvelinohjelmointi.secrettracker.domain.Tool;
import fi.palvelinohjelmointi.secrettracker.domain.ToolRepository;
import fi.palvelinohjelmointi.secrettracker.domain.ToolTypeRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ToolRepositoryTest {
	@Autowired
	private ToolRepository toolRepository;
	
	@Autowired
	private ToolTypeRepository tooltypeRepository;
	
	@Test
	public void createTool() {
		Tool tool = new Tool("TestityökaluTesti", tooltypeRepository.findByToolType("Testityyppi1").get(0));
		toolRepository.save(tool);
		assertThat(tool.getToolId()).isNotNull();
	}
	
	@Test
	public void findByToolShouldReturnTool() {
		List<Tool> tools = toolRepository.findByTool("Testityökalu1");
		assertThat(tools).hasSizeGreaterThan(0);
		
		for(Tool tool: tools) {
			assertThat(tool.getTool()).isEqualTo("Testityökalu1");
		}
	}
	
	@Test
	public void findByToolShouldNotReturnToolsIfToolDoesNotExist() {
		List<Tool> tools = toolRepository.findByTool("Does Not Exist");
		assertThat(tools).hasSize(0);
	}
	
	@Test
	public void findByToolTypeIdShouldReturnTool() {
		List<Tool> tools = toolRepository.findByTooltypeId(tooltypeRepository.findByToolType("Testityyppi1").get(0));
		assertThat(tools).hasSize(2);
	}
	
	@Test
	public void findByToolTypeIdShouldNotReturnToolsIfTypeIsNotUsed() {
		List<Tool> tools = toolRepository.findByTooltypeId(tooltypeRepository.findByToolType("Testityyppi2").get(0));
		assertThat(tools).hasSize(0);
	}
}
