package fun.timu.init.manager;

import cn.hutool.core.io.FileUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import fun.timu.init.config.CosClientConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class CosManager {
    private final CosClientConfig cosClientConfig;

    private final COSClient cosClient;

    public CosManager(CosClientConfig cosClientConfig, COSClient cosClient) {
        this.cosClientConfig = cosClientConfig;
        this.cosClient = cosClient;
    }

    /**
     * 将文件上传到指定的存储桶中
     *
     * @param key  文件在存储桶中的对象键（即路径和文件名）
     * @param file 要上传的本地文件对象
     * @return 返回上传后的对象信息，包括ETag、版本ID等
     */
    public PutObjectResult putObject(String key, File file) {
        // 创建一个PutObjectRequest对象，指定要上传到的存储桶、对象键和本地文件
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        // 使用cosClient执行上传操作，并返回上传后的对象信息
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 根据指定的键获取对象
     * 本函数通过发送请求到COS（Cloud Object Storage）来获取与键关联的对象
     * 主要解决了如何通过键值高效地从COS中检索对象的问题
     *
     * @param key 对象的唯一标识符，用于定位COS中的特定对象
     * @return 返回一个COSObject对象，该对象包含了从COS获取的与键关联的数据
     */
    public COSObject getObject(String key) {
        // 创建一个GetObjectRequest对象，用于指定获取对象的请求参数
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        // 通过cosClient发起获取对象的请求，并返回获取到的对象
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 上传对象（附带图片信息）
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putPictureObject(String key, File file) {
        // 创建上传请求
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        // 对图片进行处理（获取基本信息也被视作为一种处理）
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        // 创建一个规则列表来存储图片处理规则
        List<PicOperations.Rule> rules = new ArrayList<>();
        // 图片压缩（转成 webp 格式）
        String webpKey = FileUtil.mainName(key) + ".webp";
        PicOperations.Rule compressRule = new PicOperations.Rule();
        compressRule.setRule("imageMogr2/format/webp");
        compressRule.setBucket(cosClientConfig.getBucket());
        compressRule.setFileId(webpKey);
        rules.add(compressRule);
        // 构造处理参数
        picOperations.setRules(rules);
        // 设置图片处理操作到请求中
        putObjectRequest.setPicOperations(picOperations);
        // 执行上传操作并返回结果
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 删除对象
     *
     * @param key 唯一键
     */
    public void deleteObject(String key) {
        // 根据提供的键从配置的存储桶中删除对象
        cosClient.deleteObject(cosClientConfig.getBucket(), key);
    }
}
