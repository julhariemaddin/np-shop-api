package com.ecommerce.np_shop.payment.paypal.config;


import com.paypal.sdk.Environment;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaypalConfig {
    @Value("${paypal.client.id}")
    private String clientId;
    @Value("${paypal.client.secret}")
    private String secret;

    @Bean
    public PaypalServerSdkClient paypalServerSdkClient() {
        return new PaypalServerSdkClient.Builder()
                .clientCredentialsAuth(
                        new ClientCredentialsAuthModel.Builder(clientId,secret).build()
                ).environment(Environment.SANDBOX)
                .build();
    }
}
