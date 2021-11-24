<#--첫번째 SQL 주석 -->
<#if sql_id == "select_pt_ux_panel_title">
    /* SVC.Programming.SearchYoutubeJpaRepository.select_pt_ux_panel_title.01 */
    select
        pannel_id ,
        title_id ,
        category_gb,
        category_type,
        title_nm,
        category_id,
        ui_type,
        bg_img_file,
        description,
        ordered
    FROM
        SMARTUX.PT_UX_PANEL_TITLE
    WHERE	1=1
      <#if pannel_id?has_content>
      AND pannel_id = :pannel_id
      </#if>
      <#if category_gb?has_content>
      and category_gb = :category_gb
      </#if>
    ORDER BY pannel_id , ORDERED asc
</#if>
<#--두번째 SQL 주석 -->
<#if sql_id == "sql2">
    /* SVC.Programming.SearchYoutubeJpaRepository.select_pt_ux_panel_title.02 */
    SELECT
        pannel_id ,
        title_id ,
        category_gb,
        category_type,
        title_nm,
        category_id,
        ui_type,
        bg_img_file,
        description,
        ordered
    FROM SMARTUX.PT_UX_PANEL_TITLE
    WHERE	1=1
    <#if pannel_id?has_content>
        AND pannel_id = :pannel_id
    </#if>
    <#if pannel_id?has_content>
        and category_gb = :category_gb
    </#if>
    ORDER BY pannel_id , ORDERED asc
</#if>