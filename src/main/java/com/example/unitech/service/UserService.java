package com.example.unitech.service;

import com.example.unitech.dto.UserDto;
import com.example.unitech.entity.User;
import org.apache.coyote.BadRequestException;

public interface UserService {
    UserDto registerUser(UserDto userDto) throws BadRequestException;

    User getUserByPin(String pin);

}
