package fun.timu.init.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.timu.init.model.entity.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
    /**
     * 查子列表（包括已被删除的数据）
     */
    List<Post> listPostWithDelete(Date minUpdateTime);

}
