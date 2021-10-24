package fi.palvelinohjelmointi.secrettracker.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fi.palvelinohjelmointi.secrettracker.components.ResponseGenerator;
import fi.palvelinohjelmointi.secrettracker.domain.Secret;
import fi.palvelinohjelmointi.secrettracker.domain.SecretRepository;
import fi.palvelinohjelmointi.secrettracker.domain.Tool;
import fi.palvelinohjelmointi.secrettracker.domain.ToolRepository;
import fi.palvelinohjelmointi.secrettracker.domain.ToolType;
import fi.palvelinohjelmointi.secrettracker.domain.ToolTypeRepository;
import fi.palvelinohjelmointi.secrettracker.dto.ToolDto;

@RestController
public class ToolController {
	@Autowired
	private ToolRepository toolRepository;
	
	@Autowired
	private ToolTypeRepository toolTypeRepository;
	
	@Autowired
	private SecretRepository secretRepository;
	
	@Autowired
	private ResponseGenerator resGenerator; // imported from components-package
	
	// Get all tools from the database
	@GetMapping("/tools")
	public @ResponseBody List<Tool> tools(){
		return (List<Tool>) toolRepository.findAll();
	}
	
	// Get a tool with a specific id
	@GetMapping("tools/{id}")
	public @ResponseBody ResponseEntity<?> getToolById(@PathVariable("id") Long toolId){
		Optional<Tool> tool = toolRepository.findById(toolId);
		
		if(tool.isEmpty()) {
			Map<String, String> response = resGenerator.createResponse("Tool with the given id was not found", "404"); // A method from ResponseGenerator-component
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}else {
			return new ResponseEntity<>(tool, HttpStatus.OK);
		}
	}
	
	// Create a new tool
	@PostMapping("/tools")
	public @ResponseBody ResponseEntity<Map<String, String>> addTool(@Valid @RequestBody ToolDto toolDto, BindingResult bindingResult){
		Map<String, String> response;
		
		if(bindingResult.hasErrors()) { // If validation finds any errors in the data
			response = resGenerator.createResponseFromBingindResult(bindingResult); // A method from ResponseGenerator-component
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		Optional<ToolType> toolType = toolTypeRepository.findById(toolDto.getTooltypeId());
		
		if(toolType.isEmpty()) { // If tool with the given id was not found
			response = resGenerator.createResponse("ToolType with the given id not found", "400"); // A method from ResponseGenerator-component
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		Tool newTool = new Tool(toolDto.getTool(), toolType.get());
		toolRepository.save(newTool);
		
		response = resGenerator.createResponse("Tool created sccesfully", "201"); // A method from ResponseGenerator-component
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	// Modify a tool
	@PutMapping("/tools/{id}")
	public @ResponseBody ResponseEntity<Map<String, String>> modifyTool(@Valid @RequestBody ToolDto toolDto, BindingResult bindingResult, @PathVariable("id") Long toolId){
		Map<String, String> response;
		
		if(bindingResult.hasErrors()) { // If validation finds errors in the data
			response = resGenerator.createResponseFromBingindResult(bindingResult); // A method from ResponseGenerator-component
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		Optional<Tool> tool = toolRepository.findById(toolId);
		Optional<ToolType> toolType = toolTypeRepository.findById(toolDto.getTooltypeId());
		
		if(tool.isEmpty()) { // If tool with the given id was not found
			response = resGenerator.createResponse("Tool with the given id not found", "404"); // A method from ResponseGenerator-component
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		
		if(toolType.isEmpty()) { // If toolType with the given id was not found
			response = resGenerator.createResponse("ToolType with the given id not found", "400"); // A method from ResponseGenerator-component
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		Tool newTool = tool.get();
		newTool.setTool(toolDto.getTool());
		newTool.setTooltypeId(toolType.get());
		toolRepository.save(newTool);
		
		response = resGenerator.createResponse("Tool modified succesfully", "200"); // A method from ResponseGenerator-component
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	// Deletes a tool with specific id
	@DeleteMapping("/tools/{id}")
	public @ResponseBody ResponseEntity<Map<String, String>> deleteTool(@PathVariable("id") Long toolId){
		Map<String, String> response;
		
		Optional<Tool> tool = toolRepository.findById(toolId);
		
		if(tool.isEmpty()) { // If tool with the given id was not found
			response = resGenerator.createResponse("Tool with the given id not found", "404");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		
		List<Secret> secrets = secretRepository.findByToolId(tool.get());
		
		if(secrets.size() > 0) { // If tool has any secrets associated with it -> cannot be deleted
			response = resGenerator.createResponse("Tool has already been linked with a secret", "400"); // A method from ResponseGenerator-component
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		toolRepository.deleteById(toolId);
		
		response = null;
		
		return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
	}
}
