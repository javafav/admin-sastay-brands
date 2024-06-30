package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
@Transactional
public class UserService {

	public static final Integer USERS_PER_PAGE = 4;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<User> listAll(Sort sort) {

		return (List<User>) userRepo.findAll(sort);
	}

	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll();
	}

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
     helper.listEntities(pageNum, USERS_PER_PAGE, userRepo);
	}

	public User save(User user) {
		boolean isUpdaingUser = (user.getId() != null);
		if (isUpdaingUser) {
			User existsingUser = userRepo.findById(user.getId()).get();
			if (user.getPassword().isEmpty()) {
				user.setPassword(existsingUser.getPassword());
			} else {
				encodePassword(user);
			}
		} else {
			encodePassword(user);
		}

		return userRepo.save(user);
	}

	private void encodePassword(User user) {
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
	}

	public boolean isEmailUnique(Integer id, String email) {

		User userByEmail = userRepo.getUserByName(email);
		if (userByEmail == null)
			return true;
		boolean isCreatingNew = (id == null);
		if (isCreatingNew) {
			if (userByEmail != null) {
				return false;
			}
		} else {
			if (userByEmail.getId() != id) {
				return false;
			}
		}
		return true;
	}

	public User getUser(Integer id) throws UserNotFoundException {

		try {
			return userRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("User not found with given ID " + id);
		}
	}

	public void deleteUser(Integer id) throws UserNotFoundException {
		Long countById = userRepo.countById(id);
		if (countById == 0 || countById == null) {
			throw new UserNotFoundException("User not found with given ID " + id);

		}
		userRepo.deleteById(id);
	}

	public void updateUserEnableStatus(Integer userId, boolean status) throws UserNotFoundException {
		try {
			User user = userRepo.findById(userId).get();
			if (user != null) {
				userRepo.updateEnabledStatus(userId, status);
			}
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("User not found with given ID " + userId);
		}

	}

	public User getUserByEmail(String email) {
		return userRepo.getUserByName(email);

	}

	public User updateAccount(User userInform) {
		User userInDB = userRepo.findById(userInform.getId()).get();
		if (!userInform.getPassword().isEmpty()) {
			encodePassword(userInform);
		}
		if (userInform.getPhotos() != null) {
			userInDB.setPhotos(userInform.getPhotos());
		}
		userInDB.setFirstName(userInform.getFirstName());
		userInDB.setLastName(userInform.getLastName());
		return userRepo.save(userInDB);

	}
}
