package dev.aethermc.scanner;

import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

final class PackServer {
    static final String KEY_A = "aethermc.deepprobe.canary";
    static final String KEY_B = "aethermc.deepprobe.canary2";
    private static final SecureRandom RNG = new SecureRandom();

    /** One probe pack per scan: unique token (URL), unique sha1, unique pack UUID and unique expected values. */
    static final class Entry {
        final String token;
        final UUID packId;
        final String valA;
        final String valB;
        final byte[] zip;
        final byte[] sha1;
        volatile boolean fetched;

        Entry(String token, UUID packId, String valA, String valB, byte[] zip, byte[] sha1) {
            this.token = token;
            this.packId = packId;
            this.valA = valA;
            this.valB = valB;
            this.zip = zip;
            this.sha1 = sha1;
        }
    }

    private final Map<String, Entry> entries = new ConcurrentHashMap<>();
    private HttpServer server;
    private String dir = "aethermc-deepprobe";

    static String randomHex(int bytes) {
        byte[] b = new byte[bytes];
        RNG.nextBytes(b);
        StringBuilder sb = new StringBuilder();
        for (byte x : b) {
            sb.append(String.format("%02x", x));
        }
        return sb.toString();
    }

    /** Builds a fresh pack for one scan. The player never sees the same hash or values twice. */
    Entry create() {
        try {
            String token = randomHex(12);
            String valA = "AMC_" + randomHex(5);
            String valB = "AMC_" + randomHex(5);
            byte[] zip = buildZip(valA, valB, token);
            byte[] sha1 = MessageDigest.getInstance("SHA-1").digest(zip);
            Entry e = new Entry(token, UUID.randomUUID(), valA, valB, zip, sha1);
            entries.put(token, e);
            return e;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    void discard(String token) {
        if (token != null) {
            entries.remove(token);
        }
    }

    boolean wasFetched(String token) {
        Entry e = token == null ? null : entries.get(token);
        return e != null && e.fetched;
    }

    /** Directory part of the configured path ("aethermc-deepprobe.zip" -> "aethermc-deepprobe"). */
    String dir() {
        return dir;
    }

    private static byte[] buildZip(String valA, String valB, String nonce) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            put(zos, "pack.mcmeta", "{\"pack\":{\"description\":\"AetherMC probe " + nonce + "\",\"pack_format\":75,\"min_format\":75,\"max_format\":75}}");
            put(zos, "assets/aethermc/lang/en_us.json", "{\"" + KEY_A + "\":\"" + valA + "\",\"" + KEY_B + "\":\"" + valB + "\"}");
        }
        return out.toByteArray();
    }

    private static void put(ZipOutputStream zos, String name, String body) throws IOException {
        ZipEntry ze = new ZipEntry(name);
        ze.setTime(315532800000L);
        zos.putNextEntry(ze);
        zos.write(body.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    void start(String bind, int port, String path, Consumer<String> log) throws IOException {
        String p = path.startsWith("/") ? path.substring(1) : path;
        if (p.endsWith(".zip")) {
            p = p.substring(0, p.length() - 4);
        }
        this.dir = p.isEmpty() ? "aethermc-deepprobe" : p;
        this.server = HttpServer.create(new InetSocketAddress(bind, port), 0);
        String prefix = "/" + this.dir + "/";
        this.server.createContext(prefix, ex -> {
            try {
                String uri = ex.getRequestURI().getPath();
                String name = uri.startsWith(prefix) ? uri.substring(prefix.length()) : "";
                String token = name.endsWith(".zip") ? name.substring(0, name.length() - 4) : name;
                Entry e = entries.get(token);
                if (e == null) {
                    ex.sendResponseHeaders(404, -1L);
                    return;
                }
                String m = ex.getRequestMethod();
                ex.getResponseHeaders().add("Content-Type", "application/zip");
                if ("HEAD".equalsIgnoreCase(m)) {
                    ex.getResponseHeaders().add("Content-Length", String.valueOf(e.zip.length));
                    ex.sendResponseHeaders(200, -1L);
                } else if ("GET".equalsIgnoreCase(m)) {
                    ex.sendResponseHeaders(200, e.zip.length);
                    ex.getResponseBody().write(e.zip);
                    e.fetched = true;
                    log.accept("pack " + token + " served to " + ex.getRemoteAddress().getAddress().getHostAddress());
                } else {
                    ex.sendResponseHeaders(405, -1L);
                }
            } finally {
                ex.close();
            }
        });
        this.server.setExecutor(Executors.newFixedThreadPool(2, r -> {
            Thread t = new Thread(r, "AetherMCScanner-http");
            t.setDaemon(true);
            return t;
        }));
        this.server.start();
    }

    void stop() {
        if (this.server != null) {
            this.server.stop(0);
            this.server = null;
        }
        entries.clear();
    }
}
