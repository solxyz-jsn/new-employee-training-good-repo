package jp.co.solxyz.jsn.springbootadvancedexam.presentation.admin.user.controller;

import jp.co.solxyz.jsn.springbootadvancedexam.application.user.management.UserManagementUseCase;
import jp.co.solxyz.jsn.springbootadvancedexam.application.user.management.UserSummary;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.admin.user.model.UserManagementModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * ユーザ管理コントローラー
 */
@Controller
@RequestMapping("/admin/management/user")
@RequiredArgsConstructor
@Slf4j
public class UserManagementController {

    /**
     * ユーザ管理ユースケース
     */
    private final UserManagementUseCase userManagementUseCase;

    /**
     * ユーザ管理画面表示
     * @return ユーザ管理画面
     */
    @GetMapping
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("admin/user-management");
        List<UserManagementModel> displayedUsers = userManagementUseCase.getAllUsers().stream()
                .map(this::toModel)
                .toList();

        mav.addObject("users", displayedUsers);
        mav.addObject("activeMenu", "userManagement");
        return mav;
    }

    /**
     * ユーザ概要を画面表示用モデルに変換する
     * @param userSummary ユーザ概要
     * @return 画面表示用ユーザ管理モデル
     */
    private UserManagementModel toModel(UserSummary userSummary) {
        UserManagementModel model = new UserManagementModel();
        model.setUserId(userSummary.getUserId());
        model.setUserName(userSummary.getUserName());
        model.setEmail(userSummary.getEmail());
        model.setAdmin(userSummary.isAdmin());
        return model;
    }
}
