package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
class BookCoverServiceTest {

    private static final String ISBN = "9784873117904";

    private static final String COVER_URL = "https://cover.openbd.jp/9784873117904.jpg";

    @Mock
    private HttpClient httpClient;

    private BookCoverService bookCoverService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookCoverService = new BookCoverService(httpClient, JsonMapper.builder().build());
    }

    @Test
    @DisplayName("書影URLを取得できた場合、ISBNごとにキャッシュされる")
    void shouldCacheCoverUrlWhenOpenBdReturnsCover() throws Exception {
        HttpResponse<String> response = response(200, "[{\"summary\":{\"cover\":\"" + COVER_URL + "\"}}]");
        when(httpClient.send(any(HttpRequest.class), anyBodyHandler())).thenReturn(response);

        Map<String, String> actual = bookCoverService.getCoverUrls(List.of(" " + ISBN + " ", ISBN));
        Map<String, String> cached = bookCoverService.getCoverUrls(List.of(ISBN));

        assertThat(actual).containsExactly(Map.entry(ISBN, COVER_URL));
        assertThat(cached).containsExactly(Map.entry(ISBN, COVER_URL));
        verify(httpClient, times(1)).send(any(HttpRequest.class), anyBodyHandler());
    }

    @Test
    @DisplayName("openBDの一時的な失敗は書影なしとしてキャッシュしない")
    void shouldNotCacheMissingCoverWhenOpenBdTemporarilyFails() throws Exception {
        HttpResponse<String> failedResponse = response(500, "");
        HttpResponse<String> successResponse = response(200, "[{\"summary\":{\"cover\":\"" + COVER_URL + "\"}}]");
        when(httpClient.send(any(HttpRequest.class), anyBodyHandler())).thenReturn(failedResponse, successResponse);

        Map<String, String> failed = bookCoverService.getCoverUrls(List.of(ISBN));
        Map<String, String> recovered = bookCoverService.getCoverUrls(List.of(ISBN));

        assertThat(failed).isEmpty();
        assertThat(recovered).containsExactly(Map.entry(ISBN, COVER_URL));
        verify(httpClient, times(2)).send(any(HttpRequest.class), anyBodyHandler());
    }

    @Test
    @DisplayName("openBDが書影なしを返した場合、再取得しない")
    void shouldCacheMissingCoverWhenOpenBdReturnsNoCover() throws Exception {
        HttpResponse<String> response = response(200, "[null]");
        when(httpClient.send(any(HttpRequest.class), anyBodyHandler())).thenReturn(response);

        Map<String, String> first = bookCoverService.getCoverUrls(List.of(ISBN));
        Map<String, String> second = bookCoverService.getCoverUrls(List.of(ISBN));

        assertThat(first).isEmpty();
        assertThat(second).isEmpty();
        verify(httpClient, times(1)).send(any(HttpRequest.class), anyBodyHandler());
    }

    @Test
    @DisplayName("openBDへのリクエストが例外になった場合、書影なしとしてキャッシュしない")
    void shouldNotCacheMissingCoverWhenOpenBdRequestThrowsException() throws Exception {
        HttpResponse<String> successResponse = response(200, "[{\"summary\":{\"cover\":\"" + COVER_URL + "\"}}]");
        when(httpClient.send(any(HttpRequest.class), anyBodyHandler()))
                .thenThrow(new IOException())
                .thenReturn(successResponse);

        Map<String, String> failed = bookCoverService.getCoverUrls(List.of(ISBN));
        Map<String, String> recovered = bookCoverService.getCoverUrls(List.of(ISBN));

        assertThat(failed).isEmpty();
        assertThat(recovered).containsExactly(Map.entry(ISBN, COVER_URL));
        verify(httpClient, times(2)).send(any(HttpRequest.class), anyBodyHandler());
    }

    private HttpResponse<String> response(int statusCode, String body) {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn(body);
        return response;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private HttpResponse.BodyHandler<String> anyBodyHandler() {
        return any(HttpResponse.BodyHandler.class);
    }
}
