package com.zslin.business.tools;

import com.zslin.business.dao.IProductCategoryDao;
import com.zslin.business.dao.IProductDao;
import com.zslin.business.dto.CategoryProductDto;
import com.zslin.business.dto.CategorySelectDto;
import com.zslin.business.dto.CategoryTreeDto;
import com.zslin.business.model.Product;
import com.zslin.business.model.ProductCategory;
import com.zslin.core.repository.SimpleSortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CategoryTools {

    @Autowired
    private IProductCategoryDao categoryDao;

    @Autowired
    private IProductDao productDao;

    public void buildCategoryOrderNo() {
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
        List<ProductCategory> root = categoryDao.findRoot(sort);
        Integer index = 1;
        for(ProductCategory r : root) {
            categoryDao.updateOrderNo(index++, r.getId());
            buildCategoryOrderNo(r.getId(), sort);
        }
    }

    private void buildCategoryOrderNo(Integer pid, Sort sort) {
        List<ProductCategory> list = categoryDao.findByPid(pid, sort);
        if(list!=null && list.size()>0) {
            int index = 1;
            for(ProductCategory m : list) {
                categoryDao.updateOrderNo(index++, m.getId());
                buildCategoryOrderNo(m.getId(), sort);
            }
        }
    }

    /**
     * 生成分类数据的树结构数据
     * @return
     */
    public List<CategoryTreeDto> buildTree() {
        List<CategoryTreeDto> result = new ArrayList<>();
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
        List<ProductCategory> rootList = categoryDao.findRoot(sort);
        for(ProductCategory m : rootList) {
            List<ProductCategory> children = categoryDao.findByPid(m.getId(), sort);
            List<CategoryProductDto> cpList = new ArrayList<>();
            for(ProductCategory c : children) {
                List<Product> proList = productDao.findByCateId(c.getId());
                cpList.add(new CategoryProductDto(c, proList));
            }
            result.add(new CategoryTreeDto(m, cpList));
        }
        return result;
    }

    /**
     * 生成分类数据的Select结构数据
     * @return
     */
    public List<CategorySelectDto> buildSelect(boolean mustIncludeSub) {
        List<CategorySelectDto> result = new ArrayList<>();
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
        List<ProductCategory> rootList = categoryDao.findRoot(sort);
        for(ProductCategory pc : rootList) {
            List<CategorySelectDto> subList = buildSun(pc.getId());
            if(mustIncludeSub && subList.size()<=0) {continue;}
            result.add(new CategorySelectDto(pc.getName(), pc.getId(), buildSun(pc.getId())));
        }
        return result;
    }

    private List<CategorySelectDto> buildSun(Integer pid) {
        List<CategorySelectDto> result = new ArrayList<>();
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
        List<ProductCategory> children = categoryDao.findByPid(pid, sort);
        for(ProductCategory pc : children) {
            result.add(new CategorySelectDto(pc.getName(), pc.getId()));
        }
        return result;
    }
}
