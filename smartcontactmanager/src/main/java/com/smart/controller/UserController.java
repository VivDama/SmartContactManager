package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	//method to add common data 
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		System.out.println("Username " + principal.getName());
		User user = this.userRepository.getUserByUserName(principal.getName());
		 System.out.println(user);	
		 model.addAttribute("user", user);
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title","Smart Contact Dashboard | Dashboard");
		User user = this.userRepository.getUserByUserName(principal.getName());
		Long contactsCountByUser = this.contactRepository.getContactsCountByUser(user.getId());
		model.addAttribute("contact_count",contactsCountByUser );
		return "normal/user_dashboard";
	}

	@GetMapping("/add_contact")
	 public String openAddContactForm(Model model) {
		model.addAttribute("title","Smart Contact Dashboard | Add New Contact");
		model.addAttribute("contact", new Contact());
		 return "normal/add_contact_form";
	 }
	
	//processing add contact form
	@PostMapping("/process_contact")
	public String processContact(@Valid @ModelAttribute Contact contact , BindingResult result,
			@RequestParam("profileImage") MultipartFile file ,Model model, Principal principal,
			HttpSession session) {
		if(contact == null)
			return "normal/add_contact_form";
		try {
			if(result.hasErrors()) {
				//List<ObjectError> allErrors = result.getAllErrors();
				throw new Exception("Binding errors");
			}
				
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			
			//processing and uploading file
			if(!file.isEmpty()) {
				contact.setImageUrl(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()
						+ File.separator  + contact.getImageUrl()/* + file.getOriginalFilename() */);
				Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
				//contact.setImageUrl(path.toString());
			}
			else {
				System.out.println("No image uploaded,hence profile image is empty");
				contact.setImageUrl("default_contact.png");
			}
			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);
			System.out.println("Data has been updated succesfully");
			System.out.println("Contact form data: " + contact);
			session.setAttribute("message", new Message("Contact has been successfully added.", "success"));
		}catch(Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
			Message message = new Message("Something went wrong!! Please check the below entered details and submit again.", "danger");
			message.setAllErrors(result.getAllErrors());
			message.setBindingResult(result);
			System.out.println("Controller output: ");			
			session.setAttribute("message", message);
			return "normal/add_contact_form";
		}
		return "normal/add_contact_success";		
	}
	
	//Show contacts handler
	@GetMapping("/show_contacts/{page}")
	public String showContacts(@PathVariable("page") int page ,Model model, Principal principal) {
		model.addAttribute("title", "Smart Contact Manager | View Contacts");
		User user = this.userRepository.getUserByUserName(principal.getName());
		
		//PageRequest return object of PageRequest which is child class of Pageable. Hence we are storing it in variable of Pageable class
		Pageable pageable = PageRequest.of(page, 12);
		
		
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_contacts";
	}
	
	@GetMapping("/view_contact/{currentPage}/{contactId}")
	public String showContactDetail(@PathVariable("contactId") int cId, @PathVariable("currentPage") Integer currentPage ,Model model, Principal principal) {

		try {
			model.addAttribute("currentPageIndex", currentPage);
			Contact contact = this.contactRepository.getReferenceById(cId);
			User user = this.userRepository.getUserByUserName(principal.getName());
			if(user.getId() == contact.getUser().getId()) {
				model.addAttribute("contact", contact);				
			}
		}catch(EntityNotFoundException e) {
			System.out.println("Invalid contact id has been tried to access from the URL");
			model.addAttribute("exceptionMessage", "The requested resource doesnot exist or you donot have the necessary permissions to view this page ");
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			model.addAttribute("exceptionMessage", "Ahh Snap, looks like there is some issue in the backend. Do some Yoga while our Engineers fix this for you. ");
		}finally {
			return "normal/contact_detail";
		}
		
	}
	
	@GetMapping("/delete/{cId}/{currentPage}")
	public String deleteContact(@PathVariable("cId") int cId,@PathVariable("currentPage") int currentPage, Principal principal, HttpSession session) {
		Contact contact = this.contactRepository.getReferenceById(cId);
		User user = contact.getUser();
		if(user == this.userRepository.getUserByUserName(principal.getName())) {
			this.contactRepository.deleteByContactId(cId);
			session.setAttribute("message", new Message("Contact Successfully deleted!", "success"));
		}
			
		return "redirect:/user/show_contacts/"+currentPage;
	}
	
	
	//handler for showing form for updating contact
	@PostMapping("/update_contact_form/{cId}")
	public String updateForm(@PathVariable("cId") int cId ,Model model) {
		model.addAttribute("title", "Smart Contact Manager | Update Contact");
		Contact contact = this.contactRepository.getReferenceById(cId);
		model.addAttribute("contact", contact);
		return "normal/update_form";
	}

	//Handler for processing contact update
	
	@PostMapping("/contact_update")
	public String updateContact(@ModelAttribute("contact") Contact contact, @RequestParam("profileImage") MultipartFile file, HttpSession session, Principal principal) {
		
		Contact oldContactDetails = this.contactRepository.getReferenceById(contact.getId());
		try {
			if(!file.isEmpty()) {
				// delete old image file
				System.out.println("image is empty again!");
				if(oldContactDetails.getImageUrl()!= null && !oldContactDetails.getImageUrl().equals("default_contact.png")) {
					File deleteFile = new ClassPathResource("static/images").getFile();
					File file2 = new File(deleteFile,oldContactDetails.getImageUrl());
					file2.delete();
				}				
				// update latest image
				System.out.println("saving the latest image");
				contact.setImageUrl(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()
						+ File.separator  + contact.getImageUrl()/* + file.getOriginalFilename() */);
				Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
				
			}
			else {
				System.out.println("continung wth old image");
				contact.setImageUrl(oldContactDetails.getImageUrl());
			}
			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Your contact has been succesfully updated","success"));
		}catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return "redirect:/user/view_contact/0/"+contact.getId() ;
	}
	
	/* Handler to view User profile */
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Smart Contact Manager | Profile");
		return "normal/profile";
	}
	
	@GetMapping("/settings")
	public String openSettings(Model model) {
		model.addAttribute("title","Smart Contact Dashboard | Settings");
		return "normal/settings";
	}
	
	@GetMapping("/change_password_form")
	public String changePasswordForm(Model model) {
		model.addAttribute("title","Smart Contact Dashboard | Change Password");
		return "normal/password_change_form";
	}
	
	/* Change password handler */
	@PostMapping("/change_password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword, Principal principal,
			HttpSession session) {
			
		User user = this.userRepository.getUserByUserName(principal.getName());
		if(this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
			// change the password
			user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(user);
			session.setAttribute("message", new Message("Password has been updated successfully", "success"));
		}
		else {
			// Inform user that the password is incorrect
			session.setAttribute("message", new Message("Entered current password is incorrect", "danger"));
			return "redirect:/user/settings";
		}
		System.out.println("old password " + oldPassword + ", new password " + newPassword);
		return "redirect:/user/index";
	}
	
	@GetMapping("/change_name_form")
	public String changeNameForm(Principal principal, Model model) {
		model.addAttribute("title","Smart Contact Dashboard | Change Name");
		return "normal/change_name";
	}
	
	@PostMapping("/change_name")
	public String changeName(@RequestParam("newName") String newName, Principal principal, HttpSession session) {
		User user = this.userRepository.getUserByUserName(principal.getName());
		user.setName(newName);
		this.userRepository.save(user);
		session.setAttribute("message", new Message("UserName succesfully updated","success"));
		return "redirect:/user/settings";
	}
	
	@GetMapping("/change_profilePicture_form")
	public String changeProfilePictureForm(Model model) {
		model.addAttribute("title","Smart Contact Dashboard | Change Profile Picture");
		return "normal/change_profilePicture_form";
	}
	
	@PostMapping("/change_profile_picture")
	public String changeProfilePicture(@RequestParam("newProfileImage") MultipartFile file ,Principal principal, HttpSession session) {
		if(!file.isEmpty()) {
			
			try {
				System.out.println("Storing the new profile picture");
				User user = this.userRepository.getUserByUserName(principal.getName());
				File saveFile = new ClassPathResource("static/images").getFile();
				System.out.println("SaveFile is done");
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "users"+ File.separator+user.getId()
						+ ".png"/* File.separator+file.getOriginalFilename() */);
				System.out.println("Copied path " + path);
				System.out.println(file.getInputStream());
				Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
				user.setImageUrl("users/"+user.getId()+".png");
				this.userRepository.save(user);
				System.out.println("Successfully copied");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//contact.setImageUrl(path.toString());
		}
		else {
			System.out.println("No image uploaded");
			session.setAttribute("message", new Message("Upload a valid photo","danger"));
			return "redirect:/user/change_profilePicture_form";
			//contact.setImageUrl("default_contact.png");
		}	
		session.setAttribute("message", new Message("Profile picture succesfully updated","success"));
		return "redirect:/user/settings";
	}
	
	@GetMapping("/change_personal_note_form")
	public String changePersonalNoteForm(Model model) {
		model.addAttribute("title","Smart Contact Dashboard | Change Personal note");
		return "normal/change_note_form";
	}
	
	@PostMapping("/change_personal_note")
	public String changePersonalNote(@RequestParam("newPersonalNote") String note ,Principal principal, HttpSession session) {
		User user = this.userRepository.getUserByUserName(principal.getName());
		user.setAbout(note);
		this.userRepository.save(user);
		session.setAttribute("message", new Message("Personal note successfully updated","success"));
		return "redirect:/user/settings";
	}
}
