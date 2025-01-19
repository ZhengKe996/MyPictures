package fun.timu.init.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.timu.init.common.ErrorCode;
import fun.timu.init.exception.BusinessException;
import fun.timu.init.mapper.PostThumbMapper;
import fun.timu.init.model.entity.Post;
import fun.timu.init.model.entity.PostThumb;
import fun.timu.init.model.entity.User;
import fun.timu.init.service.PostService;
import fun.timu.init.service.PostThumbService;
import javax.annotation.Resource;

import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 点赞服务实现
 */
@Service
public class PostThumbServiceImpl extends ServiceImpl<PostThumbMapper, PostThumb> implements PostThumbService {
    @Resource
    private PostService postService;
    private Lock lock = new ReentrantLock(); // 锁

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doPostThumbInner(long userId, long postId) {
        PostThumb postThumb = new PostThumb();
        postThumb.setUserId(userId);
        postThumb.setPostId(postId);
        QueryWrapper<PostThumb> thumbQueryWrapper = new QueryWrapper<>(postThumb);
        PostThumb oldPostThumb = this.getOne(thumbQueryWrapper);
        boolean result;

        // 已点赞
        if (oldPostThumb != null) {
            result = this.remove(thumbQueryWrapper);
            if (result) {
                // 点赞数 - 1
                result = postService.update().eq("id", postId).gt("thumbNum", 0).setSql("thumbNum = thumbNum - 1").update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 未点赞
            result = this.save(postThumb);
            if (result) {
                // 点赞数 + 1
                result = postService.update().eq("id", postId).setSql("thumbNum = thumbNum + 1").update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }

    /**
     * 点赞
     *
     * @param postId
     * @param loginUser
     * @return
     */
    @Override
    public int doPostThumb(long postId, User loginUser) {

        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已点赞
        long userId = loginUser.getId();
        // 每个用户串行点赞
        PostThumbService postThumbService = (PostThumbService) AopContext.currentProxy();

        lock.lock();
        try {
            postThumbService.doPostThumbInner(userId, postId);
        } finally {
            lock.unlock();
        }
        return 0;
    }
}
