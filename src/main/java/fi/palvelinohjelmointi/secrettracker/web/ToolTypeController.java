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
import fi.palvelinohjelmointi.secrettracker.domain.Tool;
import fi.palvelinohjelmointi.secrettracker.domain.ToolRepository;
import fi.palvelinohjelmointi.secrettracker.domain.ToolType;
import fi.palvelinohjelmointi.secrettracker.domain.ToolTypeRepository;

@RestController
public class ToolTypeController {
	@Autowired
	private ToolTypeRepository toolTypeRepository;
	
	@Autowired
	private ToolRepository toolRepository;
	
	@Autowired
	private ResponseGenerator resGenerator; // imported from components-package
	
	// Get all tooltypes
	@GetMapping("/tooltypes")
	public @ResponseBody List<ToolType> toolTypes(){
		return (List<ToolType>) toolTypeRepository.findAll();
	}
	
	// Get a tooltype with specific id
	@GetMapping("/tooltypes/{id}")
	public @ResponseBody ResponseEntity<?> getToolTypeById(@PathVariable("id") Long toolTypeId){
		Optional<ToolType> toolType = toolTypeRepository.findById(toolTypeId);
		
		if(toolType.isEmpty()) {
			Map<String, String> response = resGenerator.createResponse("Tooltype with the given id not found", "404"); // A method from ResponseGenerator-component
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(toolType, HttpStatus.OK);
	}
	
	// Create a tooltype
	@PostMapping("/tooltypes")
	public @ResponseBody ResponseEntity<Map<String, String>> addToolType(@Valid @RequestBody ToolType toolType, BindingResult bindingResult){
		Map<String, String> response;
		
		if(bindingResult.hasErrors()) { // If validation notices errors in the request body
			response = resGenerator.createResponseFromBingindResult(bindingResult); // A method from ResponseGenerator-component
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		toolTypeRepository.save(toolType);
		
		response = resGenerator.createResponse("Toolype created succesfully", "201"); // A method from ResponseGenerator-component
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	// Modify a tooltype with a specific id
	@PutMapping("/tooltypes/{id}")
	public @ResponseBody ResponseEntity<Map<String, String>> editToolType(@Valid @RequestBody ToolType requestToolType, BindingResult bindingResult,
			@PathVariable("id") Long toolTypeId){
		Map<String, String> response;
		
		if(bindingResult.hasErrors()) { // If validation notices error in request body
			response = resGenerator.createResponseFromBingindResult(bindingResult); // A method from ResponseGenerator-component
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		Optional<ToolType> toolType = toolTypeRepository.findById(toolTypeId);
		
		if(toolType.isEmpty()) { // If tooltype with the given id was not found
			response = resGenerator.createResponse("Tooltype with the given id not found", "404");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		
		ToolType newToolType = toolType.get();
		newToolType.setToolType(requestToolType.getToolType());
		toolTypeRepository.save(newToolType);
		
		response = resGenerator.createResponse("Tooltype modified succesfully", "200");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	// Delete a tooltype with specific id
	@DeleteMapping("/tooltypes/{id}")
	public @ResponseBody ResponseEntity<Map<String, String>> deleteToolType(@PathVariable("id") Long toolTypeId){
		Map<String, String> response;
		
		Optional<ToolType> toolType = toolTypeRepository.findById(toolTypeId);
		
		if(toolType.isEmpty()) { // If tooltype with the given id was not found
			response = resGenerator.createResponse("Tooltype with the given id not found", "404");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		
		List<Tool> tools = toolRepository.findByTooltypeId(toolType.get());
		
		if(tools.size() > 0) { // If there are tools associated with the tooltype in question
			response = resGenerator.createResponse("Tooltype has been already linked with a tool", "400");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		toolTypeRepository.deleteById(toolTypeId);
		
		response = null;
		return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
	}
}
