package com.ecommerce.np_shop.security;

import com.ecommerce.np_shop.entity.Account;
import com.ecommerce.np_shop.repo.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountDetailService {
    private final AccountRepository accountRepository;
    public UserDetails loadUserById(UUID id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Account not found"));
        return new AccountDetails(account);
    }
}
