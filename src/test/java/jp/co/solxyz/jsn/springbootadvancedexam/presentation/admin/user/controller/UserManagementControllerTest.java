package jp.co.solxyz.jsn.springbootadvancedexam.presentation.admin.user.controller;

import jp.co.solxyz.jsn.springbootadvancedexam.application.user.management.UserManagementUseCase;
import jp.co.solxyz.jsn.springbootadvancedexam.application.user.management.UserSummary;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.admin.user.model.UserManagementModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class UserManagementControllerTest {

    @InjectMocks
    UserManagementController controller;

    @Mock
    UserManagementUseCase service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("ユーザー管理画面が正常に表示される")
    void shouldDisplayUserManagementPageSuccessfully() {
        UserSummary user1 = new UserSummary();
        user1.setUserId("user1");
        user1.setUserName("ユーザ1");
        user1.setEmail("user1@example.com");
        user1.setAdmin(false);

        UserSummary user2 = new UserSummary();
        user2.setUserId("user2");
        user2.setUserName("ユーザ2");
        user2.setEmail("user2@example.com");
        user2.setAdmin(true);

        List<UserSummary> users = List.of(user1, user2);
        List<UserManagementModel> expectedUsers = List.of(
                new UserManagementModel("user1", "ユーザ1", "user1@example.com", false),
                new UserManagementModel("user2", "ユーザ2", "user2@example.com", true));

        when(service.getAllUsers()).thenReturn(users);

        ModelAndView mav = controller.index();

        assertThat(mav.getViewName()).isEqualTo("admin/user-management");
        assertThat(mav.getModel().get("activeMenu")).isEqualTo("userManagement");
        assertThat(mav.getModel().get("users")).isEqualTo(expectedUsers);
    }

    @Test
    @DisplayName("ユーザーがいない場合でも、ユーザー管理画面が正常に表示される")
    void shouldDisplayUserManagementPageSuccessfullyEvenIfNoUsers() {
        when(service.getAllUsers()).thenReturn(Collections.emptyList());

        ModelAndView mav = controller.index();

        assertThat(mav.getViewName()).isEqualTo("admin/user-management");
        assertThat(mav.getModel().get("activeMenu")).isEqualTo("userManagement");
        assertThat(mav.getModel().get("users")).isEqualTo(Collections.emptyList());
    }
}
