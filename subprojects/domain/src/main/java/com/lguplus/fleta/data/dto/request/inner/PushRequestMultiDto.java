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
public class PushRequestMultiDto {

    private String applicationId;
    private String serviceId;
    private String pushType;
    private String message;
    private List<PushRequestItemDto> items;
    private List<String> users;
    private Integer retryCount;

    public void setItems(List<PushRequestItemDto> items) {
        this.items = items;
    }

}
