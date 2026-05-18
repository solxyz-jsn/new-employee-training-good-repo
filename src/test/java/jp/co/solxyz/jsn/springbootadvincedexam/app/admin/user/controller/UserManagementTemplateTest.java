package jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.controller;

import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.model.UserManagementModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.service.UserManagementService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
class UserManagementTemplateTest {

    private MockMvc mockMvc;

    @MockitoBean
    private UserManagementService userManagementService;

    private final WebApplicationContext context;

    UserManagementTemplateTest(WebApplicationContext context) {
        this.context = context;
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .defaultRequest(MockMvcRequestBuilders.get("/")
                        .with(user("テスト 太郎").roles("ADMIN")))
                .build();
    }

    @Test
    @DisplayName("ユーザ管理画面テンプレートが管理者向けに表示される")
    void shouldRenderUserManagementTemplateForAdmin() throws Exception {
        UserManagementModel admin = new UserManagementModel(
                "f3d6bcdc-6c32-45b6-9aea-1aa6d36b6b13",
                "管理 次郎",
                "admin@solxyz.co.jp",
                true);
        UserManagementModel user = new UserManagementModel(
                "f3d6bcdc-6c32-45b6-9aea-1aa6d36b6b17",
                "テスト 太郎",
                "test@solxyz.co.jp",
                false);
        when(userManagementService.getAllUsers()).thenReturn(List.of(admin, user));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/admin/management/user"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admin/user-management"))
                .andReturn();

        Document document = Jsoup.parse(result.getResponse().getContentAsString());
        Element userModal = requireElement(document, "#user-modal");
        assertThat(userModal.hasClass("modal"), is(true));
        assertThat(userModal.hasClass("user-modal"), is(true));
        assertThat(userModal.attr("tabindex"), equalTo("-1"));
        assertThat(userModal.attr("aria-hidden"), equalTo("true"));

        Element userDialog = requireElement(document, "#user-modal [role=dialog]");
        assertThat(userDialog.attr("aria-modal"), equalTo("true"));
        assertThat(userDialog.attr("aria-labelledby"), equalTo("user-modal-title"));

        assertThat(requireElement(document, "#userSearch").attr("aria-label"), equalTo("ユーザ検索"));
        assertThat(requireElement(document, "#permissionFilter").attr("aria-label"), equalTo("権限フィルター"));
        requireElement(document, ".user-modal-close[aria-label='閉じる']");
        requireElement(document, ".copy-id-button[aria-label='ユーザIDをコピー']");
        requireElement(document, ".user-management-main");
        assertThat(document.selectFirst(".current-user"), nullValue());

        assertThat(document.text(), containsString("ユーザを追加"));
        assertThat(document.text(), containsString("admin@solxyz.co.jp"));
        assertThat(document.text(), containsString("一般ユーザ"));
    }

    private static Element requireElement(Document document, String cssQuery) {
        Element element = document.selectFirst(cssQuery);
        assertThat(cssQuery, element, notNullValue());
        return element;
    }
}
