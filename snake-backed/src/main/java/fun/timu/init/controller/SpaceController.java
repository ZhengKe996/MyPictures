package fun.timu.init.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.timu.init.annotation.AuthCheck;
import fun.timu.init.common.BaseResponse;
import fun.timu.init.common.DeleteRequest;
import fun.timu.init.common.ErrorCode;
import fun.timu.init.common.ResultUtils;
import fun.timu.init.constant.UserConstant;
import fun.timu.init.exception.BusinessException;
import fun.timu.init.exception.ThrowUtils;
import fun.timu.init.model.dto.space.*;
import fun.timu.init.model.entity.Space;
import fun.timu.init.model.entity.User;
import fun.timu.init.model.enums.SpaceLevelEnum;
import fun.timu.init.model.vo.SpaceVO;
import fun.timu.init.service.SpaceService;
import fun.timu.init.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/space")
public class SpaceController {
    private static final Logger logger = LoggerFactory.getLogger(SpaceController.class);
    private final SpaceService spaceService;
    private final UserService userService;

    public SpaceController(SpaceService spaceService, UserService userService) {
        this.spaceService = spaceService;
        this.userService = userService;
    }


    /**
     * 处理添加空间的请求
     * <p>
     * 该方法用于接收通过POST请求发送的SpaceAddRequest对象，并将其添加到系统中
     * 它还依赖于用户登录状态，以确保只有登录用户可以添加空间
     *
     * @param spaceAddRequest 包含要添加空间信息的请求体，不能为空
     * @param request         HTTP请求对象，用于获取用户登录信息
     * @return 返回一个BaseResponse对象，其中包含新添加空间的ID
     */
    @PostMapping("/add")
    public BaseResponse<Long> addSpace(@RequestBody SpaceAddRequest spaceAddRequest, HttpServletRequest request) {
        // 检查请求体是否为空，如果为空，则抛出参数错误异常
        ThrowUtils.throwIf(spaceAddRequest == null, ErrorCode.PARAMS_ERROR);

        // 获取当前登录用户信息
        User loginUser = userService.getLoginUser(request);

        // 调用服务层方法添加新空间，并获取新空间的ID
        long newId = spaceService.addSpace(spaceAddRequest, loginUser);

        // 返回成功响应，包含新空间的ID
        return ResultUtils.success(newId);
    }

    /**
     * 处理删除空间的请求
     *
     * @param deleteRequest 包含要删除空间的ID的请求体
     * @param request       HTTP请求对象，用于获取当前登录用户信息
     * @return 返回一个表示操作结果的BaseResponse对象
     * @throws BusinessException 如果参数无效或操作失败，抛出此异常
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        // 验证请求参数是否有效
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 获取要删除的空间的ID
        Long id = deleteRequest.getId();

        // 判断是否存在
        Space oldSpace = spaceService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人或者管理员可删除
        spaceService.checkSpaceAuth(loginUser, oldSpace);

        // 操作数据库
        boolean result = spaceService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 返回成功结果
        return ResultUtils.success(true);
    }

    /**
     * 更新空间信息，此接口仅对管理员角色开放
     *
     * @param spaceUpdateRequest 包含要更新的空间信息的请求对象
     * @param request            HTTP请求对象，用于获取请求相关的信息
     * @return 返回一个布尔值，表示空间信息是否成功更新
     * <p>
     * 此方法首先检查传入的spaceUpdateRequest对象和其ID的有效性，
     * 然后将请求对象转换为实体类对象，并进行数据填充和有效性验证，
     * 最后将更新后的实体类对象写入数据库
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest, HttpServletRequest request) {
        // 检查传入参数的有效性
        if (spaceUpdateRequest == null || spaceUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换
        Space space = new Space();
        BeanUtils.copyProperties(spaceUpdateRequest, space);
        // 自动填充数据
        spaceService.fillSpaceBySpaceLevel(space);
        // 数据校验
        spaceService.validSpace(space, false);
        // 判断是否存在
        long id = spaceUpdateRequest.getId();
        Space oldSpace = spaceService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = spaceService.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取空间（仅管理员可用）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Space> getSpaceById(long id, HttpServletRequest request) {
        // 参数合法性检查
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space space = spaceService.getById(id);
        // 检查空间是否存在
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(space);
    }


    /**
     * 根据 id 获取空间（封装类）
     */
    @GetMapping("/get/vo")
    public BaseResponse<SpaceVO> getSpaceVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        SpaceVO spaceVO = spaceService.getSpaceVO(space, request);
        User loginUser = userService.getLoginUser(request);
//        List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
//        spaceVO.setPermissionList(permissionList);
        // 获取封装类
        return ResultUtils.success(spaceVO);
    }

    /**
     * 分页获取空间列表（仅管理员可用）
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Space>> listSpaceByPage(@RequestBody SpaceQueryRequest spaceQueryRequest) {
        // 获取当前页码和页面大小
        long current = spaceQueryRequest.getCurrent();
        long size = spaceQueryRequest.getPageSize();
        // 查询数据库
        Page<Space> spacePage = spaceService.page(new Page<>(current, size), spaceService.getQueryWrapper(spaceQueryRequest));
        // 返回查询结果
        return ResultUtils.success(spacePage);
    }

    /**
     * 分页获取空间列表（封装类）
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SpaceVO>> listSpaceVOByPage(@RequestBody SpaceQueryRequest spaceQueryRequest, HttpServletRequest request) {
        // 获取当前页码和页面大小
        long current = spaceQueryRequest.getCurrent();
        long size = spaceQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Space> spacePage = spaceService.page(new Page<>(current, size), spaceService.getQueryWrapper(spaceQueryRequest));
        // 获取封装类
        return ResultUtils.success(spaceService.getSpaceVOPage(spacePage, request));
    }

    /**
     * 编辑空间（给用户使用）
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest, HttpServletRequest request) {
        if (spaceEditRequest == null || spaceEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        Space space = new Space();
        BeanUtils.copyProperties(spaceEditRequest, space);
        // 自动填充数据
        spaceService.fillSpaceBySpaceLevel(space);
        // 设置编辑时间
        space.setEditTime(new Date());
        // 数据校验
        spaceService.validSpace(space, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = spaceEditRequest.getId();
        Space oldSpace = spaceService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        spaceService.checkSpaceAuth(loginUser, oldSpace);
        // 操作数据库
        boolean result = spaceService.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 获取空间级别列表，便于前端展示
     *
     * @return
     */
    @GetMapping("/list/level")
    public BaseResponse<List<SpaceLevel>> listSpaceLevel() {
        List<SpaceLevel> spaceLevelList = Arrays.stream(SpaceLevelEnum.values()).map(spaceLevelEnum -> new SpaceLevel(spaceLevelEnum.getValue(), spaceLevelEnum.getText(), spaceLevelEnum.getMaxCount(), spaceLevelEnum.getMaxSize())).collect(Collectors.toList());
        return ResultUtils.success(spaceLevelList);
    }
}
