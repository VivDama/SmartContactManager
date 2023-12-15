package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.services.ControllerServices;
import com.smart.services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ControllerServices controllerService;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	EmailService emailService;
	Random random = new Random(System.currentTimeMillis());
	
	@GetMapping("/forgot_password")
	public String openEmailForm() {
		return "forgot_password_form";
	}
	
	@PostMapping("/send_otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {
		System.out.println(email);
		if(this.controllerService.checkUserEmailExists(email) == false) {
			session.setAttribute("message", new Message("No user exists with the given Email", "danger"));
			return "forgot_password_form";
		}
		
		// Generate otp of 4 digit
		int otp;// = random.nextInt(999999);
		otp =((1 + random.nextInt(2)) * 100000 + random.nextInt(100000));
		session.setAttribute("generated_otp", otp);
		session.setAttribute("email", email);
		System.out.println("OTP: " + otp);
		String subject = "Reset Password | Smart Contact Manager";
		String email_message  =""
				+"<div style='border:1px solid #e2e2e2; padding:20px'>"
				+"<h1>"
				+"Your One Time Password to reset your account password is "
				+"<b>" + otp
				+"</n>"
				+"</h1>"
				+"</div>";
		String to = email;
		
		boolean flag = this.emailService.sendEmail(subject, email_message, to);
		if(flag) {
			session.setAttribute("message", new Message("OTP has been sent to your registered email", "success"));
			return "submit_otp";
		}
		else {
			System.out.println("Failed to send email");
			session.setAttribute("message", new Message("Error Sending OTP! Please try again later.", "danger"));
			return "forgot_password_form";
		}
		
	}

	@PostMapping("/verify_otp")
	public String verifyOTP(@RequestParam("otp") String user_otp, HttpSession session) {
		System.out.println("Generated otp: " + session.getAttribute("generated_otp"));
		System.out.println("User Entered OTP: " + user_otp);
		if(user_otp.trim().equals(session.getAttribute("generated_otp").toString().trim())) {
			System.out.println("OTP validation successful");
			session.removeAttribute("generated_otp");
			return "new_password_form";
		}
		else {
			System.out.println("OTP verification failed");
			session.setAttribute("message", new Message("Incorrect OTP, Check your otp and submit again", "danger"));
			return"submit_otp";
		}
	}
	
	@PostMapping("/submit_new_password")
	public String submitNewPassword(@RequestParam("newPassword") String newPassword ,HttpSession session) {
		User user = this.userRepository.getUserByUserName(session.getAttribute("email").toString());
		user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		this.userRepository.save(user);
		return "password_update_success";
	}
}
