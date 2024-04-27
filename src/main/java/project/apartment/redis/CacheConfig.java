package project.apartment.redis;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;



@Configuration
public class CacheConfig {

    /**
     * 구성이 완료된 RedisCacheConfiguration을 사용하여 RedisCacheManager를 생성합니다.
     * 이렇게 구성된 RedisCacheManager는 Spring Boot 애플리케이션에서 Redis 캐시를 관리하는 데 사용됩니다.
     * 캐시 설정과 Redis 연결을 제어하고 캐시 작업을 수행할 때 RedisCacheManager를 주입받아 사용할 수 있다.
     * @param redisConnectionFactory
     * @param resourceLoader
     * @return
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory,
                                          ResourceLoader resourceLoader) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                // null 값 캐싱을 비활성화합니다. 이는 Redis에 null 값을 저장하지 않도록 설정합니다.
                .disableCachingNullValues()
                // Json 형식으로 직렬화 돼 Redis에 저장되고 검색된다.
                .serializeValuesWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer())
                );

        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();

        //캐시 항목의 만료시간 설정.
        redisCacheConfigurationMap.put(CacheNames.USERBYUSERNAME, defaultConfig.entryTtl(Duration.ofHours(4)));

        redisCacheConfigurationMap.put(CacheNames.ALLUSERS, defaultConfig.entryTtl(Duration.ofHours(4))
                //값을 직렬화하는 방식을 설정합니다.
                .serializeValuesWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(new JdkSerializationRedisSerializer())
                )
        );

        redisCacheConfigurationMap.put(CacheNames.LOGINUSER, defaultConfig.entryTtl(Duration.ofHours(2)));

        redisCacheConfigurationMap.put(CacheNames.USERBYEMAIL, defaultConfig.entryTtl(Duration.ofHours(4)));


        return RedisCacheManager.builder(redisConnectionFactory).withInitialCacheConfigurations(redisCacheConfigurationMap).build();
    }
}
