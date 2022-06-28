package com.lguplus.fleta;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriUtils;

class ResponseTest {

    final String devHostname = "localhost:8080";
    final String stpHostname = "mimstb-c.uplus.co.kr";
    final File inPath = new File("C:\\Users\\mwlee\\Documents\\시험자동화_스크립트");
    final String[] inFilenames = {
        //            "IPTV-mims.sendSms.postman_collection.json",
        //            "IPTV-mims.sendMms.postman_collection.json",
        "IPTV-mims.sendPushCode.postman_collection.json"
    };
    final File outPath = new File("C:\\Users\\mwlee\\Documents\\시험자동화_스크립트_수행결과");
    final File devOutPath = new File(outPath, "dev");
    final File stpOutPath = new File(outPath, "stp");
    final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(final String... args) {

        new ResponseTest().start();
    }

    void start() {

        Arrays.stream(inFilenames).forEach(inFilename ->
            getItems(new File(inPath, inFilename)).forEach(item -> {
                try {
                    printReponses(item, getResponse(devHostname, item), new File(devOutPath, inFilename + ".out"),
                        getResponse(stpHostname, item), new File(stpOutPath, inFilename + ".out"));
                } catch (final IOException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(item.name);
                }
            }));
    }

    List<Item> getItems(final File inFile) {

        try {
            return ((List<?>) ((Map<?, ?>) ((List<?>) (objectMapper.readValue(inFile, Map.class)).get("item")).get(0))
                .get("item")).stream()
                .filter(e -> ((Map<?, ?>) e).get("name").toString().equals("/mims/sendPushCode_VTC31_service_type_파라미터 값의 size가 클때"))
                .map(e -> new Item(
                    ((Map<?, ?>) e).get("name").toString(),
                    (String) ((Map<?, ?>) ((Map<?, ?>) e).get("request")).get("method"),
                    ((Map<?, ?>) ((Map<?, ?>) ((Map<?, ?>) e).get("request")).get("url")).get("raw").toString(),
                    (List<Map<String, String>>) ((Map<?, ?>) ((Map<?, ?>) e).get("request")).get("header"),
                    ((Map<?, ?>) ((Map<?, ?>) e).get("request")).get("body") == null ? null :
                        ((Map<?, ?>) ((Map<?, ?>) ((Map<?, ?>) e).get("request")).get("body")).get("raw").toString()))
                .collect(Collectors.toList());
        } catch (final IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    void printReponses(final Item item, final String devResponse, final File devOutFile, final String stpResponse,
        final File stpOutFile) throws IOException {

        try {
            final ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            final String devPrettyResponse = objectWriter.writeValueAsString(
                objectMapper.readValue(devResponse, Map.class));
            final String stpPrettyResponse = objectWriter.writeValueAsString(
                objectMapper.readValue(stpResponse, Map.class));
            printResponse(item, devPrettyResponse, devOutFile);
            printResponse(item, stpPrettyResponse, stpOutFile);
        } catch (final IOException e) {
            printResponse(item, devResponse, devOutFile);
            printResponse(item, stpResponse, stpOutFile);
            throw e;
        }
    }

    void printResponse(final Item item, final String response, final File file) throws IOException {

        final File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        if (!file.exists()) {
            file.createNewFile();
        }

        try (final PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            writer.println(item.name);
            writer.println(response);
        }
    }

    String getResponse(final String hostname, final Item item) throws IOException, ExecutionException, InterruptedException {

        final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(
            URI.create(item.getUrl().replace("{{mims}}", hostname)));
        item.headers.forEach(e -> requestBuilder.header(e.get("key"), e.get("value")));
        if (item.method.equals("GET")) {
            requestBuilder.GET();
        } else if (item.method.equals("POST")) {
            requestBuilder.setHeader("Content-Type", "text/plain");
            requestBuilder.POST(HttpRequest.BodyPublishers.ofString(item.body == null ? "" : item.body));
        } else {
            throw new UnsupportedOperationException(item.method);
        }

        return HttpClient.newHttpClient().sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body).get();
    }

    class Item {

        final String name;
        final String method;
        final String url;
        final List<Map<String, String>> headers;
        final String body;
        Item(final String name, final String method, final String url, List<Map<String, String>> headers, final String body) {

            this.name = name;
            this.method = method;
            this.url = url;
            this.headers = headers;
            this.body = body;
        }

        String getUrl() {

            try {
                final URL url = new URL(this.url);
                return url.getProtocol() + "://" + url.getHost() + (url.getPort() == -1 ? "" : ":" + url.getPort()) +
                    url.getPath().replace(' ', '+') + "?" +
                    StringUtils.defaultString(UriUtils.encodeQuery(url.getQuery(), StandardCharsets.UTF_8));
            } catch (final IOException e) {
                e.printStackTrace();
            }
            return url;
        }
    }
}
