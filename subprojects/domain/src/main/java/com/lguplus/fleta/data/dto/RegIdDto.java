package com.lguplus.fleta.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@SuperBuilder
@NoArgsConstructor
public class RegIdDto implements Serializable {

    private String regId;
}
