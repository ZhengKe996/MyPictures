package fun.timu.init.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.timu.init.common.ErrorCode;
import fun.timu.init.exception.BusinessException;
import fun.timu.init.exception.ThrowUtils;
import fun.timu.init.mapper.SpaceMapper;
import fun.timu.init.model.dto.analyze.*;
import fun.timu.init.model.entity.Picture;
import fun.timu.init.model.entity.Space;
import fun.timu.init.model.entity.User;
import fun.timu.init.model.vo.analyze.*;
import fun.timu.init.service.PictureService;
import fun.timu.init.service.SpaceAnalyzeService;
import fun.timu.init.service.SpaceService;
import fun.timu.init.service.UserService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SpaceAnalyzeServiceImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceAnalyzeService {

    private final UserService userService;

    private final SpaceService spaceService;

    private final PictureService pictureService;

    public SpaceAnalyzeServiceImpl(UserService userService, SpaceService spaceService, PictureService pictureService) {
        this.userService = userService;
        this.spaceService = spaceService;
        this.pictureService = pictureService;
    }

    /**
     * 获取空间使用分析数据
     *
     * @param spaceUsageAnalyzeRequest 请求参数，包含查询空间使用分析的条件
     * @param loginUser                当前登录用户，用于权限验证
     * @return SpaceUsageAnalyzeResponse 返回空间使用分析结果
     */
    @Override
    public SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser) {
        // 参数校验，确保请求参数不为空
        ThrowUtils.throwIf(spaceUsageAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 判断是否查询全部或公共图库
        if (spaceUsageAnalyzeRequest.isQueryAll() || spaceUsageAnalyzeRequest.isQueryPublic()) {
            // 仅管理员可以访问
            boolean isAdmin = userService.isAdmin(loginUser);
            ThrowUtils.throwIf(!isAdmin, ErrorCode.NO_AUTH_ERROR, "无权访问空间");

            // 统计公共图库的资源使用
            QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("picSize");
            if (!spaceUsageAnalyzeRequest.isQueryAll()) {
                queryWrapper.isNull("spaceId");
            }
            List<Object> pictureObjList = pictureService.getBaseMapper().selectObjs(queryWrapper);
            long usedSize = pictureObjList.stream().mapToLong(result -> result instanceof Long ? (Long) result : 0).sum();
            long usedCount = pictureObjList.size();

            // 封装返回结果
            SpaceUsageAnalyzeResponse spaceUsageAnalyzeResponse = new SpaceUsageAnalyzeResponse();
            spaceUsageAnalyzeResponse.setUsedSize(usedSize);
            spaceUsageAnalyzeResponse.setUsedCount(usedCount);
            // 公共图库无上限、无比例
            spaceUsageAnalyzeResponse.setMaxSize(null);
            spaceUsageAnalyzeResponse.setSizeUsageRatio(null);
            spaceUsageAnalyzeResponse.setMaxCount(null);
            spaceUsageAnalyzeResponse.setCountUsageRatio(null);
            return spaceUsageAnalyzeResponse;
        } else {
            // 查询指定空间
            Long spaceId = spaceUsageAnalyzeRequest.getSpaceId();
            // 参数校验，确保空间ID有效
            ThrowUtils.throwIf(spaceId == null || spaceId <= 0, ErrorCode.PARAMS_ERROR);

            // 获取空间信息
            Space space = spaceService.getById(spaceId);
            // 确保空间存在
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");

            // 权限校验：仅空间所有者或管理员可访问
            spaceService.checkSpaceAuth(loginUser, space);

            // 构造返回结果
            SpaceUsageAnalyzeResponse response = new SpaceUsageAnalyzeResponse();
            response.setUsedSize(space.getTotalSize());
            response.setMaxSize(space.getMaxSize());
            // 后端直接算好百分比，这样前端可以直接展示
            double sizeUsageRatio = NumberUtil.round(space.getTotalSize() * 100.0 / space.getMaxSize(), 2).doubleValue();
            response.setSizeUsageRatio(sizeUsageRatio);
            response.setUsedCount(space.getTotalCount());
            response.setMaxCount(space.getMaxCount());
            double countUsageRatio = NumberUtil.round(space.getTotalCount() * 100.0 / space.getMaxCount(), 2).doubleValue();
            response.setCountUsageRatio(countUsageRatio);
            return response;
        }
    }


    /**
     * 根据空间类别分析请求获取分析响应
     *
     * @param spaceCategoryAnalyzeRequest 空间类别分析请求对象，包含分析参数
     * @param loginUser                   执行分析操作的登录用户对象，用于权限检查
     * @return 返回一个列表，包含每个类别的空间分析响应对象
     * <p>
     * 此方法主要用于根据用户请求分析特定空间类别使用情况，包括图片数量和总大小
     */
    @Override
    public List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser) {
        // 参数非空校验
        ThrowUtils.throwIf(spaceCategoryAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 检查权限
        checkSpaceAnalyzeAuth(spaceCategoryAnalyzeRequest, loginUser);

        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        // 根据分析范围补充查询条件
        fillAnalyzeQueryWrapper(spaceCategoryAnalyzeRequest, queryWrapper);

        // 使用 MyBatis-Plus 分组查询
        queryWrapper.select("category AS category", "COUNT(*) AS count", "SUM(picSize) AS totalSize").groupBy("category");

        // 查询并转换结果
        return pictureService.getBaseMapper().selectMaps(queryWrapper).stream().map(result -> {
            String category = result.get("category") != null ? result.get("category").toString() : "未分类";
            Long count = ((Number) result.get("count")).longValue();
            Long totalSize = ((Number) result.get("totalSize")).longValue();
            return new SpaceCategoryAnalyzeResponse(category, count, totalSize);
        }).collect(Collectors.toList());
    }


    /**
     * 获取空间标签分析数据
     * <p>
     * 根据请求参数和当前用户信息，分析并返回空间中的标签统计信息
     *
     * @param spaceTagAnalyzeRequest 空间标签分析请求对象，包含分析所需的参数
     * @param loginUser              当前登录用户，用于权限检查
     * @return 返回一个列表，包含按使用次数排序的标签分析响应对象
     */
    @Override
    public List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser) {
        // 参数非空校验
        ThrowUtils.throwIf(spaceTagAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 检查权限
        checkSpaceAnalyzeAuth(spaceTagAnalyzeRequest, loginUser);

        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(spaceTagAnalyzeRequest, queryWrapper);

        // 查询所有符合条件的标签
        queryWrapper.select("tags");
        List<String> tagsJsonList = pictureService.getBaseMapper().selectObjs(queryWrapper).stream().filter(ObjUtil::isNotNull).map(Object::toString).collect(Collectors.toList());

        // 合并所有标签并统计使用次数
        Map<String, Long> tagCountMap = tagsJsonList.stream().flatMap(tagsJson -> JSONUtil.toList(tagsJson, String.class).stream()).collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));

        // 转换为响应对象，按使用次数降序排序
        return tagCountMap.entrySet().stream().sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // 降序排列
                .map(entry -> new SpaceTagAnalyzeResponse(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }

    /**
     * 获取空间大小分析数据
     *
     * @param spaceSizeAnalyzeRequest 空间大小分析请求对象，包含分析所需的参数
     * @param loginUser               当前登录用户，用于权限验证
     * @return 返回一个列表，包含各大小范围的照片数量统计
     * <p>
     * 此方法主要用于分析存储空间中照片的大小分布情况，将照片按大小范围进行统计
     */
    @Override
    public List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser) {
        // 参数非空校验
        ThrowUtils.throwIf(spaceSizeAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 检查权限
        checkSpaceAnalyzeAuth(spaceSizeAnalyzeRequest, loginUser);

        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(spaceSizeAnalyzeRequest, queryWrapper);

        // 查询所有符合条件的图片大小
        queryWrapper.select("picSize");
        List<Long> picSizes = pictureService.getBaseMapper().selectObjs(queryWrapper).stream().map(size -> ((Number) size).longValue()).collect(Collectors.toList());

        // 定义分段范围，注意使用有序 Map
        Map<String, Long> sizeRanges = new LinkedHashMap<>();
        sizeRanges.put("<100KB", picSizes.stream().filter(size -> size < 100 * 1024).count());
        sizeRanges.put("100KB-500KB", picSizes.stream().filter(size -> size >= 100 * 1024 && size < 500 * 1024).count());
        sizeRanges.put("500KB-1MB", picSizes.stream().filter(size -> size >= 500 * 1024 && size < 1 * 1024 * 1024).count());
        sizeRanges.put(">1MB", picSizes.stream().filter(size -> size >= 1 * 1024 * 1024).count());

        // 转换为响应对象
        return sizeRanges.entrySet().stream().map(entry -> new SpaceSizeAnalyzeResponse(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }


    /**
     * 获取用户在指定空间内的分析数据
     *
     * @param spaceUserAnalyzeRequest 空间用户分析请求对象，包含分析参数
     * @param loginUser               当前登录用户，用于权限验证
     * @return 用户分析响应列表，包含每个时间段的用户活动计数
     * <p>
     * 此方法首先验证请求参数是否为空，然后检查用户是否有权限进行空间分析
     * 接着，根据请求中的用户ID和其他筛选条件构造查询条件，并根据指定的时间维度（每日、每周、每月）进行数据统计
     * 最后，将查询结果转换为用户分析响应对象列表并返回
     */
    @Override
    public List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser) {
        // 参数非空校验
        ThrowUtils.throwIf(spaceUserAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        // 检查权限
        checkSpaceAnalyzeAuth(spaceUserAnalyzeRequest, loginUser);

        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        Long userId = spaceUserAnalyzeRequest.getUserId();
        queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);
        fillAnalyzeQueryWrapper(spaceUserAnalyzeRequest, queryWrapper);

        // 根据时间维度进行查询和统计
        String timeDimension = spaceUserAnalyzeRequest.getTimeDimension();
        switch (timeDimension) {
            case "day":
                queryWrapper.select("DATE_FORMAT(createTime, '%Y-%m-%d') AS period", "COUNT(*) AS count");
                break;
            case "week":
                queryWrapper.select("YEARWEEK(createTime) AS period", "COUNT(*) AS count");
                break;
            case "month":
                queryWrapper.select("DATE_FORMAT(createTime, '%Y-%m') AS period", "COUNT(*) AS count");
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的时间维度");
        }

        // 分组和排序
        queryWrapper.groupBy("period").orderByAsc("period");

        // 执行查询并转换结果
        List<Map<String, Object>> queryResult = pictureService.getBaseMapper().selectMaps(queryWrapper);
        return queryResult.stream().map(result -> {
            String period = result.get("period").toString();
            Long count = ((Number) result.get("count")).longValue();
            return new SpaceUserAnalyzeResponse(period, count);
        }).collect(Collectors.toList());
    }

    /**
     * 获取空间排行分析数据
     * 该方法用于获取系统中空间使用情况的排行，仅管理员有权限查看
     *
     * @param spaceRankAnalyzeRequest 空间排行分析请求对象，包含获取排行前N名的需求
     * @param loginUser               当前登录的用户，用于权限验证
     * @return 返回一个Space对象列表，代表空间使用情况的排行
     * <p>
     * 验证请求参数非空
     * 仅管理员可查看空间排行
     * 构造查询条件
     * 查询结果
     */
    @Override
    public List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser) {
        // 验证请求参数非空
        ThrowUtils.throwIf(spaceRankAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 仅管理员可查看空间排行
        ThrowUtils.throwIf(!userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR, "无权查看空间排行");

        // 构造查询条件
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        // 选择查询的字段和排序方式，按空间大小降序排列，并限制查询结果数量为前N名
        queryWrapper.select("id", "spaceName", "userId", "totalSize").orderByDesc("totalSize").last("LIMIT " + spaceRankAnalyzeRequest.getTopN());

        // 查询结果
        return spaceService.list(queryWrapper);
    }

    /**
     * 检查空间分析权限
     * <p>
     * 本方法用于检查用户是否有权限执行空间分析请求根据请求的类型（查询所有、查询公共或查询私有空间），
     * 系统会根据当前用户的角色和所请求的空间类型来判断用户是否有执行权限
     *
     * @param spaceAnalyzeRequest 空间分析请求对象，包含分析所需的各种参数
     * @param loginUser           当前登录用户对象，用于权限验证
     */
    private void checkSpaceAnalyzeAuth(SpaceAnalyzeRequest spaceAnalyzeRequest, User loginUser) {
        // 检查权限
        if (spaceAnalyzeRequest.isQueryAll() || spaceAnalyzeRequest.isQueryPublic()) {
            // 全空间分析或者公共图库权限校验：仅管理员可访问
            ThrowUtils.throwIf(!userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR, "无权访问公共图库");
        } else {
            // 私有空间权限校验
            Long spaceId = spaceAnalyzeRequest.getSpaceId();
            // 检查空间ID是否有效
            ThrowUtils.throwIf(spaceId == null || spaceId <= 0, ErrorCode.PARAMS_ERROR);
            Space space = spaceService.getById(spaceId);
            // 确保空间存在
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            // 检查用户是否有访问该私有空间的权限
            spaceService.checkSpaceAuth(loginUser, space);
        }
    }

    /**
     * 根据空间分析请求填充查询包装器
     * 此方法用于根据SpaceAnalyzeRequest对象中的请求参数来构建QueryWrapper对象
     * 它主要根据是否查询全部、是否查询公共空间以及特定空间ID来设置查询条件
     *
     * @param spaceAnalyzeRequest 空间分析请求对象，包含查询偏好
     * @param queryWrapper        查询包装器对象，用于后续的数据库查询
     */
    private static void fillAnalyzeQueryWrapper(SpaceAnalyzeRequest spaceAnalyzeRequest, QueryWrapper<Picture> queryWrapper) {
        // 如果请求查询全部数据，则直接返回，不设置任何查询条件
        if (spaceAnalyzeRequest.isQueryAll()) {
            return;
        }
        // 如果请求查询公共空间数据，则设置spaceId字段为null进行查询
        if (spaceAnalyzeRequest.isQueryPublic()) {
            queryWrapper.isNull("spaceId");
            return;
        }
        // 获取请求中的空间ID
        Long spaceId = spaceAnalyzeRequest.getSpaceId();
        // 如果空间ID不为空，则根据空间ID设置查询条件
        if (spaceId != null) {
            queryWrapper.eq("spaceId", spaceId);
            return;
        }
        // 如果没有指定查询范围，则抛出业务异常，提示参数错误
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "未指定查询范围");
    }


}
