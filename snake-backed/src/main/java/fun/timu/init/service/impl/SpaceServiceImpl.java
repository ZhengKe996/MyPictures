package fun.timu.init.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.timu.init.common.ErrorCode;
import fun.timu.init.config.SpaceLevelConfig;
import fun.timu.init.exception.BusinessException;
import fun.timu.init.exception.ThrowUtils;
import fun.timu.init.mapper.SpaceMapper;
import fun.timu.init.model.dto.space.SpaceAddRequest;
import fun.timu.init.model.dto.space.SpaceQueryRequest;
import fun.timu.init.model.entity.Space;
import fun.timu.init.model.entity.User;
import fun.timu.init.model.enums.SpaceLevelEnum;
import fun.timu.init.model.vo.SpaceVO;
import fun.timu.init.service.SpaceService;
import fun.timu.init.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhengke
 * @description 针对表【space(空间)】的数据库操作Service实现
 * @createDate 2025-02-05 11:41:10
 */
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceService {


    private final TransactionTemplate transactionTemplate;
    private final UserService userService;

    public SpaceServiceImpl(TransactionTemplate transactionTemplate, UserService userService) {
        this.transactionTemplate = transactionTemplate;
        this.userService = userService;
    }


    /**
     * 添加空间的方法
     *
     * @param spaceAddRequest 空间添加请求对象，包含要添加空间的信息
     * @param loginUser       登录用户对象，用于确定操作权限和关联用户ID
     * @return 返回新添加空间的ID，如果失败则返回-1
     */
    @Override
    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        // 权限校验
        if (SpaceLevelEnum.COMMON.getValue() != spaceAddRequest.getSpaceLevel() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建指定级别的空间");
        }

        // 设置默认值
        if (StrUtil.isBlank(spaceAddRequest.getSpaceName())) {
            spaceAddRequest.setSpaceName("默认空间");
        }
        if (spaceAddRequest.getSpaceLevel() == null) {
            spaceAddRequest.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }

        // 在此处将实体类和 DTO 进行转换
        Space space = new Space();
        BeanUtils.copyProperties(spaceAddRequest, space);

        // 填充数据
        this.fillSpaceBySpaceLevel(space);

        // 数据校验
        this.validSpace(space, true);

        // 获取登录用户的ID，并设置到空间对象中
        Long userId = loginUser.getId();
        space.setUserId(userId);

        // 针对用户进行加锁，以确保并发控制
        ReentrantLock lock = new ReentrantLock();
        try {
            lock.lock();
            // 使用事务模板执行添加操作，确保数据一致性
            Long newSpaceId = transactionTemplate.execute(status -> {
                // 检查用户是否已经有私有空间，如果有则抛出异常
                boolean exists = this.lambdaQuery().eq(Space::getUserId, userId).exists();
                ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个用户仅能有一个私有空间");

                // 写入数据库
                boolean result = this.save(space);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

                // 返回新写入的数据 id
                return space.getId();
            });

            // 返回结果是包装类，可以做一些处理
            return Optional.ofNullable(newSpaceId).orElseThrow(() -> new BusinessException(ErrorCode.OPERATION_ERROR, "空间添加失败"));
        } finally {
            lock.unlock();
        }
    }


    /**
     * 验证空间对象的有效性
     *
     * @param space 空间对象，包含空间名称和空间级别
     * @param add   表示是否是添加操作，true为添加，false为修改
     * @throws BusinessException 当参数无效时抛出的异常
     */
    @Override
    public void validSpace(Space space, boolean add) {
        // 检查空间对象是否为null
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);

        // 从对象中取值并立即检查是否为null
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();

        // 检查空间名称是否为空或过长
        if (add && StrUtil.isBlank(spaceName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称不能为空");
        }
        if (StrUtil.isNotBlank(spaceName) && spaceName.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称过长");
        }

        // 检查空间级别是否有效
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);
        if (spaceLevel != null && spaceLevelEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不存在");
        }

        // 添加操作时，检查空间级别是否为空
        if (add && spaceLevel == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不能为空");
        }
    }


    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        return null;
    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        return null;
    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        return null;
    }

    /**
     * 根据空间级别自动填充空间的限额
     * 当空间的最大大小或最大数量未设置时，根据空间级别自动填充这些值
     *
     * @param space 空间对象，其中包含空间级别和其他属性
     */
    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        if (space == null) {
            // 记录日志并返回
            log.warn("The provided space object is null.");
            return;
        }

        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if (spaceLevelEnum == null) {
            // 记录日志并返回
            log.warn("No corresponding SpaceLevelEnum found for space level: " + space.getSpaceLevel());
            return;
        }

        long maxSize = spaceLevelEnum.getMaxSize();
        long maxCount = spaceLevelEnum.getMaxCount();

        // 使用三元运算符简化条件判断
        space.setMaxSize(space.getMaxSize() != null ? space.getMaxSize() : maxSize);
        space.setMaxCount(space.getMaxCount() != null ? space.getMaxCount() : maxCount);
    }


    @Override
    public void checkSpaceAuth(User loginUser, Space space) {

    }
}




