package com.lguplus.fleta.config;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class FreeMakerConfig {
    private final freemarker.template.Configuration configure;

    public FreeMakerConfig() {
        this.configure = new Configuration(Configuration.VERSION_2_3_31);
        ClassTemplateLoader loader = new ClassTemplateLoader(this.getClass(), "/templates");
        this.configure.setTemplateLoader(loader);
        this.configure.setDefaultEncoding("UTF-8");
        this.configure.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }
/*
    private freemarker.template.Configuration configuration() {
        //Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        ClassTemplateLoader loader = new ClassTemplateLoader(this.getClass(), "/templates");
        configuration.setTemplateLoader(loader);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return configuration;
    }
    */

/*
    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer(){
        FreeMarkerConfigurer cfg = new FreeMarkerConfigurer();
        cfg.setTemplateLoaderPath("classpath:/templates"); //defines the classpath location of the freemarker templates
        cfg.setDefaultEncoding("UTF-8"); // Default encoding of the template files

        configuration = new Configuration(Configuration.VERSION_2_3_23);
        ClassTemplateLoader loader = new ClassTemplateLoader(
                new ConfigurationUtil().getClass(), "/ftl");
        configuration.setTemplateLoader(loader);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        return cfg;
    }
*/
}