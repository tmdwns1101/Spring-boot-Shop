package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import lombok.Data;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Data
public class ItemDto {
    private Long id;
    private String itemName;
    private int price;
    private int stockNumber;
    private String itemDetail;
    private ItemSellStatus itemSellStatus;
    private LocalDateTime createdAt;
    private String createdBy;

    private static ModelMapper modelMapper = new ModelMapper();
    public static ItemDto of(Item item) {
        return modelMapper.map(item, ItemDto.class);
    }
}
