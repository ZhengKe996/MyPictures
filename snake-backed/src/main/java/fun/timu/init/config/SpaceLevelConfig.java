package fun.timu.init.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 空间等级配置
 */
@Data
@Setter
@Getter
@Component
@Deprecated
@ConfigurationProperties(prefix = "space-level")
public class SpaceLevelConfig {

    private Map<String, SpaceLevelProperties> levels;

    public static class SpaceLevelProperties {
        private String name;
        private int level;
        private int maxImages;
        private long maxStorage;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getMaxImages() {
            return maxImages;
        }

        public void setMaxImages(int maxImages) {
            this.maxImages = maxImages;
        }

        public long getMaxStorage() {
            return maxStorage;
        }

        public void setMaxStorage(long maxStorage) {
            this.maxStorage = maxStorage;
        }
    }
}
