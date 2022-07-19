package com.lguplus.fleta.data.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;

@Builder
@Entity
@Table(name = "pt_cm_push_target", schema = "smartux")
public class PushTargetEntity implements Serializable {

    @Column(name = "p_key")
    private Integer pKey;

    @Id
    @Column(name = "reg_No")
    private Integer regNo;

    @Column(name = "sa_Id")
    private String saId;

    @Column(name = "stb_Mac")
    private String stbMac;

    @Column(name = "album_Id")
    private String albumId;

    @Column(name = "category_Id")
    private String categoryId;

    @Column(name = "service_Type")
    private String serviceType;

    @Column(name = "msg")
    private String msg;

    @Column(name = "push_Yn")
    private String pushYn;

    @Column(name = "result_Code")
    private String resultCode;

    @Column(name = "reg_Dt")
    private String regDt;

    @Column(name = "send_Dt")
    private String sendDt;

    @Column(name = "mod_Dt")
    private String modDt;
}
