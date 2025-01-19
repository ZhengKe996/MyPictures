package fun.timu.init.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.timu.init.common.BaseResponse;
import fun.timu.init.common.ErrorCode;
import fun.timu.init.common.ResultUtils;
import fun.timu.init.exception.BusinessException;
import fun.timu.init.exception.ThrowUtils;
import fun.timu.init.model.dto.post.PostQueryRequest;
import fun.timu.init.model.dto.postfavour.PostFavourAddRequest;
import fun.timu.init.model.dto.postfavour.PostFavourQueryRequest;
import fun.timu.init.model.entity.Post;
import fun.timu.init.model.entity.User;
import fun.timu.init.model.vo.PostVO;
import fun.timu.init.service.PostFavourService;
import fun.timu.init.service.PostService;
import fun.timu.init.service.UserService;
import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 收藏接口
 */
@Slf4j
@RestController
@RequestMapping("/post_favour")
public class PostFavourController {
    @Resource
    private PostService postService;

    @Resource
    private PostFavourService postFavourService;

    @Resource
    private UserService userService;

    /**
     * 收藏 / 取消收藏
     *
     * @param postFavourAddRequest
     * @param request
     * @return
     */
    @PostMapping("/")
    public BaseResponse<Integer> doPostFavour(@RequestBody PostFavourAddRequest postFavourAddRequest, HttpServletRequest request) {
        if (postFavourAddRequest == null || postFavourAddRequest.getPostId() <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        // 登录才能操作
        final User loginUser = userService.getLoginUser(request);
        long postId = postFavourAddRequest.getPostId();
        int result = postFavourService.doPostFavour(postId, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取我收藏的列表
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<PostVO>> listMyFavourPostByPage(@RequestBody PostQueryRequest postQueryRequest, HttpServletRequest request) {
        if (postQueryRequest == null) throw new BusinessException(ErrorCode.PARAMS_ERROR);

        User loginUser = userService.getLoginUser(request);
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postFavourService.listFavourPostByPage(new Page<>(current, size), postService.getQueryWrapper(postQueryRequest), loginUser.getId());
        return ResultUtils.success(postService.getPostVOPage(postPage, request));
    }

    /**
     * 获取用户收藏的列表
     *
     * @param postFavourQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<PostVO>> listFavourPostByPage(@RequestBody PostFavourQueryRequest postFavourQueryRequest, HttpServletRequest request) {
        if (postFavourQueryRequest == null) throw new BusinessException(ErrorCode.PARAMS_ERROR);

        long current = postFavourQueryRequest.getCurrent();
        long size = postFavourQueryRequest.getPageSize();
        Long userId = postFavourQueryRequest.getUserId();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20 || userId == null, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postFavourService.listFavourPostByPage(new Page<>(current, size), postService.getQueryWrapper(postFavourQueryRequest.getPostQueryRequest()), userId);
        return ResultUtils.success(postService.getPostVOPage(postPage, request));
    }
}
