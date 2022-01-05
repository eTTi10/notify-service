package com.lguplus.fleta.data.dto.request.inner;

import lombok.*;

import java.util.List;


@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PushRequestItemDto {

    private String itemKey;
    private String itemValue;

}
