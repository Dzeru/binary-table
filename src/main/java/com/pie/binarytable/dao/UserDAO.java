package com.pie.binarytable.dao;

import com.pie.binarytable.entities.User;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.data.repository.CrudRepository;

@Service
@Repository
public interface UserDAO extends CrudRepository<User, Long>
{
	User findByUsername(String email);
	User findByIdEquals(Long id);
	User findByName(String name);
}
