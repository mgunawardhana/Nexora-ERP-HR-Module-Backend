package com.nexora.backend.gemini.repository;

import com.nexora.backend.domain.entity.GeminiLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Repository
public class GeminiRepository {

    private static final int MAX_LOG_HISTORY = 1000;
    private final Map<String, GeminiLog> logs = new ConcurrentHashMap<>();
    private final List<GeminiLog> logHistory = new CopyOnWriteArrayList<>();

    public void saveLog(String sessionId, String prompt, String response) {
        GeminiLog log = new GeminiLog(sessionId, prompt, response, LocalDateTime.now());
        logs.put(sessionId, log);
        logHistory.add(log);

        while (logHistory.size() > MAX_LOG_HISTORY) {
            logHistory.remove(0);
        }
    }

    public GeminiLog getLog(String sessionId) {
        return logs.get(sessionId);
    }

    public List<GeminiLog> getAllLogs() {
        return new ArrayList<>(logHistory);
    }
}