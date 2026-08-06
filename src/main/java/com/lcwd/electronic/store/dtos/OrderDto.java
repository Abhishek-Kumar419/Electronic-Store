package com.lcwd.electronic.store.dtos;

import com.lcwd.electronic.store.entities.OrderItem;
import com.lcwd.electronic.store.entities.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class OrderDto {

    @NotBlank(message = "required")
    private String orderId;

    @Builder.Default
    private String orderStatus = "PENDING";

    @Builder.Default
    private String paymentStatus = "NOTPAID";
    private int orderAmount;

    @NotBlank(message = "required")
    private String billingAddress;
    @NotBlank(message = "required")
    private String billingPhone;
    @NotBlank(message = "required")
    private String billingName;

    private Date orderedDate=new Date();

    private Date deliveredDate;
    //private User user;
    private List<OrderItemDto> orderItems = new ArrayList<>();
}
