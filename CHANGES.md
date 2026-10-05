# AetherMCScanner 2.0.2 -> 2.1.0 (patched from decompiled source; not compiled here)

Added
- Keybind control probe (pack-free): sign with key.forward / key.jump keybinds + gui.done + missing-key fallback.
  Raw keybind id back => `keybind-blocked` (+6, flags alone, skips the pack prompt). No/empty reply => `keybind-no-reply` (+1).
- Per-scan probe pack: unique URL token, SHA-1, pack UUID and expected lang values (no cache/replay).
- `pack-status-spoofed` (+6): client said "loaded" but never fetched its unique URL.
- `pack-not-loaded` (+1): declined / failed / discarded pack (can never flag alone).
- Duplicate signals are ignored (a player can't be double-counted for the same check).
- `maybeForget` (clear-on-clean-scan) now also requires the keybind probe to have come back clean.

Fixed
- finish(): CFR artifact cast String -> CallSite (would throw ClassCastException at runtime) replaced by a String list.

Config: new `keybind-probe` section and four new `scoring.weights` entries.
Unchanged on purpose: kick behaviour, flag-threshold, brand-vanilla-mod-channels (2), no brand allowlist.

Build fixes (from the first GitHub Actions log)
- Decompiler leftovers that did not compile: duplicate lambda variable names (OpsecProbe, ScannerPlugin.onJoin),
  NBTByte(0) -> NBTByte((byte)0), Object-typed locals in passiveSignals / migrateOldData / tab completion,
  and OpsecProbe.run captured a non-final variable in lambdas.
