package fun.timu.init.controller;

import fun.timu.init.common.BaseResponse;
import fun.timu.init.common.ErrorCode;
import fun.timu.init.common.ResultUtils;
import fun.timu.init.exception.BusinessException;
import fun.timu.init.model.dto.postthumb.PostThumbAddRequest;
import fun.timu.init.model.entity.User;
import fun.timu.init.service.PostThumbService;
import fun.timu.init.service.UserService;
import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 点赞接口
 */
@Slf4j
@RestController
@RequestMapping("/post_thumb")
public class PostThumbController {
    @Resource
    private UserService userService;
    @Resource
    private PostThumbService postThumbService;

    /**
     * 点赞 / 取消点赞
     *
     * @param postThumbAddRequest
     * @param request
     * @return
     */
    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest, HttpServletRequest request) {
        if (postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long postId = postThumbAddRequest.getPostId();
        int result = postThumbService.doPostThumb(postId, loginUser);
        return ResultUtils.success(result);
    }
}
