package com.Project1.demo.dto.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import com.Project1.demo.model.UserHasRole;
import com.Project1.demo.util.EnumPattern;
import com.Project1.demo.util.EnumValue;
import com.Project1.demo.util.Gender;
import com.Project1.demo.util.GenderSubset;
import com.Project1.demo.util.PhoneNumber;
import com.Project1.demo.util.UserStatus;
import com.Project1.demo.util.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRequestDTO {


	@NotBlank(message = "fistname must not be blank")
	private String fistname;
	
	@NotNull(message = "lastname must be not null")
	private String lastname;
	
	@Email(message = "email invalid format")
	private String email;
	
	//@Pattern(regexp = "^\\d{10}$", message = "phone invalid format")
	@PhoneNumber
	private String phone;
	
	@NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
	private Date dateOfbirth;
	
	@EnumPattern(name = "status", regexp = "ACTIVE|INACTIVE|NONE")
	private UserStatus status;
	
	@GenderSubset(anyOf = {Gender.MALE,Gender.FEMALE,Gender.OTHER})
	private Gender gender;
	
	@NotNull(message = "type must be not null")
	@EnumValue(name = "type", enumClass = UserType.class)
	private String type;
	
	@NotNull
	private String username;
	
	@NotNull
	private String password;
	
	//@NotEmpty(message = "addresses can not empty")
    private List<AddressDTO> addresses = new ArrayList<>();
    
    private Set<UserHasRole> roles = new HashSet<>();
    
	public UserRequestDTO(@NotBlank(message = "fistname must not be blank") String fistname,
			@NotNull(message = "lastname must be not null") String lastname,
			@Email(message = "email invalid format") String email,
			@Pattern(regexp = "^\\d{10}$", message = "phone invalid format") String phone) {
		super();
		this.fistname = fistname;
		this.lastname = lastname;
		this.email = email;
		this.phone = phone;
	}

	public String getFistname() {
		return fistname;
	}
	public void setFistname(String fistname) {
		this.fistname = fistname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Date getDateOfbirth() {
		return dateOfbirth;
	}
	public void setDateOfbirth(Date dateOfbirth) {
		this.dateOfbirth = dateOfbirth;
	}
	public UserStatus getStatus() {
		return status;
	}
	public void setStatus(UserStatus status) {
		this.status = status;
	}
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	
	
	
	
	
	

	
}
