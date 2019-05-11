package com.pie.binarytable.repositories;

import com.pie.binarytable.entities.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Repository
public interface GoalRepository extends JpaRepository<Goal, Long>
{
	Goal findByIdEquals(Long id);
	List<Goal> findByUserId(Long userId);
	Goal findByGoalNameEqualsAndUserIdEqualsAndGoalTimestampEquals(String goalName, Long userId, String timestamp);
}
