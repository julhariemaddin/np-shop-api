package com.ecommerce.np_shop.service;

import com.ecommerce.np_shop.dto.api.user.GetProfileResponse;
import com.ecommerce.np_shop.dto.api.user.UpdatePasswordRequest;
import com.ecommerce.np_shop.dto.api.user.UpdateProfileRequest;
import com.ecommerce.np_shop.dto.api.user.UpdateProfileResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface UserService {
   UpdateProfileResponse updateProfileAndGetResponse(UUID accountId , UpdateProfileRequest updateUserRequest);
   void validateAndUpdatePassword(UUID accountId, UpdatePasswordRequest updatePasswordRequest);
   GetProfileResponse getUserProfile(UUID accountId);
}
