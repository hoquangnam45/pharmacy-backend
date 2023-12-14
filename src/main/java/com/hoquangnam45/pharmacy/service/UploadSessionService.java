package com.hoquangnam45.pharmacy.service;

import com.hoquangnam45.pharmacy.pojo.UploadSession;
import com.hoquangnam45.pharmacy.pojo.UploadSessionConfig;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class UploadSessionService {
    // type -> session id -> session info
    private final Map<String, Map<String, UploadSession>> allSessions = new HashMap<>();

    public UploadSession createUploadSession(String type, UploadSessionConfig config) {
        String sessionId = UUID.randomUUID().toString();
        Set<String> allowedFileIds = IntStream.range(0, config.getMaximumFileCount())
                .mapToObj(i -> UUID.randomUUID().toString())
                .collect(Collectors.toSet());
        UploadSession session = new UploadSession(sessionId, allowedFileIds, OffsetDateTime.now().plus(config.getExpiredDuration()), config.getExpiredDuration());
        allSessions.computeIfAbsent(type, k -> new HashMap<>()).putIfAbsent(sessionId, session);
        return session;
    }

    public void renewSession(String type, String sessionId) {
        if (hasSessionExpired(type, sessionId)) {
            return;
        }
        Map<String, UploadSession> sessions = allSessions.get(type);
        UploadSession currentSession = sessions.get(sessionId);
        sessions.put(sessionId, new UploadSession(
                currentSession.getSessionId(),
                currentSession.getAllowedFileIds(),
                OffsetDateTime.now().plus(currentSession.getExpiredDuration()),
                currentSession.getExpiredDuration()));
    }

    public boolean hasSessionExpired(String type, String sessionId) {
        Map<String, UploadSession> sessions = allSessions.get(type);
        if (sessions == null) {
            return true;
        }
        UploadSession currentSession = sessions.get(sessionId);
        return currentSession == null || currentSession.getExpiredAt().isBefore(OffsetDateTime.now());
    }

    public boolean allowItemId(String type, String sessionId, String fileId) {
        return allSessions.get(type).get(sessionId).getAllowedFileIds().contains(fileId);
    }

    public String getTempFileUploadKey(String type, String sessionId, String fileId) {
        return MessageFormat.format("{0}/tmp/{1}/{2}", type, sessionId, fileId);
    }

    public String getTempTypeUploadFolder(String type) {
        return MessageFormat.format("{0}/tmp/", type);
    }

    public String getTempSessionUploadFolder(String type, String sessionId) {
        return MessageFormat.format("{0}/tmp/{1}", type, sessionId);
    }
}
