package com.pie.binarytable.repositories;

import com.pie.binarytable.entities.GroupGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Service
@Repository
public interface GroupGoalRepository extends JpaRepository<GroupGoal, Long>
{
	ArrayList<GroupGoal> findByUserId(Long id);
	ArrayList<GroupGoal> findByGoalId(Long id);

	@Transactional
	void deleteByGoalId(Long id);
}
