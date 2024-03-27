package com.example.unitech.service.impl;

import com.example.unitech.dto.UserDto;
import com.example.unitech.entity.Role;
import com.example.unitech.entity.User;

import com.example.unitech.mapper.UserMapper;
import com.example.unitech.repository.RoleRepository;
import com.example.unitech.repository.UserRepository;
import com.example.unitech.service.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }


    @Override
    public UserDto registerUser(UserDto userDto) throws BadRequestException {
        User existingUser = userRepository.findByPin(userDto.getPin());
        if (existingUser != null) {
            throw new BadRequestException("User is already registered");
        }

        User user = new User();
        if (userDto.getPin() != null) {
            user.setPin(userDto.getPin());
        }

        if (userDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        Role roles = roleRepository.findByName("ADMIN").get();
        user.setRoles(Collections.singleton(roles));

        user = userRepository.save(user);

        return UserMapper.INSTANCE.mapToDto(user);
    }


    @Override
    public User getUserByPin(String pin) {
        User existingUser = userRepository.findByPin(pin);
        if (existingUser != null) {
            return existingUser;
        } else {
            throw new UsernameNotFoundException("User not found with PIN: " + pin);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String pin) throws UsernameNotFoundException {
        User user = userRepository.findByPin(pin);

        if (user == null) {

            throw new UsernameNotFoundException("User not found with PIN: " + pin);
        }

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getPin(),
                user.getPassword(),
                authorities
        );
    }


}



