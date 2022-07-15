package com.lguplus.fleta;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.swagger2markup.GroupBy;
import io.github.swagger2markup.Language;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {NotifyApplication.class})
@AutoConfigureMockMvc
class SwaggerTest {
  @Autowired
  private MockMvc mockMvc;
  @Test
  public void createSpringfoxSwaggerJson() throws Exception {
    String outputDir = System.getProperty("io.springfox.staticdocs.outputDir");
    MvcResult mvcResult = this.mockMvc.perform(get("/v2/api-docs")
            .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
        .andExpect(status().isOk())
        .andReturn();

    MockHttpServletResponse response = mvcResult.getResponse();
//    String swaggerJson = new String(response.getContentAsString().getBytes("ISO_8859_1"), StandardCharsets.UTF_8);
    Files.createDirectories(Paths.get(outputDir));
    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputDir, "swagger.json"), StandardCharsets.UTF_8)){
      writer.write(response.getContentAsString());
    }
  }

  @Test
  public void convertSwaggerToAsciiDoc() throws Exception {
    String outputDir = System.getProperty("io.springfox.staticdocs.outputDir");
    Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
        .withMarkupLanguage(MarkupLanguage.ASCIIDOC)
        .withOutputLanguage(Language.EN)
        .withPathsGroupedBy(GroupBy.TAGS)
        .withGeneratedExamples()
        .withoutInlineSchema()
        .build();

    // 개행 처리 위해 중간에 캐치
    Swagger2MarkupConverter swagger2MarkupConverter = Swagger2MarkupConverter.from(Path.of(outputDir, "/swagger.json").toUri())
        .withConfig(config)
        .build();

    // <br> 태그를 adoc에 맞는 줄바꿈 형태로 교환
    String result = swagger2MarkupConverter.toString().replaceAll("<br>", " + \n");
    // 파일로 저장
    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputDir, "swagger.adoc"), StandardCharsets.UTF_8)){
      writer.write(result);
    }
  }
}