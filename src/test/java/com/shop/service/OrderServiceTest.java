package com.shop.service;

import com.shop.Repository.ItemRepository;
import com.shop.Repository.MemberRepository;
import com.shop.Repository.OrderRepository;
import com.shop.constant.ItemSellStatus;
import com.shop.constant.OrderStatus;
import com.shop.constant.Role;
import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistoryDto;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    public Item saveItem() {
        Item item = Item.builder()
                .itemName("Test Item")
                .price(1000)
                .itemDetail("Test Item is So Cool!")
                .itemSellStatus(ItemSellStatus.SELL)
                .stockNumber(100)
                .build();
        return itemRepository.save(item);
    }

    public Member saveMember() {
        Member member = Member.builder()
                .name("Tom")
                .address("Seoul")
                .password("1234")
                .role(Role.USER)
                .email("test@test.com").build();
        return memberRepository.save(member);
    }



    @Test
    @DisplayName("주문 테스트")
    @org.junit.jupiter.api.Order(1)
    public void order() {
        Item item = saveItem();
        Member member = saveMember();

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());

        Long orderId = orderService.order(orderDto, member.getEmail());

        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        List<OrderItem> orderItems = order.getOrderItems();

        int totalPrice = orderDto.getCount()*item.getPrice();

        assertEquals(totalPrice, order.getTotalPrice());
    }

    @Test
    @DisplayName("구매 이력 조회 테스트")
    public void orderListTest() {
        Pageable pageable = PageRequest.of(0,4);
        Page<OrderHistoryDto> orderHistoryDtoList = orderService.getOrderList("test@test.com",pageable);
        System.out.println("test");

    }

    @Test
    @DisplayName("주문 취소 테스트")
    public void orderCancelTest() {
        Item item = saveItem();
        Member member = saveMember();

        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(item.getId());
        orderDto.setCount(10);

        Long orderId = orderService.order(orderDto,member.getEmail());

        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        orderService.cancelOrder(orderId);

        assertEquals(OrderStatus.CANCEL, order.getOrderStatus());
        assertEquals(100, item.getStockNumber());
    }
}