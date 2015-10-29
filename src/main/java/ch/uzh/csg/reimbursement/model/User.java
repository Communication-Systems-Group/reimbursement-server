package ch.uzh.csg.reimbursement.model;

import static ch.uzh.csg.reimbursement.model.Language.DE;
import static ch.uzh.csg.reimbursement.model.Role.REGISTERED_USER;
import static ch.uzh.csg.reimbursement.model.Role.USER;
import static java.util.Collections.unmodifiableSet;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.IOException;
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

import ch.uzh.csg.reimbursement.model.exception.MaxFileSizeViolationException;
import ch.uzh.csg.reimbursement.model.exception.MinFileSizeViolationException;
import ch.uzh.csg.reimbursement.model.exception.ServiceException;
import ch.uzh.csg.reimbursement.model.exception.SignatureNotFoundException;
import ch.uzh.csg.reimbursement.serializer.UserSerializer;
import ch.uzh.csg.reimbursement.utils.PropertyProvider;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "User_")
@Transactional
@JsonIgnoreProperties({ "signature" })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
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
	@Column(nullable = true, updatable = true, unique = false, name = "personnel_number")
	private String personnelNumber;

	@Getter
	@Setter
	@Column(nullable = true, updatable = true, unique = false, name = "phone_number")
	private String phoneNumber;

	@JsonIgnore
	@Getter
	@Setter
	@Column(nullable = true, updatable = true, unique = false, name = "manager_name")
	private String managerName;

	@ElementCollection(fetch = EAGER, targetClass = Role.class)
	@JoinTable(name = "Role_", joinColumns = @JoinColumn(name = "user_id"))
	@Column(nullable = false, updatable = true, unique = false, name = "role")
	@Enumerated(STRING)
	private Set<Role> roles;

	@JsonSerialize(using = UserSerializer.class)
	@Getter
	@Setter
	@ManyToOne(optional = true)
	@JoinColumn(name = "manager_id")
	private User manager;

	@OneToMany(mappedBy = "manager", fetch = LAZY)
	private Set<User> subordinates = new HashSet<User>();

	@OneToOne(cascade = ALL, orphanRemoval = true)
	@JoinColumn(name = "signature_id")
	private Signature signature;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, columnDefinition="boolean default false", name = "has_signature")
	private Boolean hasSignature = false;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, columnDefinition="boolean default true", name = "is_active")
	private Boolean isActive = true;

	@Getter
	@Setter
	@Enumerated(STRING)
	@Column(nullable = false, updatable = true, unique = false, name = "language")
	private Language language;

	public User(String firstName, String lastName, String uid, String email, String managerName, Set<Role> ldapRoles) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.uid = uid;
		this.email = email;
		this.managerName = managerName;
		setRoles(ldapRoles);
		setLanguage(DE);
		LOG.debug("User constructor: User created");
	}

	public void setSignature(MultipartFile multipartFile) {
		// PropertyProvider can't be removed and replaced with @Value
		// because this is another context
		if (multipartFile.getSize() <= Long.parseLong(PropertyProvider.INSTANCE
				.getProperty("reimbursement.filesize.minUploadFileSize"))) {
			LOG.debug("File too small, allowed: "
					+ PropertyProvider.INSTANCE.getProperty("reimbursement.filesize.minUploadFileSize")
					+ " actual: " + multipartFile.getSize());
			throw new MinFileSizeViolationException();
		} else if (multipartFile.getSize() >= Long.parseLong(PropertyProvider.INSTANCE
				.getProperty("reimbursement.filesize.maxUploadFileSize"))) {
			LOG.debug("File to big, allowed: "
					+ PropertyProvider.INSTANCE.getProperty("reimbursement.filesize.maxUploadFileSize")
					+ " actual: " + multipartFile.getSize());
			throw new MaxFileSizeViolationException();
		} else {
			byte[] content = null;
			try {
				content = multipartFile.getBytes();
			} catch (IOException e) {
				LOG.error("An IOException has been caught while creating a signature.", e);
				throw new ServiceException();
			}
			signature = new Signature(multipartFile.getContentType(), multipartFile.getSize(), content);
			setHasSignature(true);
		}
	}

	public Signature getSignature() {
		if (signature == null) {
			LOG.debug("No signature found for the user with uid: " + this.uid);
			throw new SignatureNotFoundException();
		}
		return signature;
	}

	public void addSignatureCropping(int width, int height, int top, int left) {
		signature.addCropping(width, height, top, left);
	}

	public Set<Role> getRoles() {
		return unmodifiableSet(roles);
	}

	/*
	 * Used by the synchronize method called all x hours but also by the
	 * LdapDbUpdateAuthoritiesPopulator at login time
	 */
	public void setRoles(Set<Role> ldapRoles) {
		roles = ldapRoles;
		roles.add(USER);
	}

	public void addRoleRegisteredUser() {
		roles.add(REGISTERED_USER);
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at
	 * all.
	 */
	protected User() {
	}
}
