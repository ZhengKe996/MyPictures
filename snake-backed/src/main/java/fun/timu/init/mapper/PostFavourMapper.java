package fun.timu.init.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.timu.init.model.entity.Post;
import fun.timu.init.model.entity.PostFavour;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 收藏数据库操作
 */
@Mapper
public interface PostFavourMapper extends BaseMapper<PostFavour> {
    /**
     * 分页查询收藏帖子列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<Post> listFavourPostByPage(IPage<Post> page, @Param(Constants.WRAPPER) Wrapper<Post> queryWrapper, long favourUserId);
}
