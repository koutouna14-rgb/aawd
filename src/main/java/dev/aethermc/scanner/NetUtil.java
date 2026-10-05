/*
 * Decompiled with CFR 0.152.
 */
package dev.aethermc.scanner;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class NetUtil {
    private static final Pattern V4 = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");

    private NetUtil() {
    }

    static boolean isLocalHost(String string) {
        if (string == null) {
            return false;
        }
        String string2 = string.trim().toLowerCase(Locale.ROOT);
        if (string2.startsWith("[") && string2.endsWith("]")) {
            string2 = string2.substring(1, string2.length() - 1);
        }
        if (string2.isEmpty()) {
            return false;
        }
        if (string2.equals("localhost") || string2.endsWith(".localhost") || string2.endsWith(".local") || string2.endsWith(".lan") || string2.endsWith(".home.arpa")) {
            return true;
        }
        Matcher matcher = V4.matcher(string2);
        if (matcher.matches()) {
            int n = Integer.parseInt(matcher.group(1));
            int n2 = Integer.parseInt(matcher.group(2));
            return n == 0 || n == 10 || n == 127 || n == 169 && n2 == 254 || n == 172 && n2 >= 16 && n2 <= 31 || n == 192 && n2 == 168;
        }
        if (string2.indexOf(58) >= 0) {
            return string2.equals("::1") || string2.equals("::") || string2.startsWith("fe80:") || string2.startsWith("fc") || string2.startsWith("fd");
        }
        return false;
    }
}

