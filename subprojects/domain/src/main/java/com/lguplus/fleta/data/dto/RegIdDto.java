package com.lguplus.fleta.data.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@SuperBuilder
public class RegIdDto implements Serializable {

    private String registrationId;
}
