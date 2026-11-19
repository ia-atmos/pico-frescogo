#!/bin/sh
# The tests of the flow: runs the port with no display, at fixed frames of
# 16 ms, with scripted keys, and compares the sounds with their times, the
# reports, and `resultados.csv` with what THE ORIGINAL gives for the same
# keys (`tst/fluxo/<name>.out`, written by `oracle/orig.sh`).
#
#   tst/fluxo.sh
#
# A test is `tst/fluxo/<name>.keys`: the number of frames in the first line,
# and the keys (PICO_ATM_KEYS) in the second. An optional `<name>.conf` is
# its configuration, and `<name>.oracle` has the same changes for the
# original. An optional `<name>.radar` has the bytes that the radar sends
# (see `tst/radar.py`): without it, there is no radar.

cd "$(dirname "$0")/.." || exit 1
ATMOS=.deps/env-pico/atmos-pico
OUT=$(mktemp -d)
trap 'rm -rf "$OUT"' EXIT

err=0
for k in tst/fluxo/*.keys; do
    name=$(basename "$k" .keys)
    frames=$(sed -n 1p "$k")
    keys=$(sed -n 2p "$k")
    rel="$OUT/$name"
    rm -f "$OUT/radar.txt"
    conf="tst/fluxo/$name.conf"
    [ -f "$conf" ] || conf="data/conf.atm"
    serial="tst/fluxo/$name.radar"      # the packets of the radar, or no radar
    [ -f "$serial" ] || serial=/nenhuma
    FRESCOGO_TRACE=1 FRESCOGO_SERIAL="$serial" FRESCOGO_RADAR="$OUT/radar.txt" FRESCOGO_TS=TESTE FRESCOGO_REL="$rel" FRESCOGO_CONF="$conf" \
    PICO_TESTS=1 SDL_AUDIODRIVER=dummy PICO_ATM_FRAMES="$frames:16" PICO_ATM_KEYS="$keys" \
        xvfb-run -a -s "-screen 0 1300x900x24" $ATMOS frescogo.atm \
        > "$OUT/$name.log" 2>&1
    if [ $? -ne 0 ]; then
        echo "FAIL $name (exit)"; tail -5 "$OUT/$name.log"; err=1; continue
    fi
    # the sounds, and the readings that the filter of the radar accepts
    grep -a '^\[\|^>>>' "$OUT/$name.log" > "$OUT/$name.out"
    for f in "$rel"/*.txt "$rel"/resultados.csv "$OUT/radar.txt"; do
        [ -f "$f" ] || continue
        echo "=== $(basename "$f")" >> "$OUT/$name.out"
        cat "$f" >> "$OUT/$name.out"
    done
    if cmp -s "$OUT/$name.out" "tst/fluxo/$name.out"; then
        echo "ok   $name"
    else
        echo "FAIL $name"; diff "$OUT/$name.out" "tst/fluxo/$name.out" | head -6; err=1
    fi
done
exit $err
