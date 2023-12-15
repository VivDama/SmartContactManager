package com.smart.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

@Service
public class ControllerServices {
	
	@Autowired
	UserRepository userRepository;
	
	public boolean checkUserEmailExists(String userEmail) {
		User user = this.userRepository.getUserByUserName(userEmail);
		if(user == null)
			return false;
		return true;
	}

}
