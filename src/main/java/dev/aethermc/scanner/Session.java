/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.retrooper.packetevents.util.Vector3i
 */
package dev.aethermc.scanner;

import com.github.retrooper.packetevents.util.Vector3i;
import dev.aethermc.scanner.Detection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

final class Session {
    final UUID uuid;
    final String name;
    volatile long packSentAt;
    volatile boolean packLoaded;
    volatile String packResult = "not-sent";
    volatile Vector3i signPos;
    volatile String signRestore;
    final AtomicBoolean signDone = new AtomicBoolean();
    final AtomicBoolean finished = new AtomicBoolean();
    final AtomicBoolean localGate = new AtomicBoolean();
    volatile long oreoWaitSince;
    volatile Detection opsecDetection;
    volatile boolean localClean;
    // keybind control probe (pack-free)
    volatile Vector3i kbPos;
    volatile String kbRestore;
    final AtomicBoolean kbDone = new AtomicBoolean();
    final AtomicBoolean kbGate = new AtomicBoolean();
    volatile long kbWaitSince;
    volatile String kbResult = "not-run";
    // per-session probe pack
    volatile PackServer.Entry pack;
    volatile String passiveText = "";
    final List<String[]> signals = Collections.synchronizedList(new ArrayList());

    Session(UUID uUID, String string) {
        this.uuid = uUID;
        this.name = string;
    }

    void signal(String string, String string2) {
        synchronized (this.signals) {
            for (String[] existing : this.signals) {
                if (existing[0].equals(string)) {
                    return;
                }
            }
            this.signals.add(new String[]{string, string2});
        }
    }
}

