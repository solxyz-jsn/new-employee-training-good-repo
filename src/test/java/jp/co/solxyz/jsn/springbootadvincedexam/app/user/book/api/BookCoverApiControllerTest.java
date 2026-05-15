package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.api;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service.BookCoverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class BookCoverApiControllerTest {

    @InjectMocks
    private BookCoverApiController bookCoverApiController;

    @Mock
    private BookCoverService bookCoverService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("ISBNが指定された場合、書影URLのマップが返される")
    void shouldReturnCoverUrlsWhenIsbnIsSpecified() {
        List<String> expectedIsbnList = List.of("9784873117904", "9784873119380");
        Map<String, String> coverUrls = Map.of(
                "9784873117904", "https://cover.openbd.jp/9784873117904.jpg",
                "9784873119380", "https://cover.openbd.jp/9784873119380.jpg");
        when(bookCoverService.getCoverUrls(expectedIsbnList)).thenReturn(coverUrls);

        ResponseEntity<Map<String, String>> response =
                bookCoverApiController.getCoverUrls("9784873117904, 9784873119380,9784873117904");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(coverUrls);
        verify(bookCoverService, times(1)).getCoverUrls(expectedIsbnList);
    }

    @Test
    @DisplayName("ISBNが空の場合、BadRequestが返される")
    void shouldReturnBadRequestWhenIsbnIsEmpty() {
        ResponseEntity<Map<String, String>> response = bookCoverApiController.getCoverUrls(" , ");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(bookCoverService);
    }
}
