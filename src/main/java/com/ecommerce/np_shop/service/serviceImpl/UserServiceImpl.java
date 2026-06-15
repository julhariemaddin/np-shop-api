package com.ecommerce.np_shop.service.serviceImpl;

import com.ecommerce.np_shop.dto.api.user.GetProfileResponse;
import com.ecommerce.np_shop.dto.api.user.UpdatePasswordRequest;
import com.ecommerce.np_shop.dto.api.user.UpdateProfileRequest;

import com.ecommerce.np_shop.dto.api.user.UpdateProfileResponse;
import com.ecommerce.np_shop.entity.Account;
import com.ecommerce.np_shop.entity.Role;
import com.ecommerce.np_shop.repo.AccountRepository;
import com.ecommerce.np_shop.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    @Transactional
    public UpdateProfileResponse updateProfileAndGetResponse(UUID accountId, UpdateProfileRequest updateProfileRequest) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("account not found"));
        if(updateProfileRequest.getUsername() != null){
            account.setUsername(updateProfileRequest.getUsername());
        }
        if(updateProfileRequest.getEmail() != null){
            account.setEmail(updateProfileRequest.getEmail());
        }
        Account savedAccount = accountRepository.save(account);
        return new UpdateProfileResponse(savedAccount.getUsername(),account.getEmail());
    }

    @Override
    @Transactional
    public void validateAndUpdatePassword(UUID accountId, UpdatePasswordRequest updatePasswordRequest) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("account not found"));
        if(account.getPassword().equals(passwordEncoder.encode(updatePasswordRequest.getOldPassword()))){
            account.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
            accountRepository.save(account);
        }else{
            throw new RuntimeException("old password not match");
        }
    }

    @Override
    public GetProfileResponse getUserProfile(UUID accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("account not found"));
        GetProfileResponse getProfileResponse = new GetProfileResponse();
        getProfileResponse.setAccountId(account.getId());
        getProfileResponse.setUsername(account.getUsername());
        getProfileResponse.setEmail(account.getEmail());
        getProfileResponse.setRole(account.getRoles().stream()
                .map(Role::getName).toList()
        );
        return getProfileResponse;
    }
}
