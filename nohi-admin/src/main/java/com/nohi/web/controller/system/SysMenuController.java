package com.nohi.web.controller.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nohi.common.core.domain.R;
import com.nohi.common.core.domain.TreeSelect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nohi.common.annotation.Log;
import com.nohi.common.constant.UserConstants;
import com.nohi.common.core.controller.BaseController;
import com.nohi.common.core.domain.entity.SysMenu;
import com.nohi.common.enums.BusinessType;
import com.nohi.common.utils.StringUtils;
import com.nohi.system.service.ISysMenuService;

/**
 * 菜单信息
 *
 * @author nohi
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController {
    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public R<List<SysMenu>> list(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu, getUserId());
        return R.ok(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public R<SysMenu> getInfo(@PathVariable Long menuId) {
        return R.ok(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public R<List<TreeSelect>> treeselect(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu, getUserId());
        return R.ok(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public R<Map<String,Object>> roleMenuTreeselect(@PathVariable("roleId") Long roleId) {
        List<SysMenu> menus = menuService.selectMenuList(getUserId());
        Map<String,Object> data = new HashMap<>();
        data.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        data.put("menus", menuService.buildMenuTreeSelect(menus));
        return R.ok(data);
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@Validated @RequestBody SysMenu menu) {
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu))) {
            return R.failed("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            return R.failed("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        menu.setCreateBy(getUsername());
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@Validated @RequestBody SysMenu menu) {
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu))) {
            return R.failed("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            return R.failed("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        } else if (menu.getMenuId().equals(menu.getParentId())) {
            return R.failed("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menu.setUpdateBy(getUsername());
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public R remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return R.failed("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return R.failed("菜单已分配,不允许删除");
        }
        return toAjax(menuService.deleteMenuById(menuId));
    }
}
