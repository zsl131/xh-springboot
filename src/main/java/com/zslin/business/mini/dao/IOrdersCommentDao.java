package com.zslin.business.mini.dao;

import com.zslin.business.mini.model.OrdersComment;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-04-14.
 */
public interface IOrdersCommentDao extends BaseRepository<OrdersComment, Integer>, JpaSpecificationExecutor<OrdersComment> {

}
