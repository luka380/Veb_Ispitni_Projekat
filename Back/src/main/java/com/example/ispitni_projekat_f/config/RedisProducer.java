package com.example.ispitni_projekat_f.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;
import redis.clients.jedis.args.FlushMode;

import java.time.Duration;

@ApplicationScoped
public class RedisProducer {

    private static final GenericObjectPoolConfig<Jedis> poolCfg = new GenericObjectPoolConfig<>();

    static {
        poolCfg.setMaxTotal(64);
        poolCfg.setMaxIdle(16);
        poolCfg.setMinIdle(1);

        poolCfg.setTestOnCreate(true);
        poolCfg.setTestOnBorrow(true);
        poolCfg.setTestWhileIdle(true);

        poolCfg.setTimeBetweenEvictionRuns(Duration.ofSeconds(60));
        poolCfg.setMinEvictableIdleTime(Duration.ofSeconds(120));
    }

    private final JedisClientConfig clientCfg = DefaultJedisClientConfig.builder()
            .connectionTimeoutMillis(3000)
            .socketTimeoutMillis(3000)
            .clientName("raf")
            .build();
    private final JedisPool pool = new JedisPool(poolCfg, new HostAndPort("localhost", 6379), clientCfg);

    @Produces
    public JedisPool produceJedisPool() {
        try (Jedis jedis = pool.getResource()) {
            jedis.flushAll(FlushMode.SYNC);
        }
        return pool;
    }

    public void close(@Disposes JedisPool jedisPool) {
        jedisPool.close();
    }
}
