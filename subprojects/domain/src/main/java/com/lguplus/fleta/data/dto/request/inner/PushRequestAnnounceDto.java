package com.lguplus.fleta.data.dto.request.inner;

import lombok.*;

import java.util.List;


@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PushRequestAnnounceDto {

    private String applicationId;
    private String serviceId;
    private String pushType;
    private String message;
    private List<PushRequestItemDto> items;

    public void setItems(List<PushRequestItemDto> items) {
        this.items = items;
    }

}
