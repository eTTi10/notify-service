package com.lguplus.fleta.data.entity;


import com.lguplus.fleta.data.entity.id.LatestId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pt_ux_latest")
@IdClass(LatestId.class)
public class LatestEntity implements Serializable {
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
    private String rDate;

    @Column(name = "cat_name")
    private String catName;
}
