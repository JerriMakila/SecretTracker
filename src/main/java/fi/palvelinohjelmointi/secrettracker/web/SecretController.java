package fi.palvelinohjelmointi.secrettracker.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fi.palvelinohjelmointi.secrettracker.components.ResponseGenerator;
import fi.palvelinohjelmointi.secrettracker.domain.Location;
import fi.palvelinohjelmointi.secrettracker.domain.LocationRepository;
import fi.palvelinohjelmointi.secrettracker.domain.Secret;
import fi.palvelinohjelmointi.secrettracker.domain.SecretRepository;
import fi.palvelinohjelmointi.secrettracker.domain.Tool;
import fi.palvelinohjelmointi.secrettracker.domain.ToolRepository;
import fi.palvelinohjelmointi.secrettracker.dto.SecretDto;

@RestController
public class SecretController {
	
	@Autowired
	private SecretRepository secretRepository;
	
	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private ToolRepository toolRepository;
	
	@Autowired
	private ResponseGenerator resGenerator; // imported from components-package
	
	// Get all secrets from the database;
	@GetMapping("/secrets")
	public @ResponseBody List<Secret> secrets(){
		return (List<Secret>) secretRepository.findAll();
	}
	
	// Get a secret with specific id
	@GetMapping("/secrets/{id}")
	public @ResponseBody ResponseEntity<?> getSecretById(@PathVariable("id") Long secretId){
		Optional<Secret> secret = secretRepository.findById(secretId);
		
		if(secret.isEmpty()) {
			Map<String, String> response = resGenerator.createResponse("Secret with the given id was not found", "404"); // A method from ResponseGenerator-component
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}else {
			return new ResponseEntity<>(secret, HttpStatus.OK);
		}
	}
	
	// Create a new secret
	@PostMapping("/secrets")
	public @ResponseBody ResponseEntity<Map<String, String>> addSecret(@Valid @RequestBody SecretDto secret, BindingResult bindingResult){
		Map<String, String> response;
		
		if(bindingResult.hasErrors()) {	// If validation notices errors in the data	
			response = resGenerator.createResponseFromBingindResult(bindingResult); // A method from ResponseGenerator-component
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		Optional<Location> location = locationRepository.findById(secret.getLocationId());
		Optional<Tool> tool = toolRepository.findById(secret.getToolId());
		
		if(location.isEmpty()) { // If location with the given id was not found in the database
			response = resGenerator.createResponse("Location not found with given id", "400");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		Location newLocation = location.get();
		Tool newTool = null;
		byte[] image = null;
		
		if(tool.isPresent()) {
			newTool = tool.get();
		}
		
		if(secret.getImage() != null) { // If Dto contains base64-encoded string containing the image
			try {
				image = Base64.getDecoder().decode(new String(secret.getImage()).getBytes("UTF-8")); // Converting the base64-string to byte[];
			}catch(UnsupportedEncodingException e) {
				secret.setImage(null);
			}
		}
		
		Secret newSecret = new Secret(
				secret.getSecret(),
				false,
				newLocation,
				newTool,
				image
		);
		
		secretRepository.save(newSecret);
		response = resGenerator.createResponse("Secret created succesfully", "201"); // A method from ResponseGenerator-component
		
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	// Modify a secret with a specific id
	@PutMapping("/secrets/{id}")
	public @ResponseBody ResponseEntity<Map<String, String>> modifySecret(@Valid @RequestBody SecretDto secretDto, BindingResult bindingResult, @PathVariable("id") Long secretId){
		Map<String, String> response;
		
		if(bindingResult.hasErrors()) { // If validation notices any errors in the data
			response = resGenerator.createResponseFromBingindResult(bindingResult); // A method from ResponseGenerator-component
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		Optional<Secret> secret = secretRepository.findById(secretId);
		Optional<Location> location = locationRepository.findById(secretDto.getLocationId());
		Optional<Tool> tool = toolRepository.findById(secretDto.getToolId());
		
		if(secret.isEmpty()) { // If secret with the given id was not found
			response = resGenerator.createResponse("Secret with the given id not found", "404"); // A method from ResponseGenerator-component
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		
		Secret newSecret = secret.get();
		Location newLocation = null;
		Tool newTool = null;
		byte[] image = null;
		
		if(location.isEmpty()) { // If location with the given id was not found in the database
			response = resGenerator.createResponse("Location with the given id not found", "400");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} else {
			newLocation = location.get();
		}
		
		if(tool.isPresent()) { // If requestbody contains an id of an existing tool
			newTool = tool.get();
		}
		
		if(secretDto.getImage() != null) { // If Dto contains base64-encoded string containing the image
			try {
				image = Base64.getDecoder().decode(new String(secretDto.getImage()).getBytes("UTF-8")); // Converting the base64-string to byte[]
			}catch(UnsupportedEncodingException e) {
				secretDto.setImage(null);
			}
		}else {
			image = newSecret.getImage(); // If the dto doesn't contain a new image, the existing image will be used
		}
		
		newSecret.setSecret(secretDto.getSecret());
		newSecret.setCleared(newSecret.getCleared());
		newSecret.setLocationId(newLocation);
		newSecret.setToolId(newTool);
		newSecret.setImage(image);
		
		secretRepository.save(newSecret);
		
		response = resGenerator.createResponse("Secret modified succesfully", "200");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	// Set 'cleared' value of a secret as true
	@PatchMapping("/secrets/{id}")
	public @ResponseBody ResponseEntity<Map<String, String>> markSecretAsCleared(@PathVariable("id") Long secretId){
		Map<String, String> response = new HashMap<>();
		String message;
		
		Optional<Secret> secret = secretRepository.findById(secretId);
		
		if(secret.isEmpty()) { // If secret with the given id was not found
			message = "Secret with the given id was not found";
			response.put("status", "404");
			response.put("message", message);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		
		Secret clearedSecret = secret.get();
		clearedSecret.setCleared(true);
		secretRepository.save(clearedSecret);
		
		message = "Secret modified succesfully";
		response.put("status", "200");
		response.put("message", message);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	// Deletes a secret with a specific id
	@DeleteMapping("/secrets/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody ResponseEntity<Optional<Secret>> deleteSecret(@PathVariable("id") Long secretId){
		Optional<Secret> secret = secretRepository.findById(secretId);
		
		if(secret.isEmpty()) { // If secret with the given id was not found
			return new ResponseEntity<>(secret, HttpStatus.NOT_FOUND);
		}
		
		secretRepository.delete(secret.get());
		return new ResponseEntity<>(secret, HttpStatus.NO_CONTENT);
	}
}
