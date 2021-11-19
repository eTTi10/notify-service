package com.lguplus.fleta.provider.jpa.send;

import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.entity.RegistrationIdEntity;
import com.lguplus.fleta.repository.PushRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * REGID 조회 용
 * TODO Feign으로 변경되어 삭제예정
 *
 * */

@Slf4j
@Repository
public class PushJpaRepository implements PushRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public RegistrationIdEntity getRegistrationID(SendPushCodeRequestDto sendPushCodeRequestDto) {


        String sql = "SELECT \n" +
                "\t\t       REG_ID\n" +
                "\t\tFROM   SMARTUX.PT_UX_PAIRING\n" +
                "\t\tWHERE  SA_ID = :saId \n" +
                "\t\tAND  STB_MAC = :stbMac \n" +
                "\t\tLIMIT 1";

       return (RegistrationIdEntity) em.createNativeQuery(sql, RegistrationIdEntity.class)
               .setParameter("saId", sendPushCodeRequestDto.getSaId())
               .setParameter("stbMac", sendPushCodeRequestDto.getStbMac())
               .getSingleResult();

    }
}
