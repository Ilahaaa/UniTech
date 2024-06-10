package com.example.unitech.service.impl;

import com.example.unitech.dto.UserDto;
import com.example.unitech.entity.Role;
import com.example.unitech.entity.User;
import com.example.unitech.repository.RoleRepository;
import com.example.unitech.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.findByPin(anyString())).thenReturn(null);

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(new Role("ADMIN")));

        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        UserDto userDto = new UserDto();
        userDto.setPin("123456");
        userDto.setPassword("password");

        UserDto result = assertDoesNotThrow(() -> userService.registerUser(userDto));
        assertNotNull(result);
        assertEquals(userDto.getPin(), result.getPin());

        verify(userRepository, times(1)).findByPin("123456");

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testRegisterUser_Failure_UserAlreadyRegistered() {
        when(userRepository.findByPin(anyString())).thenReturn(new User());

        UserDto userDto = new UserDto();
        userDto.setPin("123456");
        userDto.setPassword("password");

        assertThrows(BadRequestException.class, () -> userService.registerUser(userDto));
    }

    @Test
    void testGetUserByPin_Success() {
        String pin = "123456";
        User user = new User();
        user.setPin(pin);

        when(userRepository.findByPin(pin)).thenReturn(user);

        User resultUser = userService.getUserByPin(pin);
        assertNotNull(resultUser);
        assertEquals(pin, resultUser.getPin());
    }

    @Test
    void testGetUserByPin_UserNotFound() {
        String pin = "123456";

        when(userRepository.findByPin(pin)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByPin(pin));
    }

    @Test
    void testLoadUserByUsername_Success() {
        String pin = "123456";
        User user = new User();
        user.setPin(pin);
        user.setPassword("encodedPassword");
        user.setRoles(Collections.emptySet()); // Mocking roles to return an empty set

        when(userRepository.findByPin(pin)).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername(pin);
        assertNotNull(userDetails);
        assertEquals(pin, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String pin = "123456";

        when(userRepository.findByPin(pin)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(pin));
    }


}
