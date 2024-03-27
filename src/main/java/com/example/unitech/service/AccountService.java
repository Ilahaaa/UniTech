package com.example.unitech.service;


import com.example.unitech.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {


   List< BigDecimal> getActiveAccountsBalance(User loggedInUser);

}
