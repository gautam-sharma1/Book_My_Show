package com.cfs.BookMyShow.service;

import com.cfs.BookMyShow.dto.LoginRequest;
import com.cfs.BookMyShow.dto.UserRequest;
import com.cfs.BookMyShow.entity.User;
import com.cfs.BookMyShow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;// notice something that after putting @RequiredArgsConstructor we not need to make a constructor for private final fields import
    //register
    public User register(UserRequest request ){ // we can do this also the return type of method  is User the entity and the  type of request or parameter the method is accepting is type of UserRequest the DTO because at the end we are returing the entire user object not the the dto kind of user object
        if(userRepository.existsByEmail(request.getEmail())){
                throw new RuntimeException(" user already Exists by email"+request.getEmail());
        }
            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .phone(request.getPhone())
                    .build();
    return userRepository.save(user);
    }
    //login

    public User Login(LoginRequest request){
     User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("user not found by email"+request.getEmail()));
     if(!user.getPassword().equals(request.getPassword())){
         throw new RuntimeException("Invalid Password");
     }
     return user;


    }
    public List<User> getAllUser(){
        return userRepository.findAll();

    }
    public  User getUserById(Long id){
     return userRepository.findById(id)
             .orElseThrow(() -> new RuntimeException("user not found by id"+id));
    }

 }
