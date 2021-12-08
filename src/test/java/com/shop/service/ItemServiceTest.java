package com.shop.service;

import com.shop.Repository.ItemRepository;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.entity.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;


    @Test
    @DisplayName("RuntimeException에서 롤백 테스트")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void rollbackForRuntimeExceptionTest() {
        ItemFormDto itemFormDto = new ItemFormDto();
        itemFormDto.setItemName("test");
        itemFormDto.setItemDetail("tetst");
        itemFormDto.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDto.setPrice(1000);
        itemFormDto.setStockNumber(10);

        try {
            itemService.saveItem(itemFormDto, new ArrayList<>());
        } catch (Exception e) {
            List<Item> items = itemRepository.findAll();
            assertEquals(1, items.size()); // 실제로 트랜잭션이 끝나야 롤백 되므로
        }

    }
}