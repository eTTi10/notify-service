package com.lguplus.fleta.data.entity;


import com.lguplus.fleta.data.entity.id.LatestId;
import java.io.Serializable;
import java.util.Date;
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

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
@Entity
@Table(name = "SMARTUX.PT_UX_LATEST")
@IdClass(LatestId.class)
public class Latest implements Serializable {

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

    @Column(name = "category_gb")
    private String categoryGb;

    @Column(name = "r_date")
    private Date rDate;

    @Column(name = "cat_name")
    private String catName;

}
