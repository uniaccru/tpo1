package org.example.part2.tracing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RBTreeTraceRecorder {
    private RBTreeTraceRecorder() {
    }

    private static final Map<String, Map<Integer, RBTreeTracePoint>> LINE_TO_POINT = new ConcurrentHashMap<>();

    private static final ThreadLocal<List<RBTreeTracePoint>> TRACE = ThreadLocal.withInitial(ArrayList::new);
    private static final ThreadLocal<List<String>> RAW_TRACE = ThreadLocal.withInitial(ArrayList::new);
    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);

    public static void register(String methodName, int line, RBTreeTracePoint point) {
        LINE_TO_POINT.computeIfAbsent(methodName, ignored -> new ConcurrentHashMap<>())
                .put(line, point);
    }

    public static void start() {
        TRACE.get().clear();
        RAW_TRACE.get().clear();
        ACTIVE.set(true);
    }

    public static List<RBTreeTracePoint> stop() {
        ACTIVE.set(false);
        return new ArrayList<>(TRACE.get());
    }

    public static void clear() {
        TRACE.get().clear();
        RAW_TRACE.get().clear();
    }

    public static List<String> rawTraceSnapshot() {
        return new ArrayList<>(RAW_TRACE.get());
    }

    public static void hitLine(String methodName, int line) {
        if (!ACTIVE.get()) {
            return;
        }
        RAW_TRACE.get().add(methodName + ":" + line);
        RBTreeTracePoint point = LINE_TO_POINT
                .getOrDefault(methodName, Map.of())
                .get(line);
        if (point != null) {
            TRACE.get().add(point);
        }
    }
}
