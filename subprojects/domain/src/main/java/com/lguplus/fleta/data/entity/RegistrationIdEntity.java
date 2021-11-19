package com.lguplus.fleta.data.entity;

import lombok.Getter;

import javax.persistence.*;

/**
* REGID 조회 용
* TODO Feign으로 변경되어 삭제예정
*
* */

@Entity
@Getter
public class RegistrationIdEntity {

    /** Registration ID */
    @Id
    @Column(name = "REG_ID")
    private String regId;

}