package com.zslin.business.tools;

import com.zslin.business.dao.IAppModuleDao;
import com.zslin.business.dao.ICarouselDao;
import com.zslin.business.model.AppModule;
import com.zslin.business.model.Carousel;
import com.zslin.core.repository.SimpleSortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CarouselTools {

    @Autowired
    private ICarouselDao carouselDao;

    public void buildOrderNo() {
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
        List<Carousel> root = carouselDao.findAll(sort);
        Integer index = 1;
        for(Carousel r : root) {
            carouselDao.updateOrderNo(index++, r.getId());
        }
    }
}
