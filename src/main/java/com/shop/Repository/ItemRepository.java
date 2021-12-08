package com.shop.Repository;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i where i.price <= :price")
    List<Item> findItemListByPriceLessThan(@Param("price") int price);

    @Query(
            "select i " +
                    "from Item i " +
                    "where i.createdAt between :start and :end and " +
                    "i.itemSellStatus = :itemSellStatus and " +
                    "i.itemName like %:searchQuery%"
    )
    Page<Item> getAdminItemPageByItemName(@Param("start") LocalDateTime startDateTime, @Param("end") LocalDateTime endDateTime,
                                          @Param("itemSellStatus") ItemSellStatus itemSellStatus, @Param("searchQuery") String searchQuery,
                                          Pageable pageable);

    @Query(
            "select i " +
                    "from Item i " +
                    "where i.itemSellStatus = :itemSellStatus and " +
                    "i.itemName like %:searchQuery%"
    )
    Page<Item> getAdminItemPageAllRangeByItemName(@Param("itemSellStatus") ItemSellStatus itemSellStatus, @Param("searchQuery") String searchQuery,
                                                   Pageable pageable);

    @Query(
            "select i " +
                    "from Item i " +
                    "where i.createdAt between :start and :end and " +
                    "i.itemSellStatus = :itemSellStatus and " +
                    "i.createdBy like %:searchQuery%"
    )
    Page<Item> getAdminItemPageByCreatedBy(@Param("start") LocalDateTime startDateTime, @Param("end") LocalDateTime endDateTime,
                                           @Param("itemSellStatus") ItemSellStatus itemSellStatus, @Param("searchQuery") String searchQuery,
                                           Pageable pageable);

    @Query(
            "select i " +
                    "from Item i " +
                    "where i.itemSellStatus = :itemSellStatus and " +
                    "i.createdBy like %:searchQuery%"
    )
    Page<Item> getAdminItemPageAllRangeByCreatedBy(@Param("itemSellStatus") ItemSellStatus itemSellStatus, @Param("searchQuery") String searchQuery,
                                                   Pageable pageable);


    @Query(value = "select i from Item i join i.itemImg img where i.itemName like %:itemName% and img.repImgYn = 'Y' order by i.id",
    countQuery = "select count(i) from Item i join  i.itemImg img where i.itemName like %:itemName% and img.repImgYn = 'Y'")
    Page<Item> getMainItemPage(@Param("itemName") String searchQuery, Pageable pageable);

}
