package com.smart.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;



@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	/*
	 * @RequestMapping("/test")
	 * 
	 * @ResponseBody public String test() { User user1 = new User();
	 * user1.setName("firstName"); user1.setEmail("vivekd288@gmail.com"); Contact c1
	 * = new Contact(); c1.setName("firstContact"); c1.setPhone("6969");
	 * List<Contact> contacts = new ArrayList<>(); contacts.add(c1);
	 * user1.setContacts(contacts); this.userRepository.save(user1); return "test";
	 * }
	 */
	
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	//Handler for registering new user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user ,BindingResult bindingResult,@RequestParam(value = "agreement",defaultValue = "false") boolean agreement, Model model) {
		try {
			if(!agreement) {
				System.out.println("User has not checked terms and conditions");
				throw new Exception("Please accept the terms and conditions to proceed further.");
			}
			if(bindingResult.hasErrors()) {
				System.out.println("ERROR " + bindingResult.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			user.setPassword(this.passwordEncoder.encode(user.getPassword()));
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("user_default.png");
			System.out.println(user.toString());
			User result = this.userRepository.save(user);
			model.addAttribute("title", "Register - Success");
			model.addAttribute("message", new Message("Successfully registered !! ","alert-success"));
			model.addAttribute("user", new User());
		}catch(Exception e) {
			model.addAttribute("user",user);
			model.addAttribute("message", new Message("Something went wrong !! "+e.getMessage(),"alert-danger"));
			return "signup";
		}
		return "signup";
	}
	
	@GetMapping("/sign-in")
	public String customLogin(Model model) {
		model.addAttribute("title", "Smart Contact Manager | Login");
		return "login";
		
	}
	
	@PostMapping("/authentication") 
	  public String authenticatePostLogin() {
		  System.out.println("processLogin method executed");
		  return "processLogin";
	  }
	
	@RequestMapping("/authentication") 
	  public String authenticateGetLogin() {
		  System.out.println("processLogin method executed : GET");
		  return "processLogin";
	  }
	
}
