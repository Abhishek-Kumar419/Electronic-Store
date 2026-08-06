package com.lcwd.electronic.store.dtos;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CreateOrderRequest {


    private String orderStatus;
    private String paymentStatus;
    private String billingAddress;
    private String billingPhone;
    private String billingName;

    private String userId;
    private String cartId;
}
