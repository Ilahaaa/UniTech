package com.example.unitech.mapper;

import com.example.unitech.dto.AccountDto;
import com.example.unitech.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
@Mapper
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);
    @Mapping(target = "userId", source = "user.id")
    AccountDto mapToDto(Account account);
    @Mapping(target = "user.id", source = "userId")
    Account mapToEntity(AccountDto accountDto);
}
