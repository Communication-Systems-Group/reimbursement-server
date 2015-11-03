package ch.uzh.csg.reimbursement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ch.uzh.csg.reimbursement.model.Role;
import ch.uzh.csg.reimbursement.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("SELECT u FROM User u WHERE u.uid = :uid")
	public User findByUid(@Param("uid") String uid);

	@Query("SELECT u FROM User u WHERE lower(u.lastName) LIKE lower(:lastName)")
	public List<User> findAllByLastName(@Param("lastName") String lastName);

	@Query("SELECT u FROM User u JOIN u.roles roles WHERE roles = :role")
	public User findUserByRole(@Param("role") Role role);

	@Query("SELECT u FROM User u Join u.roles roles WHERE roles = :role AND u.manager = :prof AND u.isActive = true")
	public List<User> getDeputiesForProf(@Param("prof") User prof, @Param("role") Role role);

}