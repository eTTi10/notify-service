package com.lguplus.fleta.data.entity;


import com.lguplus.fleta.data.entity.id.LatestCheckId;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "SMARTUX.PT_UX_LATEST")
@IdClass(LatestCheckId.class)
public class LatestCheckEntity implements Serializable{
    @Id
    @Column(name = "sa_id")
    private String saId;

    @Id
    @Column(name = "mac")
    private String mac;

    @Id
    @Column(name = "ctn")
    private String ctn;

    @Column(name = "cat_id")
    private String catId;

}
