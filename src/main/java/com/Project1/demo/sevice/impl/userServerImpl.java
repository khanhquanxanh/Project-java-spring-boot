package com.Project1.demo.sevice.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Project1.demo.dto.request.AddressDTO;
import com.Project1.demo.dto.request.UserRequestDTO;
import com.Project1.demo.dto.response.PageResponse;
import com.Project1.demo.dto.response.UserDetailResponse;
import com.Project1.demo.exception.ResourceNotFoundException;
import com.Project1.demo.model.Address;
import com.Project1.demo.model.Role;
import com.Project1.demo.model.User;
import com.Project1.demo.model.UserHasRole;
import com.Project1.demo.repository.RoleRepository;
import com.Project1.demo.repository.SearchRepository;
import com.Project1.demo.repository.UserRepository;
import com.Project1.demo.sevice.UserServer;
import com.Project1.demo.util.UserStatus;
import com.Project1.demo.util.UserType;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class userServerImpl implements UserServer{
	
	private final UserRepository userRepository;
	
	private final SearchRepository repository;
	
	private final MailService mailService;
	
	private final PasswordEncoder passwordEncoder;
	
	private final RoleRepository roleRepository;
	
	@Override
	public long saveUser(UserRequestDTO request) throws UnsupportedEncodingException, MessagingException {
		
		

		User user = User.builder()
                .firstName(request.getFistname())
                .lastName(request.getLastname())
                .dateOfBirth(request.getDateOfbirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(request.getStatus())
                .type(UserType.valueOf(request.getType().toUpperCase()))
                .build();
		request.getAddresses().forEach(a ->
				user.saveAddress(Address.builder()
						.apartmentNumber(a.getApartmentNumber())
						.floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build()));
		
		Role defaultRole = roleRepository.findById(4)
		        .orElseThrow(() -> new RuntimeException("Role with ID 4 not found"));

		UserHasRole userHasRole = UserHasRole.builder()
		        .user(user)
		        .role(defaultRole)
		        .build();

		user.addRole(userHasRole);
		
		userRepository.save(user);
		
		log.info("User has save");
		

        if (user.getId() != null) {
            mailService.sendConfirmLink(user.getEmail(), user.getId(), "XLFJENSA");
        }

        return user.getId();
		
	}
	
	@Override
    public String confirmUser(int userId, String verifyCode) {
        return "Confirmed!";
    }

	@Override
	public void updateUser(long userId, UserRequestDTO request) {
		User user = getUserById(userId);
        user.setFirstName(request.getFistname());
        user.setLastName(request.getLastname());
        user.setDateOfBirth(request.getDateOfbirth());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        if (!request.getEmail().equals(user.getEmail())) {
            // check email from database if not exist then allow update email otherwise throw exception
            user.setEmail(request.getEmail());
        }
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setStatus(request.getStatus());
        user.setType(UserType.valueOf(request.getType().toUpperCase()));
        user.setAddresses(convertToAddress(request.getAddresses()));
        userRepository.save(user);

        log.info("User has updated successfully, userId={}", userId);
		
	}

	@Override
	public void changeStatus(long userId, UserStatus request) {
		User user = getUserById(userId);
        user.setStatus(request);
        userRepository.save(user);

        log.info("User status has changed successfully, userId={}", userId);
		
	}

	@Override
	public void deleteUser(long userId) {
		userRepository.deleteById(userId);
        log.info("User has deleted permanent successfully, userId={}", userId);
		
	}

	@Override
	public UserDetailResponse getUser(long userId) {
		User user = getUserById(userId);
        return UserDetailResponse.builder()
                .id(userId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .phone(user.getPhone())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .type(user.getType().name())
                .build();
	}

//	@Override
//	public List<UserDetailResponse> getAllUsers(int pageNo, int pageSize, String sortBy) {
//		
//		int p =0;
//		if(pageNo>0) {
//			p=pageNo-1;
//		}
//		
//		//check valid?
//		
//		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, sortBy));
//		Page<User> users = userRepository.findAll(pageable);
//		return users.stream().map(user -> UserDetailResponse.builder()
//				.firstName(user.getFirstName())
//				.lastName(user.getLastName())
//				.email(user.getEmail())
//				.phone(user.getPhone())
//				.build())
//				.toList();
//	}
	
//	@Override
//	public List<UserDetailResponse> getAllUsers(int pageNo, int pageSize, String sortBy) {
//		
//		int p =0;
//		if(pageNo>0) {
//			p=pageNo-1;
//		}
//		
//		//check valid?
//		
//		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, sortBy));
//		Page<User> users = userRepository.findAll(pageable);
//		return users.stream().map(user -> UserDetailResponse.builder()
//				.firstName(user.getFirstName())
//				.lastName(user.getLastName())
//				.email(user.getEmail())
//				.phone(user.getPhone())
//				.build())
//				.toList();
//	}
	
	@Override
    public PageResponse getAllUsers(int pageNo, int pageSize, String... sorts) {
		int page = 0;
        if (pageNo > 0) {
            page = pageNo - 1;
        }

        List<Sort.Order> orders = new ArrayList<>();

        if (sorts != null) {
            for (String sortBy : sorts) {
                log.info("sortBy: {}", sortBy);
                // firstName:asc|desc
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sortBy);
                if (matcher.find()) {
                    if (matcher.group(3).equalsIgnoreCase("asc")) {
                        orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    } else {
                        orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                }
            }
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(orders));

        Page<User> users = userRepository.findAll(pageable);
        List<UserDetailResponse> response = users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()).toList();
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(users.getTotalPages())
                .items(response)
                .build();
    }
	private List<Address> convertToAddress(List<AddressDTO> addresses) {
        List<Address> result = new ArrayList<>();
        addresses.forEach(a ->
                result.add(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build())
        );
        return result;
    }
	
	@Override
	public  User getUserById(long id) {	
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	@Override
	public PageResponse getAllUsersWithSortByMultipleColumnsAndSearch(int pageNo, int pageSize, String search,
			String sortBy) {
		// TODO Auto-generated method stub
		return repository.getAllUsersWithSortByMultipleColumnsAndSearch(pageNo, pageSize, search, sortBy);
	}

	@Override
    public PageResponse<?> advanceSearchWithCriteria(int pageNo, int pageSize, String sortBy, String address, String... search) {
        return repository.searchUserByCriteria(pageNo, pageSize, sortBy, address, search);
    }

	@Override
	public PageResponse<?> advanceSearchWithSpecifications(Pageable pageable, String[] user, String[] address) {

		Page<User> users = userRepository.findAll(pageable);
		return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(users.getTotalPages())
                .items(users.stream().toList())
                .build();
	}

	@Override
	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

}
