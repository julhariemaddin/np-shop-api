package com.ecommerce.np_shop.controller.api.user;

import com.ecommerce.np_shop.dto.api.ApiResponse;
import com.ecommerce.np_shop.dto.api.user.GetProfileResponse;
import com.ecommerce.np_shop.dto.api.user.UpdatePasswordRequest;
import com.ecommerce.np_shop.dto.api.user.UpdateProfileRequest;
import com.ecommerce.np_shop.dto.api.user.UpdateProfileResponse;
import com.ecommerce.np_shop.service.UserService;
import com.ecommerce.np_shop.util.AuthenticationExtractorUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final AuthenticationExtractorUtil authenticationExtractorUtil =
      new AuthenticationExtractorUtil();

  @PatchMapping("/profile")
  public ResponseEntity<ApiResponse<UpdateProfileResponse>> updateProfile(
      Authentication authentication,
      @Valid @RequestBody UpdateProfileRequest updateProfileRequest) {
    UpdateProfileResponse updateProfileResponse =
        userService.updateProfileAndGetResponse(
            authenticationExtractorUtil.getAccountId(authentication), updateProfileRequest);
    return ResponseEntity.ok(
        new ApiResponse<>(true, "Successfully updated profile", updateProfileResponse));
  }

  @PatchMapping("/password")
  public ResponseEntity<ApiResponse<String>> updatePassword(
      Authentication authentication,
      @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
    userService.validateAndUpdatePassword(
        authenticationExtractorUtil.getAccountId(authentication), updatePasswordRequest);
    return ResponseEntity.ok(new ApiResponse<>(true, "Successfully updated password", null));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<GetProfileResponse>> getProfile(Authentication authentication) {
    return ResponseEntity.ok(
        new ApiResponse<>(
            true,
            "Successfully retrieved profile",
            userService.getUserProfile(authenticationExtractorUtil.getAccountId(authentication))));
  }
}
