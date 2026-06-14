package com.ecommerce.np_shop.payment.dto;

import com.ecommerce.np_shop.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentResponse {
    private String approvalUrl;
    private String paymentId;
    private String paymentStatus;

}
