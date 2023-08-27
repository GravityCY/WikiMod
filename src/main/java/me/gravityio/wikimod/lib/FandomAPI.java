package me.gravityio.wikimod.lib;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FandomAPI {
    private static final Gson gson = new Gson();
    private static final HttpClient client = HttpClient.newBuilder()
            .version(java.net.http.HttpClient.Version.HTTP_2)
            .build();
    private static final String FIND_PAGE = "action=query&list=search&srsearch=%s&srlimit=1&format=json";

    public static void getMatchingPage(String url, String title, Consumer<String> consumer) {
        var reqUrl = String.format("%s/api.php?%s", url, FIND_PAGE.formatted(title));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(reqUrl))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(s -> {
                    var res = gson.fromJson(s, Map.class);
                    var top = (String) ((Map)((List)((Map)res.get("query")).get("search")).get(0)).get("title");
                    consumer.accept("%s/wiki/%s".formatted(url, top));
                });
    }
}
