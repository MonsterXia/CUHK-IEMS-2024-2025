package tech.hirsun.jade.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
@Getter @Setter
public class RedisPoolFactory {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.database}")
    private int database;

    @Value("${redis.timeout}")
    private int timeout;

    @Value("${redis.poolMaxWait}")
    private int maxWaitSeconds;

    @Value("${redis.poolMaxIdle}")
    private int maxIdle;

    @Value("${redis.poolMaxTotal}")
    private int maxTotal;

    @Bean
    public JedisPool JedisPoolFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxWaitMillis(maxWaitSeconds * 1000L);
        if (password == null || password.isEmpty()) {
            return new JedisPool(poolConfig, host, port, timeout * 1000, null, database);
        } else {
            return new JedisPool(poolConfig, host, port, timeout * 1000, password, database);
        }
    }

}