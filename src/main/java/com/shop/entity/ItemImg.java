package com.shop.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "item_img")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemImg extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_img_id")
    private Long id;

    private String imgName;

    @Column(name = "ori_img_name")
    private String oriImgName;

    private String imgUrl;

    private String repImgYn;  //대표성 이미지 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public void updateItemImg(String oriImgName, String imgName, String imgUrl) {
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
}
