package ch.uzh.csg.reimbursement.model;

import static ch.uzh.csg.reimbursement.model.Role.PROF;
import static ch.uzh.csg.reimbursement.model.Role.USER;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.model.exception.SignatureNotFoundException;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "User")
@Transactional
@JsonIgnoreProperties({"signature"})
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "uid")
public class User {

	@Transient
	private final Logger LOG = LoggerFactory.getLogger(User.class);

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@Getter
	@Column(nullable = false, updatable = true, unique = true, name = "uid")
	private String uid;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "first_name")
	private String firstName;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "last_name")
	private String lastName;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "email")
	private String email;

	@Getter
	@Setter
	@Column(nullable = true, updatable = true, unique = false, name = "manager_name")
	private String managerName;

	@ElementCollection(fetch = EAGER, targetClass = Role.class)
	@JoinTable(name = "Role", joinColumns = @JoinColumn(name = "user_id"))
	@Column(nullable = false, updatable = true, unique = false, name = "role")
	@Enumerated(STRING)
	private Set<Role> roles;

	@Getter
	@Setter
	@ManyToOne(optional = true)
	@JoinColumn(name="manager_id")
	private User manager;

	@OneToMany(mappedBy="manager", fetch = LAZY)
	private Set<User> subordinates = new HashSet<User>();

	@OneToOne(cascade = ALL, orphanRemoval = true)
	@JoinColumn(name = "signature_id")
	private Signature signature;

	public User(String firstName, String lastName, String uid, String email, String managerName, Set<String> ldapDn) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.uid = uid;
		this.email = email;
		this.managerName = managerName;
		setRoles(ldapDn);
	}

	public void setSignature(MultipartFile multipartFile) {
		byte[] content = null;
		try {
			content = multipartFile.getBytes();
		} catch (IOException e) {
			// TODO sebi | create a reasonable exception handling here.
			e.printStackTrace();
		}
		signature = new Signature(multipartFile.getContentType(), multipartFile.getSize(), content);
	}

	public byte[] getSignature() {
		if (signature == null) {
			LOG.debug("No signature found for the user with uid: " + this.uid);
			throw new SignatureNotFoundException();
		}
		return signature.getCroppedContent();
	}

	public void addSignatureCropping(int width, int height, int top, int left) {
		signature.addCropping(width, height, top, left);
	}

	public Set<Role> getRoles() {
		return Collections.unmodifiableSet(roles);
	}

	/*
	 * Used by the synchronize method called all x hours but also by the LdapDbUpdateAuthoritiesPopulator at login time
	 */
	public void setRoles(Set<String> ldapDn) {
		roles = new HashSet<Role>();
		roles.add(USER);

		for(String dnName : ldapDn) {
			if(dnName.equals("ou=Professors")) {
				roles.add(PROF);
			}
		}
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at all.
	 */
	protected User() {
	}
}
