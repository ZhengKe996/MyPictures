package fun.timu.init.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fun.timu.init.model.entity.PostThumb;
import fun.timu.init.model.entity.User;

public interface PostThumbService extends IService<PostThumb> {
    /**
     * 点赞
     *
     * @param postId
     * @param loginUser
     * @return
     */
    int doPostThumb(long postId, User loginUser);

    /**
     * 帖子点赞（内部服务）
     *
     * @param userId
     * @param postId
     * @return
     */
    int doPostThumbInner(long userId, long postId);
}
