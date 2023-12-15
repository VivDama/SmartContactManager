package com.smart.helper;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class Message {
	private String content;
	BindingResult bindingResult;
	List<ObjectError> allErrors;
	private String type;
	
	public Message(String content, String type) {
		super();
		this.content = content;
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public BindingResult getBindingResult() {
		return bindingResult;
	}

	public void setBindingResult(BindingResult bindingResult) {
		this.bindingResult = bindingResult;
	}

	public List<ObjectError> getAllErrors() {
		return allErrors;
	}

	public void setAllErrors(List<ObjectError> allErrors) {
		this.allErrors = allErrors;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
