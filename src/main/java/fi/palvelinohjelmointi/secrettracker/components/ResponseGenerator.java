package fi.palvelinohjelmointi.secrettracker.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@Component
public class ResponseGenerator {
	public Map<String, String> createResponseFromBingindResult(BindingResult bindingResult){
		List<ObjectError> errors = bindingResult.getAllErrors();
		StringBuilder errorMessage = new StringBuilder(errors.size());
		
		for(ObjectError error: errors) {
			errorMessage.append(error.getDefaultMessage() + ". ");
		}
		
		String message = errorMessage.toString().trim();
		Map<String, String> response = this.createResponse(message, "404");
		
		return response;
	}
	
	public Map<String, String> createResponse(String message, String status){
		Map<String, String> response = new HashMap<>();
		response.put("message", message);
		response.put("status", status);
		
		return response;
	}
}
