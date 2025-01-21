package fun.timu.init.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.timu.init.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhengke
 * @description 针对表【user(用户)】的数据库操作Mapper
 * @createDate 2025-01-21 10:42:08
 * @Entity generator.domain.User
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




