package com.example.unitech.controller;

import com.example.unitech.entity.User;
import com.example.unitech.service.AccountService;
import com.example.unitech.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class AccountController {
    private final AccountService accountService;
    private final UserService userService;

    private static final String GET_ACTIVE_ACCOUNTS="api/accounts";


    public AccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @GetMapping(GET_ACTIVE_ACCOUNTS)
    public ResponseEntity<List<BigDecimal>> getActiveAccounts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String loggedInUsername = authentication.getName();
        User loggedInUser = userService.getUserByPin(loggedInUsername);
        System.out.println(loggedInUsername);
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
       List< BigDecimal> balances = accountService.getActiveAccountsBalance(loggedInUser);


        return ResponseEntity.ok(balances);
    }


    }


