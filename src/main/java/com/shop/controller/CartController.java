package com.shop.controller;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/cart")
    @ResponseBody
    public ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult, Principal principal) {
        if(bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(FieldError fieldError: fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        String email = principal.getName();
        Long cartItemId;

        try {
            cartItemId = cartService.addCart(cartItemDto, email);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @GetMapping(value = "/cart")
    public String orderHistory(Principal principal, Model model) {
        List<CartDetailDto> cartDetailDtoList = cartService.getCartList(principal.getName());
        model.addAttribute("cartItems", cartDetailDtoList);

        return "cart/cartList";
    }

    @PatchMapping(value = "/cart/cartItem/{cartItemId}")
    @ResponseBody
    public ResponseEntity updateCartItem(@PathVariable Long cartItemId, int count, Principal principal) {
        if(count <= 0) {
            return new ResponseEntity<String>("?????? 1??? ?????? ???????????????.",HttpStatus.BAD_REQUEST);
        } else if(cartService.validateCartItem(cartItemId, principal.getName())) {
            return new ResponseEntity<String>("?????? ????????? ????????????.",HttpStatus.FORBIDDEN);
        } else {

            cartService.updateCartItem(cartItemId, count);
            return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/cart/cartItem/{cartItemId}")
    @ResponseBody
    public ResponseEntity deleteCartItem(@PathVariable Long cartItemId, Principal principal) {
        if(!cartService.validateCartItem(cartItemId, principal.getName())) {
            return new ResponseEntity<String>("?????? ????????? ????????????.",HttpStatus.FORBIDDEN);
        }

        cartService.deleteCartItem(cartItemId);
        return  new ResponseEntity<Long>(cartItemId,HttpStatus.OK);
    }

    @PostMapping(value = "/cart/orders")
    @ResponseBody
    public ResponseEntity orderCartItem (@RequestBody CartOrderDto cartOrderDto, Principal principal) {
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        if(cartOrderDtoList == null || cartOrderDtoList.size() == 0) {
            return new ResponseEntity<String>("????????? ????????? ??????????????????.", HttpStatus.BAD_REQUEST);
        }

        for(CartOrderDto cartOrder: cartOrderDtoList) {
            System.out.println(cartOrder.getCartItemId());
            if(!cartService.validateCartItem(cartOrder.getCartItemId(),principal.getName())) {
                return new ResponseEntity<String>("?????? ????????? ????????????.", HttpStatus.FORBIDDEN);
            }
        }

        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());
        return new ResponseEntity<Long>(orderId,HttpStatus.OK);
    }
}
