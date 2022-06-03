package com.lguplus.fleta.data.vo.musicshow;

import com.lguplus.fleta.validation.AlphabetAndNumberPattern;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
public class GetPushRequestVo {

    @AlphabetAndNumberPattern
    @Length(min = 7, max = 12)
    private final String SA_ID; // 가입자정보

    @AlphabetAndNumberPattern
    @Length(min = 14, max = 14)
    private final String STB_MAC; // 가입자 STB MAC Address

    @AlphabetAndNumberPattern
    @Length(min = 15, max = 15)
    private final String ALBUM_ID; // 앨범 ID

}
