package com.smart.dao;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

import jakarta.transaction.Transactional;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
	@Query("FROM Contact as c WHERE c.user.id=:userId ")
	public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM Contact as c WHERE c.id=:cId")
	public void deleteByContactId(@Param("cId") int cId);
	
	
	//Searching method
	public List<Contact> findByNameContainingAndUser(String name, User user);
	
	@Query("SELECT COUNT(*) FROM Contact AS c WHERE c.user.id=:userId")
	public Long getContactsCountByUser(@Param("userId") int userId);
}
