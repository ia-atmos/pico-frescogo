#!/bin/sh
# The tests of the flow: runs the port with no display, at fixed frames of
# 16 ms, with scripted keys and radars, and compares the sounds with their
# times, the reports, `resultados.csv`, and the log of the radar with the
# expected ones (`tst/fluxo/<name>.out`).
#
#   tst/fluxo.sh            checks
#   tst/fluxo.sh -w         writes the expected outputs (check them by hand)
#
# Up to v4.1.0 (the branch `4.1`), the expected outputs were those of THE
# ORIGINAL over the same keys. Since the rule changed, they are those of the
# port, checked by hand, and each report agrees with the oracle of the score.
#
# A test is `tst/fluxo/<name>.keys`: the number of frames in the first line,
# and the keys (PICO_ATM_KEYS) in the second. An optional `<name>.conf` is
# its configuration. An optional `<name>.radar` has the bytes that the radar
# sends (see `tst/radar.py`): without it, there is no radar.

cd "$(dirname "$0")/.." || exit 1
ATMOS=.deps/env-pico/atmos-pico
OUT=$(mktemp -d)
trap 'rm -rf "$OUT"' EXIT
$ATMOS -o "$OUT/frescogo" frescogo.atm || exit 1      # built once
(cd oracle && unset JAVA_TOOL_OPTIONS && mkdir -p build && javac -nowarn -Xlint:none -d build Oracle.java 2>/dev/null) || exit 1

err=0
for k in tst/fluxo/*.keys; do
    name=$(basename "$k" .keys)
    frames=$(sed -n 1p "$k")
    keys=$(sed -n 2p "$k")
    rel="$OUT/$name"
    rm -f "$OUT/radar.txt"
    conf="tst/fluxo/$name.conf"
    [ -f "$conf" ] || conf="tst/fluxo/padrao.conf"      # see `tst/radar.py`
    serial="tst/fluxo/$name.radar"      # the packets of the radar, or no radar
    [ -f "$serial" ] || serial=/nenhuma
    FRESCOGO_TRACE=1 FRESCOGO_SERIAL="$serial" FRESCOGO_RADAR="$OUT/radar.txt" FRESCOGO_TS=TESTE FRESCOGO_REL="$rel" FRESCOGO_CONF="$conf" \
    PICO_TESTS=1 SDL_AUDIODRIVER=dummy PICO_ATM_FRAMES="$frames:16" PICO_ATM_KEYS="$keys" \
        xvfb-run -a -s "-screen 0 1300x900x24" "$OUT/frescogo" \
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
    # each report, given back to the oracle, must be the same
    for f in "$rel"/frescogo-*.txt; do
        [ -f "$f" ] || continue
        python3 -I tst/relatorio.py "$f" "$conf" > "$OUT/$name.oracle" || { echo "FAIL $name (report)"; err=1; }
        (unset JAVA_TOOL_OPTIONS; java -cp oracle/build Oracle < "$OUT/$name.oracle") 2>/dev/null | grep -v '^calc ' > "$OUT/$name.rel"
        # the oracle has no newline at the end of the file (`io.write(..., "\n")`)
        printf '\n' >> "$OUT/$name.rel"
        if ! cmp -s "$f" "$OUT/$name.rel"; then
            echo "FAIL $name (the oracle reports otherwise)"; diff "$f" "$OUT/$name.rel" | head -6; err=1
        fi
    done
    if [ "$1" = "-w" ]; then
        cp "$OUT/$name.out" "tst/fluxo/$name.out"; echo "wrote $name"
    elif cmp -s "$OUT/$name.out" "tst/fluxo/$name.out"; then
        echo "ok   $name"
    else
        echo "FAIL $name"; diff "$OUT/$name.out" "tst/fluxo/$name.out" | head -6; err=1
    fi
done
exit $err
