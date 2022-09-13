package com.lguplus.fleta.data.entity.id;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class LatestId implements Serializable {

    private String saId;
    private String mac;
    private String ctn;
    private String catId;
}