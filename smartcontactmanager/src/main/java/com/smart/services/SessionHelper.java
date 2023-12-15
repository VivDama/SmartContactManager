package com.smart.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper {

	public void removeMessageFromSession() {
		try {
			HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();			
			session.removeAttribute("message");
			System.out.println("Message attribute removed succesfully");
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	public List<ObjectError> getAllErrors(Message message){
		
		for(ObjectError error : message.getAllErrors()) {
			System.out.println(error.getDefaultMessage());
		}
		return message.getAllErrors()!=null ? message.getAllErrors() : new ArrayList<ObjectError>();
	}
	
	public String getDefaultError(ObjectError error) {
		return error.getDefaultMessage();
	}
	
	public List<FieldError> getFieldErrors(String field, Message message){
		if(message.getBindingResult() == null)
			return null;
		List<FieldError> fieldErrors = message.getBindingResult().getFieldErrors(field);
		if(fieldErrors.size()<=0)
				return null;
		return fieldErrors;
		//return fieldErrors != null ? fieldErrors :  new ArrayList<FieldError>();
				
	}
	
	public int getMin(int n1, int n2) {
		return Math.min(n1, n2);
	}
	
	public int getMax(int n1, int n2) {
		return Math.max(n1, n2);
	}
}
