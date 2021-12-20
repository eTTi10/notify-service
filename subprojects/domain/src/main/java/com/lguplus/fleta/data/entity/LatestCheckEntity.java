package com.lguplus.fleta.data.entity;


import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import com.lguplus.fleta.data.entity.id.LatestCheckId;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
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


    public String toPlainText() {
        return String.join(CommonResponseDto.Separator.COLUMN
                , getSaId(), getMac(), getCtn()
                , getCatId());
    }
}
