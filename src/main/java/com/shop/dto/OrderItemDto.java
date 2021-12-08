package com.shop.dto;

import com.shop.entity.OrderItem;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemDto {

    private String itemName;

    private int count;

    private int orderPrice;

    private String imgUrl;

    public OrderItemDto(OrderItem orderItem, String imgUrl) {
        System.out.println(orderItem);
        System.out.println(orderItem.getItem().getItemName());
        System.out.println(orderItem.getCount());
        System.out.println(orderItem.getOrderPrice());
        this.itemName = orderItem.getItem().getItemName();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
    }

}
