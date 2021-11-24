package com.lguplus.fleta.config;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class FreeMarkerTemplate {

    private final FreeMakerConfig freeMakerConfig;

    public String getSqlStatement(String ftl, Map<String, Object> params) {
        try {
            Template template = freeMakerConfig.getConfigure().getTemplate(ftl);
            StringWriter sw = new StringWriter();
            template.process(params, sw);
            sw.flush();
            return "\n" + sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        } catch (TemplateException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

}