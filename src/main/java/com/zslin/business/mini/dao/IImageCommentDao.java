package com.zslin.business.mini.dao;

import com.zslin.business.mini.model.ImageComment;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-03-25.
 */
public interface IImageCommentDao extends BaseRepository<ImageComment, Integer>, JpaSpecificationExecutor<ImageComment> {

}
