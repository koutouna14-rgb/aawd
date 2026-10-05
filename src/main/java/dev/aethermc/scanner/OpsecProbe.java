/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.threadedregions.scheduler.ScheduledTask
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerResourcePackStatusEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package dev.aethermc.scanner;

import dev.aethermc.scanner.Detection;
import dev.aethermc.scanner.NetUtil;
import dev.aethermc.scanner.ProbeSession;
import dev.aethermc.scanner.Settings;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

final class OpsecProbe
implements Listener {
    private final JavaPlugin plugin;
    private final Supplier<Settings> cfgSupplier;
    private final Map<UUID, ProbeSession> sessions = new ConcurrentHashMap<UUID, ProbeSession>();
    private final Map<UUID, BiConsumer<Detection, Boolean>> callbacks = new ConcurrentHashMap<UUID, BiConsumer<Detection, Boolean>>();

    OpsecProbe(JavaPlugin javaPlugin, Supplier<Settings> supplier) {
        this.plugin = javaPlugin;
        this.cfgSupplier = supplier;
    }

    private Settings cfg() {
        return this.cfgSupplier.get();
    }

    boolean isRunning(UUID uUID) {
        return this.sessions.containsKey(uUID);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        UUID uUID = playerQuitEvent.getPlayer().getUniqueId();
        this.sessions.remove(uUID);
        this.callbacks.remove(uUID);
    }

    @EventHandler
    public void onPackStatus(PlayerResourcePackStatusEvent playerResourcePackStatusEvent) {
        Player player = playerResourcePackStatusEvent.getPlayer();
        Settings settings = this.cfg();
        ProbeSession probeSession = this.sessions.get(player.getUniqueId());
        UUID uUID = playerResourcePackStatusEvent.getID();
        String string = playerResourcePackStatusEvent.getStatus().name();
        if (settings.debug) {
            String string2;
            if (probeSession == null) {
                string2 = "no-session";
            } else if (uUID == null) {
                string2 = "no-id";
            } else if (uUID.equals(probeSession.controlId)) {
                string2 = "control";
            } else {
                string2 = probeSession.isLocal(uUID) ? "local" : "other";
            }
            long l = probeSession == null ? -1L : (string2.equals("control") ? OpsecProbe.ms(probeSession.controlSent) : OpsecProbe.ms(probeSession.localSent));
            this.plugin.getLogger().info("[debug] " + player.getName() + " pack " + string2 + " " + string + (String)(l >= 0L ? " +" + l + "ms" : "") + " ping=" + player.getPing() + "ms");
        }
        if (probeSession == null || probeSession.done || uUID == null) {
            return;
        }
        boolean bl = uUID.equals(probeSession.controlId);
        boolean bl2 = probeSession.isLocal(uUID);
        if (!bl && !bl2) {
            return;
        }
        switch (string) {
            case "ACCEPTED": {
                break;
            }
            case "DECLINED": 
            case "DISCARDED": 
            case "INVALID_URL": 
            case "FAILED_RELOAD": {
                this.finish(player, probeSession);
                break;
            }
            case "SUCCESSFULLY_LOADED": 
            case "DOWNLOADED": {
                if (settings.detectSpoofers) {
                    probeSession.result = new Detection("pack-auto-accept", "Resource pack spoofer", "Medium", "Reported " + string + " for a pack on an unreachable address", "Client reported " + string + " for a pack hosted on an unreachable address (" + (bl ? "control" : "local") + " probe). Real clients cannot do that.", true);
                }
                this.finish(player, probeSession);
                break;
            }
            case "FAILED_DOWNLOAD": {
                if (bl) {
                    if (!probeSession.controlStarted || OpsecProbe.ms(probeSession.controlSent) >= (long)settings.controlFastMs) break;
                    probeSession.controlFast = true;
                    break;
                }
                if (!probeSession.localStarted) break;
                long l = (long)settings.localFastMs + 2L * (long)Math.max(0, player.getPing());
                probeSession.recordLocalFailure(OpsecProbe.ms(probeSession.localSent), l);
                if (!probeSession.allAnswered()) break;
                this.localsDone(player, probeSession);
                break;
            }
        }
    }

    void run(Player player, BiConsumer<Detection, Boolean> biConsumer) {
        Settings settings = this.cfg();
        UUID uUID = player.getUniqueId();
        if (!settings.lpEnabled || settings.localUrls.isEmpty() || this.sessions.containsKey(uUID)) {
            biConsumer.accept(null, false);
            return;
        }
        if (settings.lpSkipIfLocal) {
            String host = OpsecProbe.virtualHost(player);
            if (NetUtil.isLocalHost(host)) {
                this.plugin.getLogger().info("Skipping local-URL probe for " + player.getName() + ": joined through local address " + host);
                biConsumer.accept(null, false);
                return;
            }
        }
        final ProbeSession ps = new ProbeSession(settings.localUrls.size());
        this.sessions.put(uUID, ps);
        this.callbacks.put(uUID, biConsumer);
        player.getScheduler().run((Plugin)this.plugin, task -> this.lambda$run$0(ps, player, settings, task), () -> {
            this.sessions.remove(uUID);
            this.fire(uUID, null, false);
        });
        Bukkit.getAsyncScheduler().runDelayed((Plugin)this.plugin, task -> this.lambda$run$2(uUID, ps, task), (long)settings.localTimeout, TimeUnit.SECONDS);
    }

    private void localsDone(Player player, ProbeSession probeSession) {
        int n;
        if (probeSession.done || !probeSession.localsDone.compareAndSet(false, true)) {
            return;
        }
        Settings settings = this.cfg();
        int n2 = probeSession.fast.get();
        if (!ProbeSession.flagged(n2, n = probeSession.expected, settings.requiredFast)) {
            this.plugin.getLogger().info(player.getName() + ": local-URL probe clean (" + n2 + "/" + n + " instant failures, " + probeSession.responses.get() + " answered" + (String)(probeSession.timings.isEmpty() ? "" : ": " + String.join((CharSequence)", ", probeSession.timings)) + ").");
            probeSession.clean = true;
            this.finish(player, probeSession);
            return;
        }
        UUID uUID = player.getUniqueId();
        probeSession.controlStarted = true;
        player.getScheduler().run((Plugin)this.plugin, scheduledTask -> {
            probeSession.controlSent = System.nanoTime();
            this.sendPack(player, probeSession.controlId, settings.controlUrl);
        }, () -> {
            this.sessions.remove(uUID);
            this.fire(uUID, null, false);
        });
        Bukkit.getAsyncScheduler().runDelayed((Plugin)this.plugin, scheduledTask -> {
            Player onlinePlayer = Bukkit.getPlayer((UUID)uUID);
            if (onlinePlayer == null) {
                this.sessions.remove(uUID);
                this.callbacks.remove(uUID);
            } else if (!probeSession.done) {
                this.finalizeProbe(onlinePlayer, probeSession);
            }
        }, (long)settings.controlWait, TimeUnit.SECONDS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void finalizeProbe(Player player, ProbeSession probeSession) {
        Settings settings = this.cfg();
        ProbeSession probeSession2 = probeSession;
        synchronized (probeSession2) {
            if (probeSession.done) {
                return;
            }
            int n = probeSession.fast.get();
            int n2 = probeSession.expected;
            if (probeSession.controlFast) {
                this.plugin.getLogger().info(player.getName() + ": local packs failed instantly (" + n + "/" + n2 + ") but so did the non-local control pack, so this network just rejects black-hole addresses. Not flagged.");
            } else {
                String string = String.join((CharSequence)", ", probeSession.timings);
                probeSession.result = new Detection("local-url-block-probe", settings.opsecModName, ProbeSession.confidence(n), "Blocked " + n + "/" + n2 + " local-URL packs instantly (slowest " + probeSession.slowestFastMs + " ms)", n + "/" + n2 + " local-URL packs failed instantly (" + string + "; limit " + settings.localFastMs + " ms + 2x ping) while a non-local control pack was still pending after " + settings.controlWait + "s. Matches OpSec 'Block Local URLs' (ExploitPreventer behaves the same way).", true);
            }
            this.finish(player, probeSession);
        }
    }

    private void sendPack(Player player, UUID uUID, String string) {
        player.addResourcePack(uUID, string, new byte[20], null, false);
    }

    private void finish(Player player, ProbeSession probeSession) {
        if (probeSession.done) {
            return;
        }
        probeSession.done = true;
        UUID uUID = player.getUniqueId();
        this.sessions.remove(uUID);
        player.getScheduler().run((Plugin)this.plugin, scheduledTask -> {
            player.removeResourcePack(probeSession.controlId);
            for (UUID localId : probeSession.localIds) {
                player.removeResourcePack(localId);
            }
        }, null);
        this.fire(uUID, probeSession.result, probeSession.clean);
    }

    private void fire(UUID uUID, Detection detection, boolean bl) {
        BiConsumer<Detection, Boolean> biConsumer = this.callbacks.remove(uUID);
        if (biConsumer != null) {
            biConsumer.accept(detection, bl);
        }
    }

    private static long ms(long l) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - l);
    }

    private static String virtualHost(Player player) {
        try {
            InetSocketAddress inetSocketAddress = player.getVirtualHost();
            if (inetSocketAddress != null) {
                return inetSocketAddress.getHostString();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private /* synthetic */ void lambda$run$2(UUID uUID, ProbeSession probeSession, ScheduledTask scheduledTask) {
        Player player = Bukkit.getPlayer((UUID)uUID);
        if (player == null) {
            this.sessions.remove(uUID);
            this.callbacks.remove(uUID);
        } else if (!probeSession.done) {
            this.localsDone(player, probeSession);
        }
    }

    private /* synthetic */ void lambda$run$0(ProbeSession probeSession, Player player, Settings settings, ScheduledTask scheduledTask) {
        probeSession.expected = probeSession.localIds.size();
        probeSession.localStarted = true;
        probeSession.localSent = System.nanoTime();
        for (int i = 0; i < probeSession.localIds.size(); ++i) {
            this.sendPack(player, probeSession.localIds.get(i), settings.localUrls.get(i));
        }
    }
}

