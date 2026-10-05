/*
 * Decompiled with CFR 0.152.
 */
package dev.aethermc.scanner;

import dev.aethermc.scanner.Detection;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

final class ProbeSession {
    final UUID controlId = UUID.randomUUID();
    final List<UUID> localIds = new ArrayList<UUID>();
    private final Set<UUID> localIdSet = ConcurrentHashMap.newKeySet();
    final AtomicInteger fast = new AtomicInteger();
    final AtomicInteger responses = new AtomicInteger();
    final Queue<String> timings = new ConcurrentLinkedQueue<String>();
    volatile long controlSent;
    volatile long localSent;
    volatile long slowestFastMs;
    volatile int expected;
    volatile boolean controlFast;
    volatile boolean localStarted;
    volatile boolean done;
    volatile boolean controlStarted;
    volatile Detection result;
    volatile boolean clean;
    final AtomicBoolean localsDone = new AtomicBoolean();

    ProbeSession(int n) {
        for (int i = 0; i < n; ++i) {
            UUID uUID = UUID.randomUUID();
            this.localIds.add(uUID);
            this.localIdSet.add(uUID);
        }
    }

    boolean isLocal(UUID uUID) {
        return uUID != null && this.localIdSet.contains(uUID);
    }

    boolean recordLocalFailure(long l, long l2) {
        boolean bl = l <= l2;
        this.timings.add(l + "ms" + (bl ? "*" : ""));
        if (bl) {
            this.fast.incrementAndGet();
            if (l > this.slowestFastMs) {
                this.slowestFastMs = l;
            }
        }
        this.responses.incrementAndGet();
        return bl;
    }

    boolean allAnswered() {
        return this.expected > 0 && this.responses.get() >= this.expected;
    }

    static int required(int n, int n2) {
        if (n2 <= 0) {
            return Integer.MAX_VALUE;
        }
        return n <= 0 ? n2 : Math.min(n, n2);
    }

    static boolean flagged(int n, int n2, int n3) {
        return n > 0 && n >= ProbeSession.required(n3, n2);
    }

    static String confidence(int n) {
        return n >= 2 ? "Very high" : "High";
    }
}

