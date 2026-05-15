package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 書影取得サービス
 */
@Service
@Slf4j
public class BookCoverService {

    /**
     * openBD API URL
     */
    private static final String OPENBD_API_URL = "https://api.openbd.jp/v1/get?isbn=";

    /**
     * 書影なしを表すキャッシュ値
     */
    private static final String MISSING_COVER = "";

    /**
     * HTTPクライアント
     */
    private final HttpClient httpClient;

    /**
     * JSONマッパー
     */
    private final JsonMapper jsonMapper;

    /**
     * ISBNごとの書影URLキャッシュ
     */
    private final ConcurrentMap<String, String> coverCache = new ConcurrentHashMap<>();

    /**
     * コンストラクタ
     * @param jsonMapper JSONマッパー
     */
    @Autowired
    public BookCoverService(JsonMapper jsonMapper) {
        this(HttpClient.newHttpClient(), jsonMapper);
    }

    BookCoverService(HttpClient httpClient, JsonMapper jsonMapper) {
        this.httpClient = httpClient;
        this.jsonMapper = jsonMapper;
    }

    /**
     * ISBNに対応する書影URLを取得
     * @param isbnList ISBNリスト
     * @return ISBNと書影URLのマップ
     */
    public Map<String, String> getCoverUrls(List<String> isbnList) {
        List<String> normalizedIsbns = isbnList.stream()
                .map(String::trim)
                .filter(isbn -> !isbn.isBlank())
                .distinct()
                .toList();

        List<String> missingIsbns = normalizedIsbns.stream()
                .filter(isbn -> !coverCache.containsKey(isbn))
                .toList();
        if (!missingIsbns.isEmpty()) {
            fetchAndCacheCovers(missingIsbns);
        }

        Map<String, String> coverUrls = new LinkedHashMap<>();
        normalizedIsbns.forEach(isbn -> {
            String coverUrl = coverCache.get(isbn);
            if (coverUrl != null && !coverUrl.isBlank()) {
                coverUrls.put(isbn, coverUrl);
            }
        });
        return coverUrls;
    }

    private void fetchAndCacheCovers(List<String> isbnList) {
        try {
            HttpRequest request = HttpRequest.newBuilder(buildOpenBdUri(isbnList))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IOException("openBD returned status " + response.statusCode());
            }

            JsonNode root = jsonMapper.readTree(response.body());
            for (int index = 0; index < isbnList.size(); index++) {
                String isbn = isbnList.get(index);
                coverCache.put(isbn, readCoverUrl(root, index));
            }
        } catch (IOException e) {
            log.warn("openBDから書影を取得できませんでした。", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("openBDからの書影取得が中断されました。", e);
        }
    }

    private URI buildOpenBdUri(List<String> isbnList) {
        String joinedIsbns = isbnList.stream()
                .map(isbn -> URLEncoder.encode(isbn, StandardCharsets.UTF_8))
                .reduce((left, right) -> left + "," + right)
                .orElse("");
        return URI.create(OPENBD_API_URL + joinedIsbns);
    }

    private String readCoverUrl(JsonNode root, int index) {
        if (!root.isArray() || root.size() <= index || root.get(index).isNull()) {
            return MISSING_COVER;
        }

        JsonNode cover = root.get(index).path("summary").path("cover");
        if (cover.isMissingNode() || cover.isNull() || cover.asString().isBlank()) {
            return MISSING_COVER;
        }
        return cover.asString();
    }
}
