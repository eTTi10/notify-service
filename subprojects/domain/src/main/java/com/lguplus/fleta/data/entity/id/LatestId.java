package com.lguplus.fleta.data.entity.id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LatestId implements Serializable {
    private String saId;
    private String mac;
    private String ctn;
    private String catId;
}