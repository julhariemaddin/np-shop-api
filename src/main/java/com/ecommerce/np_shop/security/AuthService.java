package com.ecommerce.np_shop.security;

import com.ecommerce.np_shop.dto.api.auth.LoginRequest;
import com.ecommerce.np_shop.dto.api.auth.LoginResponse;
import com.ecommerce.np_shop.dto.api.auth.RegisterRequest;
import com.ecommerce.np_shop.dto.api.auth.RegisterResponse;
import com.ecommerce.np_shop.entity.Account;
import com.ecommerce.np_shop.exception.customException.NpBadCredentialsException;
import com.ecommerce.np_shop.repo.AccountRepository;
import com.ecommerce.np_shop.repo.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String password = passwordEncoder.encode(registerRequest.getPassword());
        String email = registerRequest.getEmail();
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setEmail(email);
        account.addRole(roleRepository.getRoleByName("ROLE_USER"));
        Account saveAccount = accountRepository.save(account);
        return new RegisterResponse(saveAccount.getId(), saveAccount.getUsername(), saveAccount.getEmail());
    }

    public LoginResponse login(LoginRequest loginRequest) throws NpBadCredentialsException {
        String username = loginRequest.getUsername();
        List<Account> foundAccounts = accountRepository.findAllByUsername(username);
        if(foundAccounts.isEmpty()){
            throw new NpBadCredentialsException("Bad Credentials");
        }
        for(Account account : foundAccounts){
            if(passwordEncoder.matches(loginRequest.getPassword(), account.getPassword())){
                return LoginResponse.builder()
                        .token(jwtService.generateToken(new AccountDetails(account)))
                        .username(account.getUsername())
                        .email(account.getEmail())
                        .build();
            }
        }
        throw new NpBadCredentialsException("Bad Credentials");
    }
}
