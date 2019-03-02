package com.pie.binarytable.dao;

import com.pie.binarytable.entities.UserAccounts;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@Repository
public interface UserAccountsDAO extends CrudRepository<UserAccounts, Long>
{
	UserAccounts findByBinaryTableName(String binaryTableName);
	UserAccounts findByBinaryTableUsername(String binaryTableUsername);
	UserAccounts findByGoogleUsername(String googleUsername);
}
