package com.example.ispitni_projekat_f.dao;

import com.example.ispitni_projekat_f.model.dto.EventStatsDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.resps.Tuple;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class RedisDAO {

    private final long TTL = 3600;
    @Inject
    private JedisPool jedisPool;
    private final String topViewsKey = "topViews";
    private final String topReactionsKey = "topReactions";

    private String statsKey(Long eventId) {
        return "event:" + eventId + ":stats";
    }

    private String scoreKey(Long eventId) {
        return "event:" + eventId;
    }

    public void incrementView(Long eventId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hincrBy(statsKey(eventId), "views", 1L);
            jedis.zincrby(topViewsKey, 1, scoreKey(eventId));
            jedis.expire(statsKey(eventId), TTL);
        } catch (Exception ex) {
            System.err.println("Failed to increment view in Redis: " + ex.getMessage());
        }
    }

    public void incrementLike(Long eventId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hincrBy(statsKey(eventId), "likes", 1L);
            jedis.zincrby(topReactionsKey, 1, scoreKey(eventId));
            jedis.expire(statsKey(eventId), TTL);
        } catch (Exception ex) {
            System.err.println("Failed to increment like in Redis: " + ex.getMessage());
        }
    }

    public void decrementLike(Long eventId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hincrBy(statsKey(eventId), "likes", -1L);
            jedis.zincrby(topReactionsKey, -1, scoreKey(eventId));
            jedis.expire(statsKey(eventId), TTL);
        } catch (Exception ex) {
            System.err.println("Failed to decrementLike in Redis: " + ex.getMessage());
        }
    }

    public void incrementDislike(Long eventId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hincrBy(statsKey(eventId), "dislikes", 1L);
            jedis.zincrby(topReactionsKey, 1, scoreKey(eventId));
            jedis.expire(statsKey(eventId), TTL);
        } catch (Exception ex) {
            System.err.println("Failed to increment dislike in Redis: " + ex.getMessage());
        }
    }

    public void decrementDislike(Long eventId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hincrBy(statsKey(eventId), "dislikes", -1L);
            jedis.zincrby(topReactionsKey, -1, scoreKey(eventId));
            jedis.expire(statsKey(eventId), TTL);
        } catch (Exception ex) {
            System.err.println("Failed to decrementDislike in Redis: " + ex.getMessage());
        }
    }

    public EventStatsDTO getEventStats(Long eventId) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> stats = jedis.hgetAll(statsKey(eventId));
            jedis.expire(statsKey(eventId), TTL);
            EventStatsDTO dto = new EventStatsDTO();
            if (stats.isEmpty())
                return null;

            dto.setLikes(Long.parseLong(stats.getOrDefault("likes", "0")));
            dto.setDislikes(Long.parseLong(stats.getOrDefault("dislikes", "0")));
            dto.setViews(Long.parseLong(stats.getOrDefault("views", "0")));
            return dto;
        } catch (Exception ex) {
            System.err.println("Redis unavailable: " + ex.getMessage());
            return null;
        }
    }

    public void cacheEventStats(Long eventId, EventStatsDTO stats) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(statsKey(eventId), Map.of(
                    "likes", String.valueOf(stats.getLikes()),
                    "dislikes", String.valueOf(stats.getDislikes()),
                    "views", String.valueOf(stats.getViews())
            ));
            jedis.zadd(topViewsKey, stats.getViews(), scoreKey(eventId));
            jedis.zadd(topReactionsKey, stats.getDislikes() + stats.getLikes(), scoreKey(eventId));
            jedis.expire(statsKey(eventId), TTL);
        } catch (Exception ex) {
            System.err.println("Failed to cache event stats in Redis: " + ex.getMessage());
        }
    }

    public List<Long> getTopViewedEvents(int n) {
        try (Jedis jedis = jedisPool.getResource()) {
            var tuples = jedis.zrevrangeWithScores(topViewsKey, 0, Math.max(0, n - 1));
            return tuples.stream().map(Tuple::getElement).map(str -> str.replace("event:", "")).map(Long::parseLong).collect(Collectors.toList());
        }
    }

    public List<Long> getTopReactedEvents(int n) {
        try (Jedis jedis = jedisPool.getResource()) {
            var tuples = jedis.zrevrangeWithScores(topReactionsKey, 0, Math.max(0, n - 1));
            return tuples.stream().map(Tuple::getElement).map(str -> str.replace("event:", "")).map(Long::parseLong).collect(Collectors.toList());
        }
    }

}
