package com.zslin.core.tools;

import com.zslin.core.dao.IAdminMenuDao;
import com.zslin.core.model.AdminMenu;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.login.MenuTreeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsl on 2018/7/15.
 */
@Component
public class MenuTools {

    @Autowired
    private IAdminMenuDao menuDao;

    public List<MenuTreeDto> buildMenuTree() {
        List<MenuTreeDto> result = new ArrayList<>();
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
        List<AdminMenu> rootList = menuDao.findRootMenu(sort);
        for(AdminMenu m : rootList) {
            List<AdminMenu> children = menuDao.findByParent(m.getId(), sort);
            result.add(new MenuTreeDto(m, children));
        }
        return result;
    }
}
