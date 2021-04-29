package com.zslin.business.tools;

import com.zslin.business.dao.IProductTagDao;
import com.zslin.business.model.ProductTag;
import com.zslin.core.repository.SimpleSortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductTagTools {

    @Autowired
    private IProductTagDao productTagDao;

    public void buildOrderNo() {
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
        List<ProductTag> root = productTagDao.findAll(sort);
        Integer index = 1;
        for(ProductTag r : root) {
            productTagDao.updateOrderNo(index++, r.getId());
        }
    }
}
