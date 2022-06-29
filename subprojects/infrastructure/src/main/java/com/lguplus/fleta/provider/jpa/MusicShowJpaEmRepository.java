package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.data.dto.request.outer.GetPushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MusicShowJpaEmRepository extends AbstractJpaEmRepository{

    @PersistenceContext
    private final EntityManager entityManager;

    public GetPushDto getPush(GetPushRequestDto requestDto){
        String sql = "SELECT\n"
            + "ALBUM_ID,\n"
            + "PUSH_YN,\n"
            + "RESULT_CODE,\n"
            + "DECODE(PUSH_YN,'Y',TO_CHAR(SEND_DT,'YYYYMMDDHH24MI'),'')  as START_DT\n"
            + "FROM smartux.pt_cm_push_target\n"
            + "   WHERE SA_ID        = :sa_id\n"
            + "     AND STB_MAC      = :stb_mac\n"
            + "     AND SERVICE_TYPE = :service_type\n"
            + "     AND ALBUM_ID     = :album_id\n"
            + "     AND P_KEY        = MOD(TO_NUMBER(TO_CHAR(SEND_DT,'MM')),4)";

            Query query = this.entityManager.createNativeQuery(sql)
                .setParameter("sa_id", requestDto.getSaId())
                .setParameter("stb_mac", requestDto.getStbMac())
                .setParameter("service_type", requestDto.getServiceType())
                .setParameter("album_id", requestDto.getAlbumId());
            return super.convertSingle(query, GetPushDto.class).orElse(null);  };


    public GetPushDto getPush1(GetPushRequestDto requestDto){
        String sql = "SELECT\n"
            + "    album_id,\n"
            + "    push_yn,\n"
            + "    result_code,\n"
            + "    DECODE(push_yn,\n"
            + "           'Y',\n"
            + "           TO_CHAR(send_dt,\n"
            + "                   'YYYYMMDDHH24MI'),\n"
            + "           '')  as start_dt\n"
            + "FROM\n"
            + "    smartux.pt_cm_push_target\n"
            + "WHERE stb_mac = 'v010.0049.4369'\n"
            + "AND album_id = 'M01198F435PPV00'";

        Query query = this.entityManager.createNativeQuery(sql);
        return super.convertSingle(query, GetPushDto.class).orElse(null);
    };

}
