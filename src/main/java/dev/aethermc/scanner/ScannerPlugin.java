/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.retrooper.packetevents.PacketEvents
 *  com.github.retrooper.packetevents.event.PacketListenerAbstract
 *  com.github.retrooper.packetevents.event.PacketListenerCommon
 *  com.github.retrooper.packetevents.event.PacketListenerPriority
 *  com.github.retrooper.packetevents.event.PacketReceiveEvent
 *  com.github.retrooper.packetevents.protocol.nbt.NBT
 *  com.github.retrooper.packetevents.protocol.nbt.NBTByte
 *  com.github.retrooper.packetevents.protocol.nbt.NBTCompound
 *  com.github.retrooper.packetevents.protocol.nbt.NBTList
 *  com.github.retrooper.packetevents.protocol.nbt.NBTString
 *  com.github.retrooper.packetevents.protocol.nbt.NBTType
 *  com.github.retrooper.packetevents.protocol.packettype.PacketType$Play$Client
 *  com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityTypes
 *  com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState
 *  com.github.retrooper.packetevents.util.Vector3i
 *  com.github.retrooper.packetevents.wrapper.PacketWrapper
 *  com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign
 *  com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange
 *  com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockEntityData
 *  com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow
 *  com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenSignEditor
 *  dev.zvault.oreoscannerpro.ActionType
 *  dev.zvault.oreoscannerpro.ModCategory
 *  dev.zvault.oreoscannerpro.ModConfig
 *  dev.zvault.oreoscannerpro.OreoScannerPro
 *  net.kyori.adventure.text.Component
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.command.TabExecutor
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerResourcePackStatusEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package dev.aethermc.scanner;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTByte;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.nbt.NBTType;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityTypes;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockEntityData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenSignEditor;
import dev.aethermc.scanner.Detection;
import dev.aethermc.scanner.OpsecProbe;
import dev.aethermc.scanner.PackServer;
import dev.aethermc.scanner.Session;
import dev.aethermc.scanner.Settings;
import dev.zvault.oreoscannerpro.ActionType;
import dev.zvault.oreoscannerpro.ModCategory;
import dev.zvault.oreoscannerpro.ModConfig;
import dev.zvault.oreoscannerpro.OreoScannerPro;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class ScannerPlugin
extends JavaPlugin
implements Listener,
TabExecutor {
    private static final String FB_A = "fbA";
    private static final String FB_CONTROL = "fbC";
    private static final String FB_MISSING = "fbM";
    private static final String FB_B = "fbB";
    private static final String CONTROL_KEY = "gui.done";
    private static final String MISSING_KEY = "aethermc.deepprobe.does_not_exist";
    private static final String KB_CONTROL_A = "key.forward";
    private static final String KB_CONTROL_B = "key.jump";
    private volatile Settings s;
    private PackServer packServer;
    private OreoScannerPro oreo;
    private OpsecProbe opsec;
    private PacketListenerAbstract signListener;
    private File flaggedFile;
    private final Map<UUID, Session> sessions = new ConcurrentHashMap<UUID, Session>();
    private final Map<UUID, String> known = new ConcurrentHashMap<UUID, String>();
    private final Set<UUID> reported = ConcurrentHashMap.newKeySet();
    private final Set<UUID> expected = ConcurrentHashMap.newKeySet();

    public boolean isScanning(UUID uUID) {
        return uUID != null && (this.expected.contains(uUID) || this.sessions.containsKey(uUID));
    }

    public void onEnable() {
        this.saveDefaultConfig();
        Plugin plugin = Bukkit.getPluginManager().getPlugin("OreoScannerPro");
        if (!(plugin instanceof OreoScannerPro) || !plugin.isEnabled()) {
            this.getLogger().severe("OreoScannerPro is not enabled. Disabling.");
            Bukkit.getPluginManager().disablePlugin((Plugin)this);
            return;
        }
        this.oreo = (OreoScannerPro)plugin;
        this.flaggedFile = new File(this.getDataFolder(), "flagged.yml");
        this.migrateOldData();
        this.loadKnown();
        this.reloadSettings();
        this.opsec = new OpsecProbe(this, () -> this.s);
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)this.opsec, (Plugin)this);
        if (this.getCommand("deepprobe") != null) {
            this.getCommand("deepprobe").setExecutor((CommandExecutor)this);
            this.getCommand("deepprobe").setTabCompleter((TabCompleter)this);
        }
        if (!this.oreoCategoryOk(this.s.category)) {
            this.getLogger().warning("actions.flag-category '" + this.s.category + "' is not an OreoScanner category, using SUSPICIOUS.");
            this.s.category = "SUSPICIOUS";
        }
        this.signListener = new PacketListenerAbstract(PacketListenerPriority.LOW){

            public void onPacketReceive(PacketReceiveEvent packetReceiveEvent) {
                if (packetReceiveEvent.getPacketType() != PacketType.Play.Client.UPDATE_SIGN) {
                    return;
                }
                Object object = packetReceiveEvent.getPlayer();
                if (!(object instanceof Player)) {
                    return;
                }
                Player player = (Player)object;
                Session session = ScannerPlugin.this.sessions.get(player.getUniqueId());
                if (session == null) {
                    return;
                }
                boolean kbOpen = session.kbPos != null && !session.kbDone.get();
                boolean deepOpen = session.signPos != null && !session.signDone.get();
                if (!kbOpen && !deepOpen) {
                    return;
                }
                WrapperPlayClientUpdateSign wrapperPlayClientUpdateSign = new WrapperPlayClientUpdateSign(packetReceiveEvent);
                Vector3i replyPos = wrapperPlayClientUpdateSign.getBlockPosition();
                if (kbOpen && replyPos.equals((Object)session.kbPos)) {
                    packetReceiveEvent.setCancelled(true);
                    ScannerPlugin.this.onKeybindReply(session, wrapperPlayClientUpdateSign.getTextLines());
                    return;
                }
                if (deepOpen && replyPos.equals((Object)session.signPos)) {
                    packetReceiveEvent.setCancelled(true);
                    ScannerPlugin.this.onSignReply(session, wrapperPlayClientUpdateSign.getTextLines());
                }
            }
        };
        PacketEvents.getAPI().getEventManager().registerListener((PacketListenerCommon)this.signListener);
        this.getLogger().info("AetherMCScanner enabled (local-URL probe + deep-probe + scoring). Flags go to OreoScanner (AetherMC webhook posts them).");
    }

    public void onDisable() {
        if (this.signListener != null) {
            try {
                PacketEvents.getAPI().getEventManager().unregisterListener((PacketListenerCommon)this.signListener);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (this.packServer != null) {
            this.packServer.stop();
        }
        this.sessions.clear();
    }

    private boolean oreoCategoryOk(String string) {
        try {
            ModCategory.valueOf((String)string.toUpperCase(Locale.ROOT));
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    private void reloadSettings() {
        this.reloadConfig();
        this.s = Settings.load(this.getConfig(), this.getLogger());
        if (this.packServer != null) {
            this.packServer.stop();
            this.packServer = null;
        }
        if (!this.s.enabled) {
            return;
        }
        if (!this.s.probeEnabled) {
            this.getLogger().info("deep-probe is disabled in config; local-URL probe and passive scoring still run.");
            return;
        }
        if (!this.s.packConfigured()) {
            this.getLogger().warning("deep-probe.public-host is not set. The pack test is OFF until you set it and /deepprobe reload. Local-URL probe and passive scoring still run.");
            return;
        }
        try {
            this.packServer = new PackServer();
            this.packServer.start(this.s.bindAddress, this.s.port, this.s.packPath, string -> this.debug((String)string));
            this.getLogger().info("Probe packs hosted under " + this.s.packBase() + "/" + this.packServer.dir() + "/ (a unique pack, hash and pack id per scan)");
        }
        catch (Exception exception) {
            this.packServer = null;
            this.getLogger().severe("Could not start the pack web server on " + this.s.bindAddress + ":" + this.s.port + ": " + exception.getMessage() + ". The pack test is OFF.");
        }
    }

    private void debug(String string) {
        if (this.s != null && this.s.debug) {
            this.getLogger().info("[debug] " + string);
        }
    }

    private static String hex(byte[] byArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte by : byArray) {
            stringBuilder.append(String.format("%02x", by));
        }
        return stringBuilder.toString();
    }

    private boolean exempt(Player player) {
        if (player.hasPermission("deepprobe.bypass") || player.hasPermission("opsecaddon.bypass") || player.hasPermission("oreoscannerpro.bypass")) {
            return true;
        }
        for (String string : this.s.skipPrefixes) {
            if (string.isEmpty() || !player.getName().startsWith(string)) continue;
            return true;
        }
        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        Settings settings = this.s;
        if (!settings.enabled || !settings.scanOnJoin) {
            return;
        }
        Player player = playerJoinEvent.getPlayer();
        if (this.exempt(player)) {
            return;
        }
        UUID uUID = player.getUniqueId();
        String string = player.getName();
        if (settings.persist && this.known.containsKey(uUID)) {
            if (!settings.clearOnClean && settings.reflagOnJoin) {
                String[] stringArray = this.splitKnown(this.known.get(uUID));
                Bukkit.getAsyncScheduler().runDelayed((Plugin)this, scheduledTask -> {
                    Player knownPlayer = Bukkit.getPlayer((UUID)uUID);
                    if (knownPlayer != null) {
                        this.applyFlag(knownPlayer, stringArray[0], "Very high", "remembered: " + stringArray[1], "scanner/known-repeat", "Very high confidence | Previously flagged (" + stringArray[1] + "), rejoined", false);
                    }
                }, (long)settings.joinDelay, TimeUnit.SECONDS);
            }
            if (settings.skipIfKnown && !settings.clearOnClean) {
                return;
            }
        }
        this.expected.add(uUID);
        long l = (long)(settings.joinDelay + settings.localTimeout + settings.controlWait + settings.loadTimeout + settings.signTimeout + settings.waitOreoMax) + 20L;
        Bukkit.getAsyncScheduler().runDelayed((Plugin)this, scheduledTask -> this.expected.remove(uUID), l, TimeUnit.SECONDS);
        Bukkit.getAsyncScheduler().runDelayed((Plugin)this, scheduledTask -> this.start(uUID, string), (long)settings.joinDelay, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        UUID uUID = playerQuitEvent.getPlayer().getUniqueId();
        this.sessions.remove(uUID);
        this.expected.remove(uUID);
        this.reported.remove(uUID);
    }

    private boolean joinedLocally(Player player) {
        InetSocketAddress inetSocketAddress = player.getVirtualHost();
        if (inetSocketAddress == null) {
            return false;
        }
        String string = inetSocketAddress.getHostString().toLowerCase(Locale.ROOT);
        return string.equals("localhost") || string.startsWith("127.") || string.startsWith("192.168.") || string.startsWith("10.") || string.matches("172\\.(1[6-9]|2\\d|3[01])\\..*") || string.equals("::1");
    }

    private void start(UUID uUID, String string) {
        Player player = Bukkit.getPlayer((UUID)uUID);
        if (player == null || !this.s.enabled) {
            this.expected.remove(uUID);
            return;
        }
        Session session = new Session(uUID, string);
        if (this.sessions.putIfAbsent(uUID, session) != null) {
            return;
        }
        this.expected.remove(uUID);
        this.debug(string + " scan started");
        this.opsec.run(player, (detection, bl) -> {
            session.localClean = bl;
            this.onLocalResult(session, (Detection)detection);
        });
        long l = (long)(this.s.localTimeout + this.s.controlWait) + 8L;
        Bukkit.getAsyncScheduler().runDelayed((Plugin)this, scheduledTask -> this.onLocalResult(session, null), l, TimeUnit.SECONDS);
    }

    private void onLocalResult(Session session, Detection detection) {
        if (!session.localGate.compareAndSet(false, true)) {
            return;
        }
        if (detection != null) {
            session.opsecDetection = detection;
            session.signal(detection.id(), detection.summary());
            this.debug(session.name + " local-URL probe: " + detection.id() + " - " + detection.summary() + " (skipping pack test)");
            this.finish(session);
            return;
        }
        this.debug(session.name + " local-URL probe: nothing found");
        this.startKeybind(session);
    }

    private void startDeep(Session session) {
        boolean bl;
        UUID uUID = session.uuid;
        Player player = Bukkit.getPlayer((UUID)uUID);
        if (player == null) {
            this.sessions.remove(uUID);
            return;
        }
        boolean bl2 = bl = this.packServer != null && (!this.s.skipIfLocal || !this.joinedLocally(player));
        if (!bl) {
            this.finish(session);
            return;
        }
        player.getScheduler().run((Plugin)this, scheduledTask -> this.sendPack(player, session), () -> this.sessions.remove(uUID));
        Bukkit.getAsyncScheduler().runDelayed((Plugin)this, scheduledTask -> {
            if (!session.packLoaded && !session.finished.get()) {
                this.debug(session.name + " pack step timed out (" + session.packResult + ")");
                this.finish(session);
            }
        }, (long)this.s.loadTimeout, TimeUnit.SECONDS);
    }

    // ---------------------------------------------------------------
    // Keybind control probe (pack-free). A vanilla client resolves a keybind component to the
    // bound key's name ("W"). A shield that blocks keybind resolution returns the raw id instead.
    // ---------------------------------------------------------------
    private static NBTCompound keybindLine(String key) {
        NBTCompound nBTCompound = new NBTCompound();
        nBTCompound.setTag("keybind", (NBT)new NBTString(key));
        return nBTCompound;
    }

    private void startKeybind(Session session) {
        if (!this.s.kbEnabled) {
            this.startDeep(session);
            return;
        }
        if (Bukkit.getPlayer((UUID)session.uuid) == null) {
            this.sessions.remove(session.uuid);
            return;
        }
        this.tryKeybind(session);
    }

    private void tryKeybind(Session session) {
        Player player = Bukkit.getPlayer((UUID)session.uuid);
        if (player == null || session.finished.get()) {
            return;
        }
        if (this.oreo.scans().isInvolved(session.uuid)) {
            long l = System.currentTimeMillis();
            if (session.kbWaitSince == 0L) {
                session.kbWaitSince = l;
            }
            if (l - session.kbWaitSince < (long)this.s.waitOreoMax * 1000L) {
                Bukkit.getAsyncScheduler().runDelayed((Plugin)this, scheduledTask -> this.tryKeybind(session), 1L, TimeUnit.SECONDS);
                return;
            }
            this.debug(session.name + " OreoScanner still busy; skipping keybind probe");
            session.kbResult = "oreo-busy";
            this.afterKeybind(session);
            return;
        }
        player.getScheduler().run((Plugin)this, scheduledTask -> this.sendKeybindSign(player, session), () -> this.finish(session));
    }

    private void sendKeybindSign(Player player, Session session) {
        try {
            Location location = player.getLocation();
            int n = location.getBlockX() + 2;
            int n2 = location.getBlockY() + 3;
            int n3 = location.getBlockZ();
            String restore = player.getWorld().getBlockAt(n, n2, n3).getBlockData().getAsString();
            Vector3i pos = new Vector3i(n, n2, n3);
            session.kbRestore = restore;
            session.kbPos = pos;
            WrappedBlockState wrappedBlockState = WrappedBlockState.getByString((String)"minecraft:oak_sign[rotation=0,waterlogged=false]");
            this.send(player, (PacketWrapper<?>)new WrapperPlayServerBlockChange(pos, wrappedBlockState.getGlobalId()));
            NBTCompound empty = new NBTCompound();
            empty.setTag("text", (NBT)new NBTString(""));
            // 0: keybind control, 1: second keybind control, 2: vanilla translation control, 3: missing key with fallback
            NBTList front = new NBTList(NBTType.COMPOUND, List.of(ScannerPlugin.keybindLine(KB_CONTROL_A), ScannerPlugin.keybindLine(KB_CONTROL_B), ScannerPlugin.line(CONTROL_KEY, FB_CONTROL), ScannerPlugin.line(MISSING_KEY, FB_MISSING)));
            NBTList back = new NBTList(NBTType.COMPOUND, List.of(empty, empty, empty, empty));
            NBTCompound frontText = new NBTCompound();
            frontText.setTag("messages", (NBT)front);
            frontText.setTag("color", (NBT)new NBTString("black"));
            frontText.setTag("has_glowing_text", (NBT)new NBTByte((byte)0));
            NBTCompound backText = new NBTCompound();
            backText.setTag("messages", (NBT)back);
            backText.setTag("color", (NBT)new NBTString("black"));
            backText.setTag("has_glowing_text", (NBT)new NBTByte((byte)0));
            NBTCompound data = new NBTCompound();
            data.setTag("front_text", (NBT)frontText);
            data.setTag("back_text", (NBT)backText);
            data.setTag("is_waxed", (NBT)new NBTByte((byte)0));
            this.send(player, (PacketWrapper<?>)new WrapperPlayServerBlockEntityData(pos, BlockEntityTypes.SIGN, data));
            this.send(player, (PacketWrapper<?>)new WrapperPlayServerOpenSignEditor(pos, true));
            this.send(player, (PacketWrapper<?>)new WrapperPlayServerCloseWindow(0));
            Bukkit.getAsyncScheduler().runDelayed((Plugin)this, scheduledTask -> {
                if (session.kbDone.compareAndSet(false, true)) {
                    this.debug(session.name + " keybind probe: no reply");
                    session.kbResult = "no-reply";
                    session.signal("keybind-no-reply", "Did not answer the keybind probe sign.");
                    this.cleanupKeybind(player, session);
                    this.afterKeybind(session);
                }
            }, (long)this.s.kbTimeout, TimeUnit.SECONDS);
        }
        catch (Throwable throwable) {
            this.getLogger().warning("Keybind probe failed for " + session.name + ": " + throwable.getMessage());
            session.kbResult = "error";
            this.afterKeybind(session);
        }
    }

    private void onKeybindReply(Session session, String[] stringArray) {
        if (!session.kbDone.compareAndSet(false, true)) {
            return;
        }
        String l0 = ScannerPlugin.at(stringArray, 0);
        String l1 = ScannerPlugin.at(stringArray, 1);
        String l2 = ScannerPlugin.at(stringArray, 2);
        String l3 = ScannerPlugin.at(stringArray, 3);
        this.debug(session.name + " keybind reply: [" + l0 + "] [" + l1 + "] [" + l2 + "] [" + l3 + "]");
        boolean allEmpty = l0.isEmpty() && l1.isEmpty() && l2.isEmpty() && l3.isEmpty();
        if (allEmpty) {
            session.kbResult = "empty";
            session.signal("keybind-no-reply", "Keybind probe sign came back empty.");
        } else if (l0.equalsIgnoreCase(KB_CONTROL_A) || l1.equalsIgnoreCase(KB_CONTROL_B)) {
            session.kbResult = "blocked";
            Detection detection = new Detection("keybind-blocked", this.s.opsecModName, "High", "Keybind resolution blocked", "Client returned the raw keybind id ('" + l0 + "' / '" + l1 + "') instead of the bound key name. A vanilla client always resolves these.", true);
            session.opsecDetection = detection;
            session.signal(detection.id(), detection.summary());
        } else {
            session.kbResult = "clean";
            if (l2.equals(FB_CONTROL)) {
                session.signal("vanilla-key-blocked", "Client did not resolve vanilla key 'gui.done'.");
            }
            if (!l3.isEmpty() && !l3.equals(FB_MISSING)) {
                session.signal("fallback-mangled", "Missing key returned '" + l3 + "' instead of its fallback.");
            }
        }
        Player player = Bukkit.getPlayer((UUID)session.uuid);
        if (player != null) {
            this.cleanupKeybind(player, session);
        }
        Bukkit.getAsyncScheduler().runDelayed((Plugin)this, scheduledTask -> this.afterKeybind(session), 200L, TimeUnit.MILLISECONDS);
    }

    private void cleanupKeybind(Player player, Session session) {
        player.getScheduler().run((Plugin)this, scheduledTask -> {
            try {
                if (session.kbPos != null && session.kbRestore != null) {
                    WrappedBlockState wrappedBlockState = WrappedBlockState.getByString((String)session.kbRestore);
                    this.send(player, (PacketWrapper<?>)new WrapperPlayServerBlockChange(session.kbPos, wrappedBlockState.getGlobalId()));
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }, null);
    }

    private void afterKeybind(Session session) {
        if (!session.kbGate.compareAndSet(false, true)) {
            return;
        }
        if (session.opsecDetection != null) {
            this.debug(session.name + " keybind probe: " + session.opsecDetection.id() + " (skipping pack test)");
            this.finish(session);
            return;
        }
        this.startDeep(session);
    }

    private void sendPack(Player player, Session session) {
        try {
            session.packSentAt = System.currentTimeMillis();
            session.packResult = "sent";
            PackServer.Entry entry = this.packServer.create();
            session.pack = entry;
            player.setResourcePack(entry.packId, this.s.packUrlFor(this.packServer.dir(), entry.token), entry.sha1, this.s.prompt.isEmpty() ? null : Component.text((String)this.s.prompt), false);
        }
        catch (Throwable throwable) {
            this.getLogger().warning("Could not send probe pack to " + session.name + ": " + throwable.getMessage());
            session.packResult = "send-failed";
            this.finish(session);
        }
    }

    @EventHandler
    public void onPackStatus(PlayerResourcePackStatusEvent playerResourcePackStatusEvent) {
        Session session = this.sessions.get(playerResourcePackStatusEvent.getPlayer().getUniqueId());
        if (session == null || session.pack == null || !session.pack.packId.equals(playerResourcePackStatusEvent.getID())) {
            return;
        }
        String string = playerResourcePackStatusEvent.getStatus().name();
        long l = System.currentTimeMillis() - session.packSentAt;
        this.debug(session.name + " probe pack " + string + " +" + l + "ms");
        session.packResult = string;
        switch (playerResourcePackStatusEvent.getStatus()) {
            case SUCCESSFULLY_LOADED: {
                session.packLoaded = true;
                if (!this.packServer.wasFetched(session.pack.token)) {
                    session.signal("pack-status-spoofed", "Reported the probe pack as loaded but never downloaded it (unique per-scan URL).");
                }
                Bukkit.getAsyncScheduler().runDelayed((Plugin)this, scheduledTask -> this.trySign(session), 1L, TimeUnit.SECONDS);
                break;
            }
            case DECLINED: 
            case FAILED_DOWNLOAD: 
            case INVALID_URL: 
            case FAILED_RELOAD: 
            case DISCARDED: {
                if (!session.packLoaded) {
                    session.signal("pack-not-loaded", "Probe pack not loaded: " + string);
                }
                this.finish(session);
                break;
            }
        }
    }

    private void trySign(Session session) {
        Player player = Bukkit.getPlayer((UUID)session.uuid);
        if (player == null || session.finished.get()) {
            return;
        }
        if (this.oreo.scans().isInvolved(session.uuid)) {
            long l = System.currentTimeMillis();
            if (session.oreoWaitSince == 0L) {
                session.oreoWaitSince = l;
            }
            if (l - session.oreoWaitSince < (long)this.s.waitOreoMax * 1000L) {
                Bukkit.getAsyncScheduler().runDelayed((Plugin)this, scheduledTask -> this.trySign(session), 1L, TimeUnit.SECONDS);
                return;
            }
            this.debug(session.name + " OreoScanner still busy; skipping sign step");
            this.finish(session);
            return;
        }
        player.getScheduler().run((Plugin)this, scheduledTask -> this.sendSign(player, session), () -> this.finish(session));
    }

    private static NBTCompound line(String string, String string2) {
        NBTCompound nBTCompound = new NBTCompound();
        nBTCompound.setTag("translate", (NBT)new NBTString(string));
        nBTCompound.setTag("fallback", (NBT)new NBTString(string2));
        return nBTCompound;
    }

    private void send(Player player, PacketWrapper<?> packetWrapper) {
        PacketEvents.getAPI().getPlayerManager().sendPacket((Object)player, packetWrapper);
    }

    private void sendSign(Player player, Session session) {
        try {
            Vector3i vector3i;
            Location location = player.getLocation();
            int n = location.getBlockX() + 2;
            int n2 = location.getBlockY() + 3;
            int n3 = location.getBlockZ();
            String string = player.getWorld().getBlockAt(n, n2, n3).getBlockData().getAsString();
            session.signPos = vector3i = new Vector3i(n, n2, n3);
            WrappedBlockState wrappedBlockState = WrappedBlockState.getByString((String)"minecraft:oak_sign[rotation=0,waterlogged=false]");
            this.send(player, (PacketWrapper<?>)new WrapperPlayServerBlockChange(vector3i, wrappedBlockState.getGlobalId()));
            NBTCompound nBTCompound = new NBTCompound();
            nBTCompound.setTag("text", (NBT)new NBTString(""));
            NBTList nBTList = new NBTList(NBTType.COMPOUND, List.of(ScannerPlugin.line("aethermc.deepprobe.canary", FB_A), ScannerPlugin.line(CONTROL_KEY, FB_CONTROL), ScannerPlugin.line(MISSING_KEY, FB_MISSING), ScannerPlugin.line("aethermc.deepprobe.canary2", FB_B)));
            NBTList nBTList2 = new NBTList(NBTType.COMPOUND, List.of(nBTCompound, nBTCompound, nBTCompound, nBTCompound));
            NBTCompound nBTCompound2 = new NBTCompound();
            nBTCompound2.setTag("messages", (NBT)nBTList);
            nBTCompound2.setTag("color", (NBT)new NBTString("black"));
            nBTCompound2.setTag("has_glowing_text", (NBT)new NBTByte((byte)0));
            NBTCompound nBTCompound3 = new NBTCompound();
            nBTCompound3.setTag("messages", (NBT)nBTList2);
            nBTCompound3.setTag("color", (NBT)new NBTString("black"));
            nBTCompound3.setTag("has_glowing_text", (NBT)new NBTByte((byte)0));
            NBTCompound nBTCompound4 = new NBTCompound();
            nBTCompound4.setTag("front_text", (NBT)nBTCompound2);
            nBTCompound4.setTag("back_text", (NBT)nBTCompound3);
            nBTCompound4.setTag("is_waxed", (NBT)new NBTByte((byte)0));
            this.send(player, (PacketWrapper<?>)new WrapperPlayServerBlockEntityData(vector3i, BlockEntityTypes.SIGN, nBTCompound4));
            this.send(player, (PacketWrapper<?>)new WrapperPlayServerOpenSignEditor(vector3i, true));
            this.send(player, (PacketWrapper<?>)new WrapperPlayServerCloseWindow(0));
            session.signRestore = string;
            Bukkit.getAsyncScheduler().runDelayed((Plugin)this, scheduledTask -> {
                if (session.signDone.compareAndSet(false, true)) {
                    this.debug(session.name + " sign probe: no reply");
                    session.signal("sign-no-reply", "Loaded the probe pack but never answered the sign probe.");
                    this.cleanup(player, session);
                    this.finish(session);
                }
            }, (long)this.s.signTimeout, TimeUnit.SECONDS);
        }
        catch (Throwable throwable) {
            this.getLogger().warning("Sign probe failed for " + session.name + ": " + throwable.getMessage());
            this.finish(session);
        }
    }

    private void onSignReply(Session session, String[] stringArray) {
        Player player;
        boolean bl;
        if (!session.signDone.compareAndSet(false, true)) {
            return;
        }
        String string = ScannerPlugin.at(stringArray, 0);
        String string2 = ScannerPlugin.at(stringArray, 1);
        String string3 = ScannerPlugin.at(stringArray, 2);
        String string4 = ScannerPlugin.at(stringArray, 3);
        this.debug(session.name + " sign reply: [" + string + "] [" + string2 + "] [" + string3 + "] [" + string4 + "]");
        boolean bl2 = bl = string.isEmpty() && string2.isEmpty() && string3.isEmpty() && string4.isEmpty();
        if (!bl) {
            if (string2.equals(FB_CONTROL)) {
                session.signal("vanilla-key-blocked", "Client did not resolve vanilla key 'gui.done'.");
            } else if (session.pack == null || !session.pack.valA.equals(string) || !session.pack.valB.equals(string4)) {
                session.signal("pack-key-blocked", "Loaded the probe pack but its translation keys came back as '" + string + "' / '" + string4 + "'.");
            }
            if (!string3.equals(FB_MISSING)) {
                session.signal("fallback-mangled", "Missing key returned '" + string3 + "' instead of its fallback.");
            }
        }
        if ((player = Bukkit.getPlayer((UUID)session.uuid)) != null) {
            this.cleanup(player, session);
        }
        Bukkit.getAsyncScheduler().runDelayed((Plugin)this, scheduledTask -> this.finish(session), 200L, TimeUnit.MILLISECONDS);
    }

    private static String at(String[] stringArray, int n) {
        return stringArray != null && n < stringArray.length && stringArray[n] != null ? stringArray[n].trim() : "";
    }

    private void cleanup(Player player, Session session) {
        player.getScheduler().run((Plugin)this, scheduledTask -> {
            try {
                if (session.signPos != null && session.signRestore != null) {
                    WrappedBlockState wrappedBlockState = WrappedBlockState.getByString((String)session.signRestore);
                    this.send(player, (PacketWrapper<?>)new WrapperPlayServerBlockChange(session.signPos, wrappedBlockState.getGlobalId()));
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                player.removeResourcePack(session.pack == null ? java.util.UUID.randomUUID() : session.pack.packId);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }, null);
    }

    private void passiveSignals(Player player, Session session) {
        String string2 = "";
        try {
            String brandName = player.getClientBrandName();
            string2 = brandName == null ? "" : brandName.toLowerCase(Locale.ROOT);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        List<String> object = new ArrayList<String>();
        try {
            for (String string3 : player.getListeningPluginChannels()) {
                String string4 = string3.toLowerCase(Locale.ROOT);
                if (string4.startsWith("minecraft:")) continue;
                object.add(string4);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        session.passiveText = "brand=" + (string2.isEmpty() ? "unknown" : string2) + ", mod-channels=" + object.size();
        if (!this.s.passive) {
            return;
        }
        boolean bl = string2.equals("vanilla");
        boolean bl2 = string2.contains("fabric") || string2.contains("quilt");
        boolean bl3 = string2.contains("forge");
        if (bl && !object.isEmpty()) {
            session.signal("brand-vanilla-mod-channels", "Brand 'vanilla' but mod channels registered " + String.valueOf(object) + ".");
        }
        boolean bl4 = object.stream().anyMatch(string -> string.startsWith("fabric") || string.startsWith("c:") || string.startsWith("quilt"));
        boolean bl5 = object.stream().anyMatch(string -> string.startsWith("fml") || string.startsWith("forge") || string.startsWith("neoforge"));
        if (bl2 && bl5 || bl3 && bl4) {
            session.signal("loader-channel-mismatch", "Brand '" + string2 + "' does not match the channels it registered.");
        }
        if (string2.equals("fabric") && object.isEmpty()) {
            session.signal("fabric-no-channels", "Brand 'fabric' with no mod channels (channel spoofing, or no Fabric API).");
        }
    }

    private void finish(Session session) {
        if (!session.finished.compareAndSet(false, true)) {
            return;
        }
        this.sessions.remove(session.uuid);
        if (session.pack != null && this.packServer != null) {
            this.packServer.discard(session.pack.token);
        }
        Player player = Bukkit.getPlayer((UUID)session.uuid);
        if (player == null) {
            return;
        }
        if (session.signPos != null && !session.signDone.get()) {
            this.cleanup(player, session);
        } else if (session.packSentAt != 0L) {
            try {
                player.getScheduler().run((Plugin)this, scheduledTask -> player.removeResourcePack(session.pack == null ? java.util.UUID.randomUUID() : session.pack.packId), null);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        this.passiveSignals(player, session);
        int n = 0;
        ArrayList<String> arrayList = new ArrayList<String>();
        ArrayList<String> arrayList2 = new ArrayList<String>();
        for (String[] object : new ArrayList<String[]>(session.signals)) {
            int string2 = this.s.weight(object[0]);
            n += string2;
            arrayList.add(object[0] + "(+" + string2 + "): " + object[1]);
            arrayList2.add(object[0]);
        }
        this.debug(session.name + " finished: total=" + n + " pack=" + session.packResult + " " + String.valueOf(arrayList));
        if (n < this.s.threshold) {
            this.maybeForget(session);
        }
        if (n > 0 && n >= this.s.threshold) {
            String string = n >= this.s.threshold * 2 ? "Very high" : (n > this.s.threshold ? "High" : "Medium");
            Detection detection = session.opsecDetection;
            String string2 = detection != null ? this.s.opsecModName : this.s.modName;
            String string3 = detection != null ? "opsec-detector/" + detection.id() : "aethermc.deepprobe.canary";
            String string4 = detection != null ? detection.summary() : String.join((CharSequence)", ", arrayList2);
            String string5 = (String)string + " confidence | " + string4 + " | " + session.passiveText;
            this.applyFlag(player, string2, string, String.join((CharSequence)" | ", arrayList), string3, string5, true);
        }
    }

    private void maybeForget(Session session) {
        boolean bl;
        Settings settings = this.s;
        if (!(settings.persist && settings.clearOnClean && this.known.containsKey(session.uuid))) {
            return;
        }
        boolean bl2 = session.packSentAt != 0L;
        boolean kbOk = !this.s.kbEnabled || "clean".equals(session.kbResult);
        boolean bl3 = bl = session.localClean && kbOk && (!bl2 || session.packLoaded && session.signDone.get());
        if (!bl) {
            this.debug(session.name + " is remembered, but this scan was not conclusive (local-URL clean=" + session.localClean + ", pack=" + session.packResult + "); keeping the entry.");
            return;
        }
        this.known.remove(session.uuid);
        this.saveKnown();
        this.reported.remove(session.uuid);
        this.getLogger().info(session.name + " passed a full clean scan - removed from the remembered list.");
    }

    private ModCategory category() {
        try {
            return ModCategory.valueOf((String)this.s.category.toUpperCase(Locale.ROOT));
        }
        catch (Exception exception) {
            return ModCategory.SUSPICIOUS;
        }
    }

    private static String clip(String string, int n) {
        return string.length() <= n ? string : string.substring(0, n - 3) + "...";
    }

    private void applyFlag(Player player, String string, String string2, String string3, String string4, String string5, boolean bl) {
        UUID uUID = player.getUniqueId();
        if (!this.reported.add(uUID)) {
            return;
        }
        Settings settings = this.s;
        String string6 = (settings.alertStaff ? "Alert staff" : "Logged") + (settings.kick ? ", kick" : "");
        if (settings.flagOreo) {
            try {
                this.oreo.flags().recordFlag(uUID, player.getName(), string, this.category(), string4, ScannerPlugin.clip(string5 + " | " + string6, 220));
            }
            catch (Throwable throwable) {
                this.getLogger().warning("Could not record OreoScanner flag: " + throwable.getMessage());
            }
        }
        this.getLogger().warning(player.getName() + " flagged: " + string + " [" + string2 + "] - " + string3);
        if (settings.alertStaff) {
            try {
                this.oreo.alerts().sendPrefixed(this.oreo.prefix(), "<#A8C5E5>" + player.getName() + " <#9CA3AF>flagged for <#A8C5E5>" + string + " <#9CA3AF>(" + string2 + " confidence, " + string4.replaceFirst("^.*/", "") + ")");
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (bl && settings.persist) {
            this.known.put(uUID, string + "|" + ScannerPlugin.clip(string3, 180));
            this.saveKnown();
        }
        for (String string7 : settings.commands) {
            String string8 = string7.replace("%player%", player.getName());
            Bukkit.getGlobalRegionScheduler().run((Plugin)this, scheduledTask -> Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)string8));
        }
        if (settings.kick) {
            if (settings.kickViaOreo) {
                this.oreoKick(player, string);
            } else {
                player.getScheduler().run((Plugin)this, scheduledTask -> player.kick((Component)Component.text((String)settings.kickMessage)), null);
            }
        }
    }

    private void oreoKick(Player player, String string) {
        try {
            ModConfig modConfig = null;
            for (ModConfig modConfig2 : this.oreo.mods()) {
                if (!modConfig2.name().equalsIgnoreCase(string) || !modConfig2.actions().contains(ActionType.KICK) && !modConfig2.actions().contains(ActionType.BAN)) continue;
                modConfig = modConfig2;
                break;
            }
            if (modConfig == null) {
                modConfig = new ModConfig(string, this.category(), "aethermc.scanner.none", List.of("aethermc.scanner.none"), EnumSet.of(ActionType.KICK), Map.of(), List.of());
            }
            this.oreo.scans().executeTerminal(player, List.of(modConfig));
        }
        catch (Throwable throwable) {
            this.getLogger().warning("OreoScanner kick failed (" + String.valueOf(throwable) + "), kicking with actions.kick-message instead.");
            player.getScheduler().run((Plugin)this, scheduledTask -> player.kick((Component)Component.text((String)this.s.kickMessage)), null);
        }
    }

    private String[] splitKnown(String string) {
        String string2 = string == null ? "" : string;
        int n = string2.indexOf(124);
        if (n < 0) {
            return new String[]{this.s.modName, string2};
        }
        return new String[]{string2.substring(0, n), string2.substring(n + 1)};
    }

    private void loadKnown() {
        this.known.clear();
        if (!this.flaggedFile.exists()) {
            return;
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File)this.flaggedFile);
        ConfigurationSection configurationSection = yamlConfiguration.getConfigurationSection("players");
        if (configurationSection == null) {
            return;
        }
        for (String string : configurationSection.getKeys(false)) {
            try {
                this.known.put(UUID.fromString(string), configurationSection.getString(string, ""));
            }
            catch (IllegalArgumentException illegalArgumentException) {}
        }
    }

    private synchronized void saveKnown() {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        for (Map.Entry<UUID, String> entry : this.known.entrySet()) {
            yamlConfiguration.set("players." + String.valueOf(entry.getKey()), (Object)entry.getValue());
        }
        try {
            this.getDataFolder().mkdirs();
            yamlConfiguration.save(this.flaggedFile);
        }
        catch (IOException iOException) {
            this.getLogger().warning("Could not save flagged.yml: " + iOException.getMessage());
        }
    }

    private void migrateOldData() {
        if (this.flaggedFile.exists()) {
            return;
        }
        File file = this.getDataFolder().getParentFile();
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        int n = 0;
        try {
            File file2 = new File(file, "AetherMCDeepProbe/flagged.yml");
            if (file2.exists()) {
                ConfigurationSection oldPlayers = YamlConfiguration.loadConfiguration((File)file2).getConfigurationSection("players");
                if (oldPlayers != null) {
                    for (String key : oldPlayers.getKeys(false)) {
                        yamlConfiguration.set("players." + key, (Object)("Opsec (Deep-Probe)|" + oldPlayers.getString(key, "earlier detection")));
                        ++n;
                    }
                }
            }
            File file3 = new File(file, "AetherMCOpsecDetector/flagged.properties");
            if (file3.exists()) {
                Properties properties = new Properties();
                try (FileInputStream in = new FileInputStream(file3)) {
                    properties.load((InputStream)in);
                }
                for (String string : properties.stringPropertyNames()) {
                    String[] stringArray = properties.getProperty(string).split("\\|", 3);
                    yamlConfiguration.set("players." + string, (Object)((stringArray.length > 1 ? stringArray[1] : "OpSec") + "|" + (stringArray.length > 2 ? stringArray[2] : "earlier detection")));
                    ++n;
                }
            }
            if (n > 0) {
                this.getDataFolder().mkdirs();
                yamlConfiguration.save(this.flaggedFile);
                this.getLogger().info("Imported " + n + " remembered player(s) from the old OpSec Detector / DeepProbe plugins.");
            }
        }
        catch (Exception exception) {
            this.getLogger().warning("Could not import old remembered players: " + exception.getMessage());
        }
    }

    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] stringArray2) {
        if (stringArray2.length == 0) {
            commandSender.sendMessage("/deepprobe <player | status <player> | list | forget <player|uuid> | reload>");
            return true;
        }
        switch (stringArray2[0].toLowerCase(Locale.ROOT)) {
            case "reload": {
                this.reloadSettings();
                this.loadKnown();
                commandSender.sendMessage("AetherMCScanner reloaded." + (String)(this.packServer != null ? " Packs hosted under " + this.s.packBase() + "/" + this.packServer.dir() + "/" : " Pack test is off (see console)."));
                break;
            }
            case "list": {
                commandSender.sendMessage("Remembered players: " + this.known.size());
                for (Map.Entry<UUID, String> entry : this.known.entrySet()) {
                    String string2 = Bukkit.getOfflinePlayer((UUID)entry.getKey()).getName();
                    commandSender.sendMessage(" - " + String.valueOf(string2 == null ? (Serializable)entry.getKey() : string2 + " (" + String.valueOf(entry.getKey()) + ")") + "  " + entry.getValue());
                }
                break;
            }
            case "forget": {
                if (stringArray2.length < 2) {
                    commandSender.sendMessage("Usage: /deepprobe forget <player|uuid>");
                    return true;
                }
                UUID uUID = this.resolve(stringArray2[1]);
                if (uUID != null && this.known.remove(uUID) != null) {
                    this.saveKnown();
                    this.reported.remove(uUID);
                    commandSender.sendMessage("Forgot " + stringArray2[1] + ".");
                    break;
                }
                commandSender.sendMessage(stringArray2[1] + " is not remembered.");
                break;
            }
            case "status": {
                if (stringArray2.length < 2) {
                    commandSender.sendMessage("Usage: /deepprobe status <player>");
                    return true;
                }
                UUID uUID = this.resolve(stringArray2[1]);
                Session session = uUID == null ? null : this.sessions.get(uUID);
                Player player = Bukkit.getPlayerExact((String)stringArray2[1]);
                if (player != null) {
                    Session session2 = new Session(player.getUniqueId(), player.getName());
                    this.passiveSignals(player, session2);
                    commandSender.sendMessage(player.getName() + ": " + session2.passiveText + (String)(session2.signals.isEmpty() ? "" : "  passive signals: " + String.valueOf(session2.signals.stream().map(stringArray -> stringArray[0]).toList())));
                }
                if (uUID != null && this.known.containsKey(uUID)) {
                    commandSender.sendMessage(stringArray2[1] + " is remembered: " + this.known.get(uUID));
                }
                if (session != null) {
                    commandSender.sendMessage("Scan running: local-URL probe=" + (this.opsec.isRunning(session.uuid) ? "running" : "done") + ", pack=" + session.packResult + ", sign=" + (session.signDone.get() ? "done" : "pending") + ", signals=" + session.signals.size());
                    break;
                }
                if (uUID != null && this.known.containsKey(uUID)) break;
                commandSender.sendMessage("No scan running and " + stringArray2[1] + " is not remembered.");
                break;
            }
            default: {
                Player player = Bukkit.getPlayerExact((String)stringArray2[0]);
                if (player == null) {
                    commandSender.sendMessage("Player not online.");
                    return true;
                }
                if (this.sessions.containsKey(player.getUniqueId())) {
                    commandSender.sendMessage("A scan is already running for " + player.getName() + ".");
                    return true;
                }
                this.reported.remove(player.getUniqueId());
                commandSender.sendMessage("Scanning " + player.getName() + " (about " + (this.s.localTimeout + this.s.controlWait + this.s.loadTimeout) + "s max)...");
                this.start(player.getUniqueId(), player.getName());
            }
        }
        return true;
    }

    private UUID resolve(String string) {
        try {
            return UUID.fromString(string);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            Player player = Bukkit.getPlayerExact((String)string);
            if (player != null) {
                return player.getUniqueId();
            }
            for (UUID uUID : this.known.keySet()) {
                String string2 = Bukkit.getOfflinePlayer((UUID)uUID).getName();
                if (string2 == null || !string2.equalsIgnoreCase(string)) continue;
                return uUID;
            }
            return null;
        }
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String string, String[] stringArray) {
        ArrayList<String> arrayList;
        block4: {
            block3: {
                arrayList = new ArrayList<String>();
                if (stringArray.length != 1) break block3;
                for (String string2 : List.of("status", "list", "forget", "reload")) {
                    if (!string2.startsWith(stringArray[0].toLowerCase(Locale.ROOT))) continue;
                    arrayList.add(string2);
                }
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (!onlinePlayer.getName().toLowerCase(Locale.ROOT).startsWith(stringArray[0].toLowerCase(Locale.ROOT))) continue;
                    arrayList.add(onlinePlayer.getName());
                }
                break block4;
            }
            if (stringArray.length != 2 || !stringArray[0].equalsIgnoreCase("status") && !stringArray[0].equalsIgnoreCase("forget")) break block4;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.getName().toLowerCase(Locale.ROOT).startsWith(stringArray[1].toLowerCase(Locale.ROOT))) continue;
                arrayList.add(player.getName());
            }
        }
        return arrayList;
    }
}

