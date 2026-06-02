package com.ecommerce.np_shop.security;


import com.ecommerce.np_shop.entity.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

public class AccountDetails implements UserDetails {
    private final Account account;
    public AccountDetails(Account account) {
        this.account = account;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return account.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
    }
    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getUsername();
    }

    public UUID getId() {
        return account.getId();
    }
}
