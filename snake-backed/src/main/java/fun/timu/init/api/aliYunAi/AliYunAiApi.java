package fun.timu.init.api.aliYunAi;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.*;
import cn.hutool.json.JSONUtil;
import fun.timu.init.api.aliYunAi.model.CreateOutPaintingTaskRequest;
import fun.timu.init.api.aliYunAi.model.CreateOutPaintingTaskResponse;
import fun.timu.init.api.aliYunAi.model.GetOutPaintingTaskResponse;
import fun.timu.init.common.ErrorCode;
import fun.timu.init.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
public class AliYunAiApi {
    // 读取配置文件
    @Value("${aliYunAi.apiKey}")
    private String apiKey;

    // 创建任务地址
    public static final String CREATE_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/out-painting";

    // 查询任务状态
    public static final String GET_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";

    /**
     * 创建任务
     *
     * @param createOutPaintingTaskRequest
     * @return
     */
    public CreateOutPaintingTaskResponse createOutPaintingTask(CreateOutPaintingTaskRequest createOutPaintingTaskRequest) {
        if (createOutPaintingTaskRequest == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "扩图参数为空");
        }
        // 发送请求
        HttpRequest httpRequest = HttpRequest.post(CREATE_OUT_PAINTING_TASK_URL).header(Header.AUTHORIZATION, "Bearer " + apiKey)
                // 必须开启异步处理，设置为enable。
                .header("X-DashScope-Async", "enable").header(Header.CONTENT_TYPE, ContentType.JSON.getValue()).body(JSONUtil.toJsonStr(createOutPaintingTaskRequest));
        try (HttpResponse httpResponse = httpRequest.execute()) {
            if (!httpResponse.isOk()) {
                log.error("请求异常：{}", httpResponse.body());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图失败");
            }
            CreateOutPaintingTaskResponse response = JSONUtil.toBean(httpResponse.body(), CreateOutPaintingTaskResponse.class);
            String errorCode = response.getCode();
            if (StrUtil.isNotBlank(errorCode)) {
                String errorMessage = response.getMessage();
                log.error("AI 扩图失败，errorCode:{}, errorMessage:{}", errorCode, errorMessage);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图接口响应异常");
            }
            return response;
        }
    }

    /**
     * 查询创建的任务
     *
     * @param taskId 任务ID，用于查询特定的任务信息
     * @return 返回GetOutPaintingTaskResponse对象，包含任务详细信息
     * @throws BusinessException 当任务ID为空、格式不正确，或获取任务失败时抛出
     */
    public GetOutPaintingTaskResponse getOutPaintingTask(String taskId) {
        // 检查任务ID是否为空
        if (StrUtil.isBlank(taskId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务 id 不能为空");
        }

        // 验证 taskId 格式
        if (!isValidTaskId(taskId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "任务 id 格式不正确");
        }

        try {
            // 构造请求URL
            String url = String.format(GET_OUT_PAINTING_TASK_URL, taskId);
            // 发起HTTP GET请求获取任务信息
            try (HttpResponse httpResponse = HttpRequest.get(url).header(Header.AUTHORIZATION, "Bearer " + apiKey).execute()) {
                // 检查HTTP响应状态码
                if (!httpResponse.isOk()) {
                    log.error("获取任务失败，HTTP 状态码: {}", httpResponse.getStatus());
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取任务失败");
                }
                // 将响应体转换为GetOutPaintingTaskResponse对象并返回
                return JSONUtil.toBean(httpResponse.body(), GetOutPaintingTaskResponse.class);
            }
        } catch (HttpException e) {
            // 处理网络请求异常
            log.error("网络请求异常: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "网络请求失败");
        }
    }

    private boolean isValidTaskId(String taskId) {
        // 实现 taskId 的格式验证逻辑，例如正则表达式匹配
        return taskId.matches("[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}");
    }
}
