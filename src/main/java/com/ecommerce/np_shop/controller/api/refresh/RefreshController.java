package com.ecommerce.np_shop.controller.api.refresh;

import com.ecommerce.np_shop.dto.api.ApiResponse;
import com.ecommerce.np_shop.dto.api.refresh.RefreshRequest;
import com.ecommerce.np_shop.dto.api.refresh.RefreshResponse;
import com.ecommerce.np_shop.redis.service.RefreshService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class RefreshController {
    private final RefreshService refreshService;
    @PostMapping
    public ResponseEntity<ApiResponse<RefreshResponse>> refresh(@RequestBody RefreshRequest refreshRequest){
        return ResponseEntity.ok(new ApiResponse<>(true , "Access token retrieved successfully" ,refreshService.getAccessToken(refreshRequest.getRefreshToken())));
    }
}
