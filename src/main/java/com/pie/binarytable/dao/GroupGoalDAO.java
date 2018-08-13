package com.pie.binarytable.dao;

import com.pie.binarytable.entities.GroupGoal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Repository
public interface GroupGoalDAO extends CrudRepository<GroupGoal, Long>
{
	ArrayList<GroupGoal> findByUserId(Long id);
	void deleteByGoalId(Long id);
}
