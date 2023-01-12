package com.lguplus.fleta.data.entity;

import com.lguplus.fleta.data.entity.id.LatestId;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "pt_hdtv_latest", schema = "smartux")
@IdClass(LatestId.class)
public class MobileLatest {

    @Id
    @Column(name = "sa_id")
    private String saId;

    @Id
    @Column(name = "mac")
    private String mac;

    @Id
    @Column(name = "ctn")
    private String ctn;

    @Id
    @Column(name = "cat_id")
    private String catId;

    @Column(name = "reg_id")
    private String regId;

    @Column(name = "cat_name")
    private String catName;

    @Column(name = "close_yn")
    private String closeYn;

    @CreationTimestamp
    @Column(name = "r_date")
    private Timestamp rDate;

    @Column(name = "service_type")
    private String serviceType;
}
