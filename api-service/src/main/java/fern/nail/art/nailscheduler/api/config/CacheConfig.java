package fern.nail.art.nailscheduler.api.config;

import java.time.Duration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@EnableCaching
@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
public class CacheConfig {

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder()
                .cacheWriter(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
                .withCacheConfiguration("userCache",
                        RedisCacheConfiguration.defaultCacheConfig()
                                               .entryTtl(RedisCacheWriter.TtlFunction.persistent()))
                .withCacheConfiguration("userProcedureTimeCache",
                        RedisCacheConfiguration.defaultCacheConfig()
                                               .entryTtl(RedisCacheWriter.TtlFunction.persistent()))
                .withCacheConfiguration("slotCache",
                        RedisCacheConfiguration.defaultCacheConfig()
                                               .entryTtl(Duration.ofHours(4)))
                .withCacheConfiguration("appointmentCache",
                        RedisCacheConfiguration.defaultCacheConfig()
                                               .entryTtl(Duration.ofHours(12)))
                .withCacheConfiguration("workdayCache",
                        RedisCacheConfiguration.defaultCacheConfig()
                                               .entryTtl(Duration.ofDays(1)))
                .withCacheConfiguration("workdayTemplateCache",
                        RedisCacheConfiguration.defaultCacheConfig()
                                               .entryTtl(RedisCacheWriter.TtlFunction.persistent()))
                .build();
    }
}
