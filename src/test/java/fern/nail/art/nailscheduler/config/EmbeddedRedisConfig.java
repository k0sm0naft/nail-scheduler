package fern.nail.art.nailscheduler.config;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

@Configuration
public class EmbeddedRedisConfig {

    @PostConstruct
    public void startRedis() throws IOException {
        new RedisServer().start();
    }
}