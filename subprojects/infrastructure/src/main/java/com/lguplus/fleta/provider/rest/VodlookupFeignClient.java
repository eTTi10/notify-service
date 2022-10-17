package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.data.dto.AlbumProgrammingDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "vodlookup", url = "${service.vodlookup.url}")
public interface VodlookupFeignClient {

    @GetMapping(value = "/vodlookup/albums/programming", produces = "application/json", consumes = "application/json")
    InnerResponseDto<List<AlbumProgrammingDto>> getAlbumProgramming(@RequestParam("categoryType1") String categoryType1, @RequestParam("albumId") List<String> albumId);
}
