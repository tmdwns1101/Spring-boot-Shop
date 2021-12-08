package com.shop.Repository;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest() {
        Item item = Item.builder()
                .itemName("Test Item12")
                .price(1000)
                .itemDetail("Test item detail!")
                .itemSellStatus(ItemSellStatus.SELL)
                .stockNumber(100)
                .build();
        Item savedItem = itemRepository.save(item);
        assertEquals(1, savedItem.getId());
        assertEquals("Test Item12", savedItem.getItemName());

    }

    @Test
    @DisplayName("3000원 이하 상품 조회")
    public void findItemListByPriceLessThanTest() {
        for(int i=0; i<10; i++) {
            Item item = Item.builder()
                    .itemName("Test Item"+i)
                    .price(2995+i)
                    .itemDetail("Test item detail!" + i)
                    .itemSellStatus(ItemSellStatus.SELL)
                    .stockNumber(100+i)
                    .build();
            itemRepository.save(item);
        }

        assertEquals(6, itemRepository.findItemListByPriceLessThan(3000).size());
    }
}