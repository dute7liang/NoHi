package com.nohi.web.controller.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nohi.common.core.domain.R;
import com.nohi.system.domain.vo.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.nohi.common.constant.Constants;
import com.nohi.common.core.domain.entity.SysMenu;
import com.nohi.common.core.domain.entity.SysUser;
import com.nohi.common.core.domain.model.LoginBody;
import com.nohi.common.utils.SecurityUtils;
import com.nohi.framework.web.service.SysLoginService;
import com.nohi.framework.web.service.SysPermissionService;
import com.nohi.system.service.ISysMenuService;

/**
 * 登录验证
 *
 * @author nohi
 */
@RestController
public class SysLoginController {
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public R<Map<String,Object>> login(@RequestBody LoginBody loginBody) {
        Map<String,Object> data = new HashMap<>();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        data.put(Constants.TOKEN, token);
        return R.ok(data);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public R<Map<String,Object>> getInfo() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        Map<String,Object> data = new HashMap<>();
        data.put("user", user);
        data.put("roles", roles);
        data.put("permissions", permissions);
        return R.ok(data);
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public R<List<RouterVo>> getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return R.ok(menuService.buildMenus(menus));
    }
}
