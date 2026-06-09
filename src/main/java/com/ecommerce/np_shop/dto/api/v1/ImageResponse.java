package com.ecommerce.np_shop.dto.api.v1;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ImageResponse{
    private UUID id;
    private String url;
    private String fileName;
    private String contentType;
}
