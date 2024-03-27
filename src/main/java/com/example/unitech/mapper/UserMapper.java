package com.example.unitech.mapper;

import com.example.unitech.dto.UserDto;
import com.example.unitech.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserDto mapToDto(User user);

    User mapToEntity(UserDto userDto);
}
