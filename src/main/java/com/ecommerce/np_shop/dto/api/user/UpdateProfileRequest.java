package com.ecommerce.np_shop.dto.api.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

  @Size(min = 3, max = 20, message = "Username must be 3-20 characters")
  @Pattern(
      regexp = "^[a-zA-Z0-9._-]+$",
      message = "Username can only contain letters, numbers, dots, dashes, underscores")
  private String username;

  @Email(message = "Invalid email format")
  private String email;
}
