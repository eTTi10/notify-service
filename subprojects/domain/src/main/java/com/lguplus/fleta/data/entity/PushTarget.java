package com.lguplus.fleta.data.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
@Entity
@Table(name = "pt_cm_push_target", schema = "smartux")
public class PushTarget implements Serializable {

    @Column(name = "p_key")
    private Integer pKey;

    @Id
    @Column(name = "reg_no")
    private Integer regNo;

    @Column(name = "sa_id")
    private String saId;

    @Column(name = "stb_mac")
    private String stbMac;

    @Column(name = "album_id")
    private String albumId;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "service_type")
    private String serviceType;

    @Column(name = "msg")
    private String msg;

    @Column(name = "push_yn")
    private String pushYn;

    @Column(name = "result_code")
    private String resultCode;

    @Column(name = "reg_dt")
    private Timestamp regDt;

    @Column(name = "send_dt")
    private Timestamp sendDt;

    @Column(name = "mod_dt")
    private Timestamp modDt;
}
