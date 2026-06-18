package com.ecommerce.np_shop.payment.dto;

import com.ecommerce.np_shop.entity.OrderItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResource {
    private String id;
    private String status;
    private SupplementaryData supplementary_data;
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SupplementaryData {
        private RelatedIds related_ids;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RelatedIds {
        private String order_id;
    }
}
