package com.pie.binarytable.dao;

import com.pie.binarytable.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@Repository
public interface UserDAO extends JpaRepository<User, Long>
{
	User findByUsername(String email);
	User findByIdEquals(Long id);
	User findByName(String name);
	//User findByUserAccountsId(Long id);
	User findByGoogleUsername(String googleUsername);
	User findByGoogleName(String googleName);
}
