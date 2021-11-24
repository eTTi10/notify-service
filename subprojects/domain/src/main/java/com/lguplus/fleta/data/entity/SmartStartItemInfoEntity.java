package com.lguplus.fleta.data.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Getter
public class SmartStartItemInfoEntity implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "pannel_id")
    private String pannel_id;

    @Id
    @Column(name = "title_id")
    private String title_id;

    @Column(name = "category_gb")
    private String category_gb;

    @Column(name = "category_type")
    private String item_type;

    @Column(name = "title_nm")
    private String item_title;

    @Column(name = "category_id")
    private String genre_code;

    @Column(name = "ui_type")
    private String ui_type;

    @Column(name = "bg_img_file")
    private String bg_img_file;

    @Column(name = "description")
    private String description;

    @Column(name = "ordered")
    private String order_seq;
}