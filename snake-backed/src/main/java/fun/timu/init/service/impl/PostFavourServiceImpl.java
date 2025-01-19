package fun.timu.init.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.timu.init.common.ErrorCode;
import fun.timu.init.exception.BusinessException;
import fun.timu.init.mapper.PostFavourMapper;
import fun.timu.init.model.entity.Post;
import fun.timu.init.model.entity.PostFavour;
import fun.timu.init.model.entity.User;
import fun.timu.init.service.PostFavourService;
import fun.timu.init.service.PostService;
import javax.annotation.Resource;

import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 收藏服务实现
 */
@Service
public class PostFavourServiceImpl extends ServiceImpl<PostFavourMapper, PostFavour> implements PostFavourService {
    @Resource
    private PostService postService;
    private Lock lock = new ReentrantLock(); // 锁

    /**
     * 收藏
     *
     * @param postId
     * @param loginUser
     * @return
     */
    @Override
    public int doPostFavour(long postId, User loginUser) {
        // 判断是否存在
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已帖子收藏
        long userId = loginUser.getId();
        // 每个用户串行帖子收藏
        PostFavourService postFavourService = (PostFavourService) AopContext.currentProxy();

        lock.lock();
        try {
            return postFavourService.doPostFavourInner(userId, postId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Page<Post> listFavourPostByPage(IPage<Post> page, Wrapper<Post> queryWrapper, long favourUserId) {
        if (favourUserId <= 0) return new Page<>();
        return baseMapper.listFavourPostByPage(page, queryWrapper, favourUserId);
    }

    /**
     * 封装了事务的方法
     *
     * @param userId
     * @param postId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doPostFavourInner(long userId, long postId) {
        PostFavour postFavour = new PostFavour();
        postFavour.setUserId(userId);
        postFavour.setPostId(postId);
        QueryWrapper<PostFavour> postFavourQueryWrapper = new QueryWrapper<>(postFavour);
        PostFavour oldPostFavour = this.getOne(postFavourQueryWrapper);
        boolean result;
        // 已收藏
        if (oldPostFavour != null) {
            result = this.remove(postFavourQueryWrapper);
            if (result) {
                // 帖子收藏数 - 1
                result = postService.update().eq("id", postId).gt("favourNum", 0).setSql("favourNum = favourNum - 1").update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 未收藏
            result = this.save(postFavour);
            if (result) {
                // 收藏数 + 1
                result = postService.update().eq("id", postId).setSql("favourNum = favourNum + 1").update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }
}
