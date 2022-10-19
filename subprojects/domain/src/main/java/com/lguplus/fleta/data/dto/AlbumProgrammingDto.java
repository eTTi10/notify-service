package com.lguplus.fleta.data.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AlbumProgrammingDto implements Serializable {

    private String albumId;

    private String categoryId;

    private String seriesYn;

    private String seriesNo;

}
