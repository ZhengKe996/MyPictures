package fun.timu.init.controller;

import fun.timu.init.common.BaseResponse;
import fun.timu.init.common.ErrorCode;
import fun.timu.init.common.ResultUtils;
import fun.timu.init.exception.ThrowUtils;
import fun.timu.init.model.dto.analyze.*;
import fun.timu.init.model.entity.Space;
import fun.timu.init.model.entity.User;
import fun.timu.init.model.vo.analyze.*;
import fun.timu.init.service.SpaceAnalyzeService;
import fun.timu.init.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/analyze")
public class SpaceAnalyzeController {

    private final SpaceAnalyzeService spaceAnalyzeService;
    private final UserService userService;

    public SpaceAnalyzeController(SpaceAnalyzeService spaceAnalyzeService, UserService userService) {
        this.spaceAnalyzeService = spaceAnalyzeService;
        this.userService = userService;
    }

    /**
     * 获取空间使用状态
     */
    @PostMapping("/usage")
    public BaseResponse<SpaceUsageAnalyzeResponse> getSpaceUsageAnalyze(@RequestBody SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUsageAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        SpaceUsageAnalyzeResponse spaceUsageAnalyze = spaceAnalyzeService.getSpaceUsageAnalyze(spaceUsageAnalyzeRequest, loginUser);
        return ResultUtils.success(spaceUsageAnalyze);
    }

    /**
     * 处理空间类别分析请求的控制器方法
     * 该方法响应带有"/category"路径的POST请求，用于分析空间数据并按类别返回分析结果
     *
     * @param spaceCategoryAnalyzeRequest 包含分析请求参数的对象，用于指定分析条件
     * @param request                     HTTP请求对象，用于获取登录用户信息
     * @return 返回一个BaseResponse对象，其中包含空间类别分析结果列表
     */
    @PostMapping("/category")
    public BaseResponse<List<SpaceCategoryAnalyzeResponse>> getSpaceCategoryAnalyze(@RequestBody SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, HttpServletRequest request) {
        // 检查请求参数是否为空，如果为空则抛出参数错误异常
        ThrowUtils.throwIf(spaceCategoryAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 获取当前登录用户信息
        User loginUser = userService.getLoginUser(request);

        // 调用服务层方法获取空间类别分析结果
        List<SpaceCategoryAnalyzeResponse> resultList = spaceAnalyzeService.getSpaceCategoryAnalyze(spaceCategoryAnalyzeRequest, loginUser);

        // 构造成功响应对象并返回分析结果列表
        return ResultUtils.success(resultList);
    }

    /**
     * 处理 POST 请求，用于获取空间标签分析结果
     *
     * @param spaceTagAnalyzeRequest 空间标签分析的请求参数对象，封装了进行空间标签分析所需的信息
     * @param request                HTTP请求对象，用于获取登录用户信息
     * @return 返回一个包含空间标签分析结果列表的BaseResponse对象
     * <p>
     * 此方法首先检查传入的SpaceTagAnalyzeRequest对象是否为null，如果为null，则抛出参数错误异常
     * 然后通过UserService获取当前登录用户信息，接着调用SpaceAnalyzeService中的方法进行空间标签分析
     * 最后，使用ResultUtils将分析结果封装成成功响应对象并返回
     */
    @PostMapping("/tag")
    public BaseResponse<List<SpaceTagAnalyzeResponse>> getSpaceTagAnalyze(@RequestBody SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, HttpServletRequest request) {
        // 检查请求参数是否为null，如果为null，则抛出参数错误异常
        ThrowUtils.throwIf(spaceTagAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 获取当前登录用户信息
        User loginUser = userService.getLoginUser(request);

        // 调用服务层方法获取空间标签分析结果列表
        List<SpaceTagAnalyzeResponse> resultList = spaceAnalyzeService.getSpaceTagAnalyze(spaceTagAnalyzeRequest, loginUser);

        // 将分析结果列表封装成成功响应对象并返回
        return ResultUtils.success(resultList);
    }

    /**
     * 处理空间大小分析请求
     * 该方法用于分析空间大小，并返回分析结果列表
     *
     * @param spaceSizeAnalyzeRequest 空间大小分析请求对象，包含分析所需的参数
     * @param request                 HTTP请求对象，用于获取登录用户信息
     * @return 返回一个包含空间大小分析结果列表的响应对象
     */
    @PostMapping("/size")
    public BaseResponse<List<SpaceSizeAnalyzeResponse>> getSpaceSizeAnalyze(@RequestBody SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, HttpServletRequest request) {
        // 检查请求参数是否为空，如果为空则抛出参数错误异常
        ThrowUtils.throwIf(spaceSizeAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 调用服务方法获取空间大小分析结果列表
        List<SpaceSizeAnalyzeResponse> resultList = spaceAnalyzeService.getSpaceSizeAnalyze(spaceSizeAnalyzeRequest, loginUser);

        // 返回成功响应，包含分析结果列表
        return ResultUtils.success(resultList);
    }

    /**
     * 处理用户空间分析的POST请求
     * 该方法用于获取用户空间分析数据，根据提供的请求参数和当前登录用户信息进行处理
     *
     * @param spaceUserAnalyzeRequest 用户空间分析请求对象，包含分析所需的参数
     * @param request                 HTTP请求对象，用于获取当前登录用户信息
     * @return 返回一个包含用户空间分析数据的响应对象
     */
    @PostMapping("/user")
    public BaseResponse<List<SpaceUserAnalyzeResponse>> getSpaceUserAnalyze(@RequestBody SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, HttpServletRequest request) {
        // 检查请求参数是否为空，如果为空则抛出参数错误异常
        ThrowUtils.throwIf(spaceUserAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 获取当前登录用户信息
        User loginUser = userService.getLoginUser(request);

        // 调用服务层方法获取用户空间分析数据
        List<SpaceUserAnalyzeResponse> resultList = spaceAnalyzeService.getSpaceUserAnalyze(spaceUserAnalyzeRequest, loginUser);

        // 返回成功响应，包含用户空间分析数据列表
        return ResultUtils.success(resultList);
    }

    /**
     * 处理空间排名分析请求
     * 该方法用于接收客户端发送的空间排名分析请求，并返回分析结果
     *
     * @param spaceRankAnalyzeRequest 空间排名分析请求对象，包含分析所需的参数
     * @param request                 HTTP请求对象，用于获取登录用户信息
     * @return 返回一个包含空间排名分析结果的BaseResponse对象
     */
    @PostMapping("/rank")
    public BaseResponse<List<Space>> getSpaceRankAnalyze(@RequestBody SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, HttpServletRequest request) {
        // 检查请求参数是否为空，如果为空则抛出参数错误异常
        ThrowUtils.throwIf(spaceRankAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 获取当前登录用户信息
        User loginUser = userService.getLoginUser(request);

        // 调用服务层方法获取空间排名分析结果
        List<Space> resultList = spaceAnalyzeService.getSpaceRankAnalyze(spaceRankAnalyzeRequest, loginUser);

        // 返回成功响应，包含分析结果
        return ResultUtils.success(resultList);
    }


}
