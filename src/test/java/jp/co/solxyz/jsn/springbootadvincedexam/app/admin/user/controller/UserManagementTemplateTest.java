package jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.controller;

import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.model.UserManagementModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.service.UserManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
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

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/management/user"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admin/user-management"))
                .andExpect(MockMvcResultMatchers.content().string(containsString("user-management-main")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("ユーザを追加")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("aria-label=\"ユーザ検索\"")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("aria-label=\"権限フィルター\"")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("trapUserModalFocus")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("event.key !== \"Tab\"")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("userModalTrigger.focus({preventScroll: true})")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("手動でコピーしてください")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("document.execCommand(\"copy\")")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("textarea.parentNode")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("admin@solxyz.co.jp")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("一般ユーザ")));
    }
}
