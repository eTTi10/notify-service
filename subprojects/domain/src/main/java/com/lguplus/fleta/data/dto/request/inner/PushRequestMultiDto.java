package com.lguplus.fleta.data.dto.request.inner;

import lombok.*;

import java.util.List;


@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PushRequestMultiDto {

    private String applicationId;
    private String serviceId;
    private String pushType;
    private String message;
    private List<PushRequestItemDto> items;
    private List<String> users;

    public void setItems(List<PushRequestItemDto> items) {
        this.items = items;
    }

}
