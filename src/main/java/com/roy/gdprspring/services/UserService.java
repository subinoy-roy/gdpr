package com.roy.gdprspring.services;

import com.roy.gdprspring.annotations.Encrypt;
import com.roy.gdprspring.dtos.UserDto;
import com.roy.gdprspring.entities.AppUser;
import com.roy.gdprspring.repositories.UserRepo;
import org.springframework.stereotype.Service;

/**
 * Service class for managing users.
 */
@Service
public class UserService {

    private final UserRepo userRepo;

    /**
     * Constructor for UserService.
     *
     * @param userRepo the user repository to be used by this service
     */
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Saves a new user.
     *
     * @param userDto the user data transfer object containing user details
     * @return the saved user data transfer object
     */
    @Encrypt
    public UserDto saveUser(UserDto userDto) {
        AppUser appUser = new AppUser();
        appUser.setName(userDto.getName());
        appUser.setEmail(userDto.getEmail());
        appUser.setInstagramHandle(userDto.getInstagramHandle());
        AppUser savedUser = userRepo.save(appUser);
        userDto.setId(savedUser.getId());
        return userDto;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the user data transfer object of the retrieved user, or null if the user is not found
     */
    public UserDto getUserById(long id) {
        if(userRepo.findById(id).isPresent()){
            AppUser appUser = userRepo.findById(id).get();
            UserDto userDto = new UserDto();
            userDto.setId(appUser.getId());
            userDto.setName(appUser.getName());
            userDto.setEmail(appUser.getEmail());
            userDto.setInstagramHandle(appUser.getInstagramHandle());
            return userDto;
        }
        return null;
    }
}