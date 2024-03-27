package com.example.unitech.repository;


import com.example.unitech.entity.Account;
import com.example.unitech.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserAndIsActiveTrue(User user);
}
