package com.pie.binarytable.dao;

import com.pie.binarytable.entities.Goal;
import com.pie.binarytable.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Repository
public interface GoalDAO extends CrudRepository<Goal, Long>
{
	Goal findByIdEquals(Long id);
	List<Goal> findByUserId(Long userId);
	Goal findByGoalNameEqualsAndUserIdEquals(String goalName, Long userId);
}
