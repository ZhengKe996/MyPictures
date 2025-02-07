package fun.timu.init.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "customExecutor")
    public ThreadPoolExecutor customExecutor() {
        return new ThreadPoolExecutor(10, // 核心线程数
                20, // 最大线程数
                60L, // 空闲线程存活时间
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(100) // 队列容量
        );
    }
}
