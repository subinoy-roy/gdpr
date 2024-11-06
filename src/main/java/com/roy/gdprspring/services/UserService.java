package com.roy.gdprspring.services;

import com.roy.gdprspring.annotations.Encrypt;
import com.roy.gdprspring.dtos.UserDto;
import com.roy.gdprspring.entities.AppUser;
import com.roy.gdprspring.repositories.UserRepo;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

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
