package com.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainItemDto {

    private Long id;

    private String itemName;

    private String itemDetail;

    private int price;

    private String imgUrl;
}
