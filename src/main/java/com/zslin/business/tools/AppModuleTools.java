package com.zslin.business.tools;

import com.zslin.business.dao.IAppModuleDao;
import com.zslin.business.model.AppModule;
import com.zslin.core.repository.SimpleSortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppModuleTools {

    @Autowired
    private IAppModuleDao appModuleDao;

    public void buildModuleOrderNo() {
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
        List<AppModule> root = appModuleDao.findAll(sort);
        Integer index = 1;
        for(AppModule r : root) {
            appModuleDao.updateOrderNo(index++, r.getId());
        }
    }
}
