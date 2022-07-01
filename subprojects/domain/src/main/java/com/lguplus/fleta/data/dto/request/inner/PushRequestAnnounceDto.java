package com.lguplus.fleta.data.dto.request.inner;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


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
