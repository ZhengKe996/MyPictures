package fun.timu.init.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.timu.init.model.entity.PostThumb;
import org.apache.ibatis.annotations.Mapper;

/**
 * 点赞数据库操作
 */
@Mapper
public interface PostThumbMapper extends BaseMapper<PostThumb> {
}
