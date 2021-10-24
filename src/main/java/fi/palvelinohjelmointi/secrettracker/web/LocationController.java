package fi.palvelinohjelmointi.secrettracker.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import fi.palvelinohjelmointi.secrettracker.domain.Location;
import fi.palvelinohjelmointi.secrettracker.domain.LocationRepository;
import fi.palvelinohjelmointi.secrettracker.domain.Secret;

@RestController
public class LocationController {
	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private ResponseGenerator resGenerator; //imported from components-package
	
	// Get all locations in the database
	@GetMapping("/locations")
	public @ResponseBody List<Location> locations(){
		return (List<Location>) locationRepository.findAll();
	}
	
	//Get location with a specific id
	@GetMapping("/locations/{id}")
	public @ResponseBody ResponseEntity<?> getLocationById(@PathVariable("id") Long locationId){
		Optional<Location> location = locationRepository.findById(locationId);
		
		if(location.isEmpty()) {
			Map<String, String> response = resGenerator.createResponse("Location with the given id was not found", "404");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}else {
			return new ResponseEntity<>(location.get(), HttpStatus.OK);
		}
	}
	
	//Get all secrets associated with a specific location
	@GetMapping("/locations/{id}/secrets")
	public @ResponseBody ResponseEntity<List<Secret>> getSecretsByLocation(@PathVariable("id") Long locationId){
		Optional<Location> location = locationRepository.findById(locationId);
		
		if(location.isEmpty()) {
			return new ResponseEntity<>(new ArrayList<Secret>(), HttpStatus.NOT_FOUND);
		}
		
		List<Secret> secrets = location.get().getSecrets();
		
		return new ResponseEntity<>(secrets, HttpStatus.OK);
	}
	
	//Add a new location to the database
	@PostMapping("/locations")
	public @ResponseBody ResponseEntity<Map<String, String>> createLocation(@Valid @RequestBody Location location, BindingResult bindingResult){
		Map<String, String> response;
		
		if(bindingResult.hasErrors()) { //If validation notices errors in the data
			response = resGenerator.createResponseFromBingindResult(bindingResult); // A method from ResponseGenerator-component
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		response = resGenerator.createResponse("Location created succesfully", "201"); // A method from ResponseGenerator-component
		locationRepository.save(location);
		
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	// Modify a specific entry in the location-table
	@PutMapping("/locations/{id}")
	public @ResponseBody ResponseEntity<Map<String, String>> modifyLocation(@Valid @RequestBody Location requestLocation, BindingResult bindingResult, @PathVariable("id") Long locationId){
		Map<String, String> response;
		
		if(bindingResult.hasErrors()) {//If validation notices errors in the data
			response = resGenerator.createResponseFromBingindResult(bindingResult); // A method from ResponseGenerator-component
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		Optional<Location> location = locationRepository.findById(locationId); // Requesting the specific location from the database
		
		if(location.isEmpty()) { // If a location with the given id was not found in the database
			response = resGenerator.createResponse("location with the given id does not exist", "404");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		
		Location newLocation = location.get();
		newLocation.setLocation(requestLocation.getLocation());
		locationRepository.save(newLocation);
		
		response = resGenerator.createResponse("Location modified succesfully", "200");
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	// Deletes a specific location from the location-table
	@DeleteMapping("/locations/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody ResponseEntity<Map<String, String>> deleteLocation(@PathVariable("id") Long locationId){
		Map<String, String> response;
		Optional<Location> location = locationRepository.findById(locationId); // Getting a specific location from the database
		
		if(location.isEmpty()) { // If a location with the given id was not found in the database
			response = resGenerator.createResponse("Location with the given id does not exist", "404");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		
		Location foundLocation = location.get();
		
		if(foundLocation.getSecrets().size() > 0) { // Checking if the location has any secrets associated with it -> cannot be deleted from the database
			response = resGenerator.createResponse("Cannot delete locations that have secrets associated with them", "400");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		locationRepository.delete(foundLocation);
		response = null;
		
		return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
	}
}
