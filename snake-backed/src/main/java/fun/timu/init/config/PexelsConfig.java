package fun.timu.init.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pexels")
@Data
public class PexelsConfig {

    /**
     * Api请求秘钥
     */
    private String apiKey;
}
