package com.taronote.brain.service;

import com.taronote.admin.port.AdminPort;
import com.taronote.brain.port.AgentBrainPort;
import com.taronote.identity.domain.UserType;
import com.taronote.identity.repository.UserRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@Service
public class AgentScheduler {
    private static final Logger log = LoggerFactory.getLogger(AgentScheduler.class);
    private static final String SCHEDULE_KEY = "agent:schedule";

    private final StringRedisTemplate redisTemplate;
    private final AgentBrainPort agentBrainPort;
    private final UserRepository userRepository;
    private final AdminPort adminPort;
    private final ExecutorService executor;
    private final int batchSize;
    private final Duration baseInterval;
    private final double jitterRatio;
    private final Random random = new Random();

    public AgentScheduler(StringRedisTemplate redisTemplate,
                          AgentBrainPort agentBrainPort,
                          UserRepository userRepository,
                          AdminPort adminPort,
                          @Value("${agent.scheduler.batch-size:32}") int batchSize,
                          @Value("${agent.scheduler.base-interval-seconds:1800}") long baseIntervalSeconds,
                          @Value("${agent.scheduler.jitter-ratio:0.2}") double jitterRatio) {
        this.redisTemplate = redisTemplate;
        this.agentBrainPort = agentBrainPort;
        this.userRepository = userRepository;
        this.adminPort = adminPort;
        this.batchSize = batchSize;
        this.baseInterval = Duration.ofSeconds(baseIntervalSeconds);
        this.jitterRatio = jitterRatio;
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedSchedule() {
        List<UUID> agentIds = userRepository.findByType(UserType.AGENT).stream()
                .map(user -> user.id())
                .toList();
        long now = System.currentTimeMillis();
        for (UUID agentId : agentIds) {
            schedule(agentId.toString(), now + baseInterval.toMillis());
        }
        log.info("Seeded {} agents into schedule", agentIds.size());
    }

    @Scheduled(fixedDelay = 1000)
    public void dispatch() {
        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
        long now = System.currentTimeMillis();
        var dueSet = zset.rangeByScore(SCHEDULE_KEY, 0, now, 0, batchSize);
        if (dueSet == null || dueSet.isEmpty()) {
            return;
        }
        List<String> due = dueSet.stream().toList();
        for (String agentId : due) {
            zset.remove(SCHEDULE_KEY, agentId);
            executor.submit(() -> runAgent(agentId));
        }
    }

    private void runAgent(String agentId) {
        if (adminPort.isFrozen(agentId)) {
            return;
        }
        try {
            agentBrainPort.run(UUID.fromString(agentId));
        } catch (Exception ex) {
            log.warn("Agent run failed: {}", ex.getMessage());
        } finally {
            long next = System.currentTimeMillis() + jitteredInterval();
            if (!adminPort.isFrozen(agentId)) {
                schedule(agentId, next);
            }
        }
    }

    private long jitteredInterval() {
        long baseMillis = baseInterval.toMillis();
        long jitter = (long) (baseMillis * jitterRatio);
        long offset = jitter == 0 ? 0 : (random.nextLong(jitter * 2) - jitter);
        return Math.max(1000, baseMillis + offset);
    }

    private void schedule(String agentId, long nextWakeMillis) {
        redisTemplate.opsForZSet().add(SCHEDULE_KEY, agentId, nextWakeMillis);
    }
}
