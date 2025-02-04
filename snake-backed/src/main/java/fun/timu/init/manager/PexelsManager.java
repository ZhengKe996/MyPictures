package fun.timu.init.manager;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import fun.timu.init.config.PexelsConfig;
import fun.timu.init.model.dto.picture.PictureUploadRequest;
import fun.timu.init.model.entity.Picture;
import fun.timu.init.model.vo.PictureVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Component
public class PexelsManager {
    private final PexelsConfig pexelsConfig;


    public PexelsManager(PexelsConfig pexelsConfig) {
        this.pexelsConfig = pexelsConfig;
    }


    /**
     * 根据给定的搜索文本、数量和名称前缀，从 Pexels 获取图片
     *
     * @param searchText 搜索文本，用于在 Pexels 上搜索图片
     * @param count      希望获取的图片数量，必须是大于 0 且小于等于 30 的整数
     * @param namePrefix 图片名称的前缀，不能为空
     * @return 返回一个 Picture 对象的列表，每个对象代表一张图片
     * @throws IllegalArgumentException 如果 count 不在有效范围内或 namePrefix 为空
     * @throws RuntimeException         如果 URL 编码失败、API 返回的数据格式不正确或其他请求相关错误
     */
    public List<PictureUploadRequest> getPictureByPexels(String searchText, Integer count, String namePrefix) {
        // 验证参数有效性
        if (count <= 0 || count > 30) {
            throw new IllegalArgumentException("count 必须是大于 0 且小于等于 30 的整数");
        }
        if (StrUtil.isBlank(namePrefix)) {
            throw new IllegalArgumentException("namePrefix 不能为空");
        }

        try {
            // 对 searchText 进行 URL 编码
            String encodedSearchText = URLEncoder.encode(searchText, "UTF-8");

            // Pexels API 的搜索 URL
            String fetchUrl = String.format("https://api.pexels.com/v1/search?query=%s&per_page=%d", encodedSearchText, count);

            // 发送 HTTP 请求获取搜索结果
            String response = HttpUtil.createGet(fetchUrl).header("Authorization", pexelsConfig.getApiKey()).execute().body();

            // 解析 JSON 响应
            JSONObject jsonObject = JSONUtil.parseObj(response);
            if (!jsonObject.containsKey("photos")) {
                throw new RuntimeException("Pexels API 返回的数据格式不正确");
            }

            // 获取所有图片元素
            List<JSONObject> photos = jsonObject.getJSONArray("photos").toList(JSONObject.class);
            List<PictureUploadRequest> pictureList = new ArrayList<>();

            // 遍历所有图片元素
            for (JSONObject photo : photos) {
                // 获取图片的URL
                String fileUrl = photo.getJSONObject("src").getStr("original");
                // 如果图片URL为空，则记录日志并跳过当前图片
                if (StrUtil.isBlank(fileUrl)) continue;


                // 创建上传图片的请求对象
                PictureUploadRequest picture = new PictureUploadRequest();
                picture.setFileUrl(fileUrl);
                picture.setPicName(namePrefix + (pictureList.size() + 1));

                // 添加到列表
                pictureList.add(picture);
            }

            // 返回图片列表
            return pictureList;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URL 编码失败", e);
        } catch (Exception e) {
            throw new RuntimeException("请求或解析失败", e);
        }
    }

}
