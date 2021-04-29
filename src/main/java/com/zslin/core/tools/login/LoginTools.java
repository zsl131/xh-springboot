package com.zslin.core.tools.login;

import com.zslin.core.dao.IAdminMenuDao;
import com.zslin.core.model.AdminMenu;
import com.zslin.core.repository.SimpleSortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsl on 2018/7/13.
 */
@Component
public class LoginTools {

    @Autowired
    private IAdminMenuDao menuDao;

    public LoginDto buildAuthMenus(Integer userId) {
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
        List<AdminMenu> rootMenuList = menuDao.findRootByUser(userId, sort);
        List<MenuDto> navMenuDtoList = new ArrayList<>();
        for(AdminMenu rootMenu : rootMenuList) { //root
            List<AdminMenu> secondMenuList = menuDao.findByUser(userId, rootMenu.getSn(), sort);
            navMenuDtoList.add(new MenuDto(rootMenu, secondMenuList));
        }

        List<AdminMenu> authMenuList = menuDao.findAuthMenuByUser(userId, sort);

        return new LoginDto(navMenuDtoList, authMenuList);
    }
}
