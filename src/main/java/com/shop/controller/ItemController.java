package com.shop.controller;

import com.shop.dto.ItemDto;
import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFiles) {
        if(bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        if(itemImgFiles.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 값입니다.");
            return "item/itemForm";
        }

        try {
            long saveId = itemService.saveItem(itemFormDto, itemImgFiles);
            log.info("저장 된 이미지 아이디 : " + saveId);
        }catch (Exception e) {
            log.warning(e.getMessage());
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDetail(@PathVariable("itemId") Long itemId, Model model) {
        try {
            ItemFormDto itemFormDto = itemService.getItemDetail(itemId);
            model.addAttribute("itemFormDto",itemFormDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage","존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto",new ItemFormDto());
        }
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, @PathVariable("itemId") Long itemId
                             ,BindingResult bindingResult, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model) {
        if(bindingResult.hasErrors()) {
            return "item/itemForm";
        }
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 값입니다.");
            return "item/itemForm";
        }

        try {
            Long updatedId = itemService.updateItem(itemFormDto, itemImgFileList);
            log.info("updated Item Id : " + updatedId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 예상치 못한 오류가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }

    @GetMapping( value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, Model model, @PathVariable("page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0),3);
        log.info(itemSearchDto.toString());
        Page<ItemDto> itemDtoList = itemService.getAdminItemPage(itemSearchDto,pageable);
        log.info(itemDtoList.getContent().get(0).toString());
        model.addAttribute("items",itemDtoList);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "item/itemMng";
    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDetail(Model model, @PathVariable Long itemId) {
        ItemFormDto itemFormDto = itemService.getItemDetail(itemId);
        model.addAttribute("item",itemFormDto);
        return "item/itemDtl";
    }
}
