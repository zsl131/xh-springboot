package com.zslin.business.dao;

import com.zslin.business.model.ExpressCompany;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-04-12.
 */
public interface IExpressCompanyDao extends BaseRepository<ExpressCompany, Integer>, JpaSpecificationExecutor<ExpressCompany> {

}
