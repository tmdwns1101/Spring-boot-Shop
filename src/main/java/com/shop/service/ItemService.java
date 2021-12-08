package com.shop.service;

import com.shop.Repository.ItemImgRepository;
import com.shop.Repository.ItemRepository;
import com.shop.constant.ItemSellStatus;
import com.shop.constant.SearchDateType;
import com.shop.dto.*;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import jdk.jfr.internal.tool.Main;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = {Exception.class})
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        for(int i=0; i<itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if(i == 0) itemImg.setRepImgYn("Y");
            else itemImg.setRepImgYn("N");

            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public ItemFormDto getItemDetail(Long itemId) {
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for(ItemImg itemImg: itemImgList) {
            itemImgDtoList.add(ItemImgDto.of(itemImg));
        }

        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;

    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        for(int i=0; i< itemImgFileList.size(); i++) {
            itemImgService.updateItemImg(itemImgIds.get(i),itemImgFileList.get(i));
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<ItemDto> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        SearchDateType searchDateType = Optional.ofNullable(itemSearchDto.getSearchDateType()).orElse(SearchDateType.ALL);
        ItemSellStatus sellStatus = Optional.ofNullable(itemSearchDto.getSearchSellStatus()).orElse(ItemSellStatus.SELL);
        String searchQuery = itemSearchDto.getSearchQuery();
        String searchBy = Optional.ofNullable(itemSearchDto.getSearchBy()).orElse("itemName");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = searchDateType.calcDate(now);

        Page<Item> itemPage;

        if(searchBy.equals("itemName")) {
            if(searchDateType.equals(SearchDateType.ALL)) itemPage = itemRepository.getAdminItemPageAllRangeByItemName(sellStatus, searchQuery, pageable);
            else itemPage = itemRepository.getAdminItemPageByItemName(start, now, sellStatus, searchQuery, pageable);
        } else {
            if(searchDateType.equals(SearchDateType.ALL)) itemPage = itemRepository.getAdminItemPageAllRangeByCreatedBy(sellStatus, searchQuery, pageable);
            else itemPage = itemRepository.getAdminItemPageByCreatedBy(start, now, sellStatus, searchQuery, pageable);
        }

        return itemPage.map(ItemDto::of);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
         return itemRepository.getMainItemPage(itemSearchDto.getSearchQuery(), pageable).map(item -> {
             String imgUrl = item.getItemImg().get(0).getImgUrl();
             return new MainItemDto(item.getId(), item.getItemName(), item.getItemDetail(), item.getPrice(), imgUrl);
         });
    }
}
