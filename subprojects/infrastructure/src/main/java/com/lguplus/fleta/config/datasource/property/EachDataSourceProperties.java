package com.lguplus.fleta.config.datasource.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Setter
@Getter
public class EachDataSourceProperties {

    private String jdbcUrl;
    private String username;
    private String password;
    private Integer minimumIdle = 10;
    private Integer maximumPoolSize = 10;

    public boolean isValid() {
        return StringUtils.hasText(jdbcUrl);
    }

    public void setBasicPropertiesFrom(EachDataSourceProperties otherProp) {
        jdbcUrl = otherProp.getJdbcUrl();
        username = otherProp.getUsername();
        password = otherProp.getPassword();
    }
}
