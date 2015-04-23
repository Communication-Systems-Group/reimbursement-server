package ch.uzh.csg.reimbursement.server.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.IOException;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "User")
@Transactional
public class User {

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

	@OneToOne(cascade = ALL, orphanRemoval = true)
	@JoinColumn(name = "signature_id")
	private Signature signature;

	public User(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.uid = UUID.randomUUID().toString();
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
		return signature.getContent();
	}

	public void addSignatureCropping(int width, int height, int top, int left) {
		signature.addCropping(width, height, top, left);
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at all.
	 */
	protected User() {
	}
}
