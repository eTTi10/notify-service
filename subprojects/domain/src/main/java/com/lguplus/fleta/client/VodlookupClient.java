package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.AlbumProgrammingDto;
import java.util.List;

public interface VodlookupClient {

    List<AlbumProgrammingDto> getAlbumProgramming(String categoryType1, List<String> albumId);
}
