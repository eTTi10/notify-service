package com.lguplus.fleta.data.entity.id;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class LatestCheckId implements Serializable {
    private String saId;
    private String mac;
    private String ctn;
    private String catId;
}