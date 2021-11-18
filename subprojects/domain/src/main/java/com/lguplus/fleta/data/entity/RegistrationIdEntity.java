package com.lguplus.fleta.data.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class RegistrationIdEntity {

    /** Registration ID */
    @Id
    @Column(name = "REG_ID")
    private String regId;

}