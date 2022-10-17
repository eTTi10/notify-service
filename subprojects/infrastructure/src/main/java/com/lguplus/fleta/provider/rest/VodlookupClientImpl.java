package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.client.VodlookupClient;
import com.lguplus.fleta.data.dto.AlbumProgrammingDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VodlookupClientImpl extends CommonDomainFeignClient implements VodlookupClient {

    private final VodlookupFeignClient vodlookupFeignClient;

    @Override
    public List<AlbumProgrammingDto> getAlbumProgramming(String categoryType1, List<String> albumId) {
        return getResult(vodlookupFeignClient.getAlbumProgramming(categoryType1, albumId));
    }
}
