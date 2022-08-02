package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushWithPKeyDto;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MusicShowJpaEmRepository extends AbstractJpaEmRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public GetPushDto getPush(PushRequestDto requestDto) {
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

        return super.convertSingle(query, GetPushDto.class).orElse(null);
    }

    public Integer validAlbumId(String albumId) {

        String sql = "SELECT\n"
            + "TO_CHAR(COUNT(m.vod_contents_id)) \n"
            + "FROM voduser.pt_vo_category_map_united m,\n"
            + "     voduser.pt_vo_category_united c \n"
            + "   WHERE c.vod_category_id = m.vod_category_id \n"
            + "     AND c.vod_category_gb = 'NSC' \n"
            + "     AND m.vod_contents_id = :album_id\n";

        Query query = this.entityManager.createNativeQuery(sql)
            .setParameter("album_id", albumId)
            .setMaxResults(1);

        return Integer.parseInt(super.convertSingle(query, String.class).orElse(null));
    }

    public GetPushWithPKeyDto getPushWithPkey(PushRequestDto requestDto) {

        StringBuilder sql = new StringBuilder(
            "SELECT \n"
                + "P_KEY,\n"
                + "REG_NO,\n"
                + "SA_ID,\n"
                + "STB_MAC,\n"
                + "ALBUM_ID,\n"
                + "CATEGORY_ID,\n"
                + "SERVICE_TYPE,\n"
                + "MSG,\n"
                + "PUSH_YN,\n"
                + "RESULT_CODE,\n"
                + "TO_CHAR(SEND_DT,'YYYYMMDDHH24MI') as START_DT,\n"
                + "REG_DT\n"
                + "FROM smartux.pt_cm_push_target\n"
                + "   WHERE SA_ID        = :sa_id\n"
                + "     AND STB_MAC      = :stb_mac\n"
                + "     AND SERVICE_TYPE = :service_type\n"
                + "     AND ALBUM_ID     = :album_id\n"
                + "     AND P_KEY        = MOD(TO_NUMBER(TO_CHAR(SEND_DT,'MM')),4)\n");
        if (StringUtils.isNotBlank(requestDto.getCategoryId())) {
            sql.append(
                "AND CATEGORY_ID  = :category_id "
            );
        }

        Query query = this.entityManager.createNativeQuery(sql.toString())
            .setParameter("sa_id", requestDto.getSaId())
            .setParameter("stb_mac", requestDto.getStbMac())
            .setParameter("service_type", requestDto.getServiceType())
            .setParameter("album_id", requestDto.getAlbumId());
        if (StringUtils.isNotBlank(requestDto.getCategoryId())) {
            query.setParameter("category_id", requestDto.getCategoryId());
        }

        return super.convertSingle(query, GetPushWithPKeyDto.class).orElse(null);

    }

    public Integer getRegNoNextVal() {

        String sql = "SELECT CAST(NEXTVAL('smartux.seq_pt_cm_push_target') AS INTEGER)";

        Query query = this.entityManager.createNativeQuery(sql);

        Integer a = super.convertSingle(query, Integer.class).orElse(null);

        return super.convertSingle(query, Integer.class).orElse(null);
    }
}
