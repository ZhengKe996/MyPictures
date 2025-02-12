package fun.timu.init.controller;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import fun.timu.init.annotation.AuthCheck;
import fun.timu.init.common.BaseResponse;
import fun.timu.init.common.ErrorCode;
import fun.timu.init.common.ResultUtils;
import fun.timu.init.constant.UserConstant;
import fun.timu.init.model.dto.picture.PictureUploadRequest;
import fun.timu.init.model.entity.User;
import fun.timu.init.model.vo.GithubRepoVO;
import fun.timu.init.model.vo.GithubUserVO;
import fun.timu.init.model.vo.PictureVO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GithubController {

    /**
     * 获取GitHub用户信息的API端点
     *
     * @param username
     * @param request
     * @return
     */
    @GetMapping("/userinfo")
    public BaseResponse<GithubUserVO> getGithubUserInfo(String username, HttpServletRequest request) {
        try {
            // 构建GitHub API URL
            String url = "https://api.github.com/users/" + username;

            // 发送HTTP GET请求
            String response = HttpUtil.get(url);

            // 解析JSON响应
            JSONObject jsonObject = JSONUtil.parseObj(response);

            // 将JSON对象转换为GithubUserVO对象
            GithubUserVO githubUserVO = JSONUtil.toBean(jsonObject, GithubUserVO.class);

            // 返回成功响应
            return ResultUtils.success(githubUserVO);
        } catch (Exception e) {
            // 返回错误信息给前端
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
    }

    /**
     * 获取GitHub用户仓库信息
     * 该方法通过发送HTTP GET请求到GitHub API，获取指定用户的仓库信息，并解析返回给前端
     *
     * @param username GitHub用户名，用于指定要获取仓库信息的用户
     * @param request  HttpServletRequest对象，可用于获取请求相关的信息
     * @return BaseResponse<List < GithubRepoVO>> 包含GitHub仓库信息的响应对象，或包含错误信息的响应对象
     */
    @GetMapping("/repos")
    public BaseResponse<List<GithubRepoVO>> getGithubUserRepos(String username, HttpServletRequest request) {
        try {
            // 构建GitHub API URL
            String url = "https://api.github.com/users/" + username + "/repos";
            String response = HttpUtil.get(url);
            List<GithubRepoVO> list = JSONUtil.toList(JSONUtil.parseArray(response), GithubRepoVO.class);
            return ResultUtils.success(list);
        } catch (Exception e) {
            // 返回错误信息给前端
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
    }

}
