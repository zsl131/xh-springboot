package com.zslin.business.dao;

import com.zslin.business.model.Phonograph;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2019-12-09.
 */
public interface IPhonographDao extends BaseRepository<Phonograph, Integer>, JpaSpecificationExecutor<Phonograph> {

}
