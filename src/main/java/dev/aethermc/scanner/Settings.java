/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 */
package dev.aethermc.scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;

final class Settings {
    boolean enabled;
    boolean debug;
    boolean scanOnJoin;
    int joinDelay;
    List<String> skipPrefixes = new ArrayList<String>();
    boolean lpEnabled;
    String controlUrl;
    List<String> localUrls = new ArrayList<String>();
    int requiredFast;
    boolean lpSkipIfLocal;
    int controlWait;
    int controlFastMs;
    int localTimeout;
    int localFastMs;
    boolean detectSpoofers;
    boolean probeEnabled;
    boolean skipIfLocal;
    String publicHost;
    String bindAddress;
    String packPath;
    String prompt;
    int port;
    int loadTimeout;
    int signTimeout;
    int waitOreoMax;
    int threshold;
    boolean passive;
    boolean kbEnabled;
    int kbTimeout;
    final Map<String, Integer> weights = new HashMap<String, Integer>();
    boolean persist;
    boolean reflagOnJoin;
    boolean skipIfKnown;
    boolean clearOnClean;
    boolean flagOreo;
    String modName;
    String opsecModName;
    String category;
    boolean alertStaff;
    boolean kick;
    boolean kickViaOreo;
    String kickMessage;
    List<String> commands = new ArrayList<String>();

    Settings() {
    }

