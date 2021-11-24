package com.lguplus.fleta.provider.jpa.push;

import com.lguplus.fleta.config.FreeMarkerTemplate;
import com.lguplus.fleta.data.dto.request.SmartStartItemRequestDto;
import com.lguplus.fleta.data.entity.SmartStartItemInfoEntity;
import com.lguplus.fleta.repository.search.SmartStartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SmartStartItemJpaRepository implements SmartStartItemRepository {

    @PersistenceContext
    private EntityManager em;

    private final FreeMarkerTemplate freeMakerTemplate;
    private final String sqlTemplateFile = "pt_ux_youtube.ftl";

    @Override
    public List<SmartStartItemInfoEntity> getSmartStartItemList(final SmartStartItemRequestDto request) {

        Map<String,Object> params = new HashMap<>();
        params.put("sql_id", "select_pt_ux_panel_title");
        params.put("pannel_id", "SP01");
        //params.put("category_gb", "I30");

        String jsql2 = freeMakerTemplate.getSqlStatement(sqlTemplateFile, params);

        List<SmartStartItemInfoEntity> list = em.createNativeQuery(jsql2, SmartStartItemInfoEntity.class)
                .setParameter("pannel_id", "SP01")
               // .setParameter("category_gb", "I30")
                .getResultList();

        log.debug("count => " + list.size());

        return list;
    }

    public List<SmartStartItemInfoEntity> getSmartStartItemListAsIs(final SmartStartItemRequestDto request) {

        String jpql = "/* SVC.Programming.SmartStartItemJpaRepository.getSmartStartItemList.01 */ \n" +
                "select 			\n"+
                "	pannel_id ,		\n"+
                "	title_id ,		\n"+
                "	category_gb,	\n"+
                "	category_type, 	\n"+
                "	title_nm,		\n"+
                "	category_id,	\n"+
                "	ui_type,		\n"+
                "	bg_img_file,	\n"+
                "	description,	\n"+
                "	ordered      	\n"+
                "from smartux.pt_ux_panel_title	\n"+
                "where	1=1			\n";

        if(!"".equals(request.getConfig_pannel_code())) {
            jpql += "   and pannel_id = :pannel_id \n";
        }

        jpql += "order by ordered asc";

        Query qry = em.createNativeQuery(jpql, SmartStartItemInfoEntity.class);
        if(!"".equals(request.getConfig_pannel_code())) {
            qry.setParameter("pannel_id", request.getConfig_pannel_code());
        }
        return (List<SmartStartItemInfoEntity>) qry.getResultList();
    }
}