    static Settings load(FileConfiguration fileConfiguration, Logger logger) {
        String[][] stringArrayArray;
        Settings settings = new Settings();
        settings.enabled = fileConfiguration.getBoolean("enabled", true);
        settings.debug = fileConfiguration.getBoolean("debug", false);
        settings.scanOnJoin = fileConfiguration.getBoolean("scan-on-join", true);
        settings.joinDelay = Math.max(1, fileConfiguration.getInt("join-delay-seconds", 3));
        settings.skipPrefixes = fileConfiguration.getStringList("skip-name-prefixes");
        settings.lpEnabled = fileConfiguration.getBoolean("local-url-probe.enabled", true);
        settings.controlUrl = fileConfiguration.getString("local-url-probe.control-url", "http://192.0.2.1/oreo-control.zip");
        ArrayList<String> arrayList = new ArrayList<String>(fileConfiguration.getStringList("local-url-probe.local-urls"));
        if (arrayList.isEmpty()) {
            arrayList.add("http://10.255.255.254/oreo-local.zip");
            arrayList.add("http://192.168.255.254/oreo-local2.zip");
            arrayList.add("http://172.31.255.254/oreo-local3.zip");
        }
        if (arrayList.size() > 4) {
            logger.warning("local-url-probe.local-urls has " + arrayList.size() + " entries; more than 4 gets close to OpSec's own TrackPack alert. Using the first 4.");
            arrayList = new ArrayList(arrayList.subList(0, 4));
        }
        settings.localUrls = arrayList;
        settings.requiredFast = fileConfiguration.getInt("local-url-probe.required-fast", 0);
        settings.lpSkipIfLocal = fileConfiguration.getBoolean("local-url-probe.skip-if-local-server", true);
        settings.controlWait = Math.max(1, fileConfiguration.getInt("local-url-probe.control-wait-seconds", 4));
        settings.controlFastMs = fileConfiguration.getInt("local-url-probe.control-fast-fail-ms", 1500);
        settings.localTimeout = Math.max(1, fileConfiguration.getInt("local-url-probe.local-timeout-seconds", 10));
        settings.localFastMs = fileConfiguration.getInt("local-url-probe.local-fast-fail-ms", 1000);
        settings.detectSpoofers = fileConfiguration.getBoolean("local-url-probe.detect-pack-spoofers", true);
        settings.probeEnabled = fileConfiguration.getBoolean("deep-probe.enabled", true);
        settings.publicHost = fileConfiguration.getString("deep-probe.public-host", "CHANGE_ME");
        settings.port = fileConfiguration.getInt("deep-probe.port", 25580);
        settings.bindAddress = fileConfiguration.getString("deep-probe.bind-address", "0.0.0.0");
        settings.packPath = fileConfiguration.getString("deep-probe.path", "aethermc-deepprobe.zip");
        settings.prompt = fileConfiguration.getString("deep-probe.prompt", "AetherMC connection check");
        settings.loadTimeout = Math.max(5, fileConfiguration.getInt("deep-probe.load-timeout-seconds", 25));
        settings.signTimeout = Math.max(2, fileConfiguration.getInt("deep-probe.sign-reply-timeout-seconds", 5));
        settings.waitOreoMax = Math.max(0, fileConfiguration.getInt("deep-probe.wait-for-oreo-max-seconds", 60));
        settings.skipIfLocal = fileConfiguration.getBoolean("deep-probe.skip-if-local-server", true);
        settings.kbEnabled = fileConfiguration.getBoolean("keybind-probe.enabled", true);
        settings.kbTimeout = Math.max(2, fileConfiguration.getInt("keybind-probe.reply-timeout-seconds", 5));
        settings.threshold = Math.max(1, fileConfiguration.getInt("scoring.flag-threshold", 3));
        settings.passive = fileConfiguration.getBoolean("scoring.passive-checks", true);
        for (String[] stringArray : stringArrayArray = new String[][]{{"local-url-block-probe", "6"}, {"pack-auto-accept", "6"}, {"pack-key-blocked", "3"}, {"vanilla-key-blocked", "3"}, {"brand-vanilla-mod-channels", "2"}, {"loader-channel-mismatch", "2"}, {"fabric-no-channels", "1"}, {"fallback-mangled", "1"}, {"sign-no-reply", "1"}, {"keybind-blocked", "6"}, {"keybind-no-reply", "1"}, {"pack-not-loaded", "1"}, {"pack-status-spoofed", "6"}}) {
            settings.weights.put(stringArray[0], fileConfiguration.getInt("scoring.weights." + stringArray[0], Integer.parseInt(stringArray[1])));
        }
        settings.persist = fileConfiguration.getBoolean("persist.enabled", true);
        settings.reflagOnJoin = fileConfiguration.getBoolean("persist.reflag-on-join", true);
        settings.skipIfKnown = fileConfiguration.getBoolean("persist.skip-probe-if-known", true);
        settings.clearOnClean = fileConfiguration.getBoolean("persist.clear-on-clean-scan", false);
        settings.flagOreo = fileConfiguration.getBoolean("actions.record-oreoscanner-flag", true);
        settings.modName = fileConfiguration.getString("actions.mod-name", "Opsec (Deep-Probe)");
        settings.opsecModName = fileConfiguration.getString("actions.opsec-mod-name", "OpSec");
        settings.category = fileConfiguration.getString("actions.flag-category", "SUSPICIOUS");
        settings.alertStaff = fileConfiguration.getBoolean("actions.alert-staff", true);
        settings.kick = fileConfiguration.getBoolean("actions.kick", false);
        settings.kickViaOreo = fileConfiguration.getBoolean("actions.kick-via-oreoscanner", true);
        settings.kickMessage = fileConfiguration.getString("actions.kick-message", "Disallowed modification detected: Opsec");
        settings.commands = new ArrayList<String>(fileConfiguration.getStringList("actions.console-commands"));
        return settings;
    }

    int weight(String string) {
        return this.weights.getOrDefault(string, 1);
    }

    boolean packConfigured() {
        return this.probeEnabled && this.publicHost != null && !this.publicHost.isBlank() && !this.publicHost.equalsIgnoreCase("CHANGE_ME");
    }

    String packUrl() {
        return this.packBase();
    }

    /** Base URL of the pack server; each scan appends /<dir>/<token>.zip. */
    String packBase() {
        return "http://" + this.publicHost + ":" + this.port;
    }

    String packUrlFor(String dir, String token) {
        return this.packBase() + "/" + dir + "/" + token + ".zip";
    }
}

