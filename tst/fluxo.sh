#!/bin/sh
# The tests of the flow: runs the port with no display, at fixed frames of
# 16 ms, with scripted keys, and compares the trace (sounds and transitions),
# the reports, and `resultados.csv` with the expected ones in `tst/fluxo/`.
# Each report is also given back to the oracle of the score, as a script of
# hits, which must write the same report.
#
#   tst/fluxo.sh        checks
#   tst/fluxo.sh -w     writes the expected outputs again
#
# A test is `tst/fluxo/<name>.keys`: the number of frames in the first line,
# and the keys (PICO_ATM_KEYS) in the second. An optional `<name>.conf` is
# its configuration, and `<name>.oracle` has the same changes for the oracle.

cd "$(dirname "$0")/.." || exit 1
ATMOS=.deps/env-pico/atmos-pico
OUT=$(mktemp -d)
trap 'rm -rf "$OUT"' EXIT
unset JAVA_TOOL_OPTIONS
mkdir -p oracle/build && javac -nowarn -Xlint:none -d oracle/build oracle/Oracle.java || exit 1

err=0
for k in tst/fluxo/*.keys; do
    name=$(basename "$k" .keys)
    frames=$(sed -n 1p "$k")
    keys=$(sed -n 2p "$k")
    rel="$OUT/$name"
    conf="tst/fluxo/$name.conf"
    [ -f "$conf" ] || conf="data/conf.atm"
    FRESCOGO_TRACE=1 FRESCOGO_TS=TESTE FRESCOGO_REL="$rel" FRESCOGO_CONF="$conf" \
    PICO_TESTS=1 SDL_AUDIODRIVER=dummy PICO_ATM_FRAMES="$frames:16" PICO_ATM_KEYS="$keys" \
        xvfb-run -a -s "-screen 0 1300x900x24" $ATMOS frescogo.atm \
        > "$OUT/$name.out" 2>&1
    if [ $? -ne 0 ]; then
        echo "FAIL $name (exit)"; tail -5 "$OUT/$name.out"; err=1; continue
    fi
    # the trace, then each report, then the csv
    for f in "$rel"/*.txt "$rel"/resultados.csv; do
        [ -f "$f" ] || continue
        echo "=== $(basename "$f")" >> "$OUT/$name.out"
        cat "$f" >> "$OUT/$name.out"
    done
    if [ "$1" = "-w" ]; then
        cp "$OUT/$name.out" "tst/fluxo/$name.out"
    elif cmp -s "$OUT/$name.out" "tst/fluxo/$name.out"; then
        echo "ok   $name"
    else
        echo "FAIL $name"; err=1
    fi

    # each report against the oracle: its hits as a script (with no radar,
    # the speeds come from the times), with its falls, rest, and names
    for f in "$rel"/*.txt; do
        [ -f "$f" ] || continue
        {
            echo "radar 0"
            [ -f "tst/fluxo/$name.oracle" ] && cat "tst/fluxo/$name.oracle"
            echo "inicio 0"
            awk '
                /^Descanso:/ { print "descanso", $2*1000 }
                /^Quedas:/ && !q {
                    q = 1
                    if (NF >= 3) {
                        s = $3; gsub(/[()]/, "", s)
                        n = split(s, p, /[+-]/)
                        print "quedas", p[1]
                        print "manual", (index(s,"+") ? p[2] : -p[2])
                    } else {
                        print "quedas", $2
                    }
                }
                / pontos \/ / { i++; nm = $1; sub(/:$/, "", nm); if ($2 ~ /^[0-9]+$/) print "nome", i-1, nm }
                /^SEQU/ { print "seq" }
                /^[0-9][0-9][0-9][0-9][0-9][0-9]   / { print "hit", $1+0, ($2=="->" ? 0 : 1), 0 }
            ' "$f"
            echo "estado terminado"
            echo "calc"
            echo "report TESTE"
        } > "$OUT/$name.script"
        java -cp oracle/build Oracle < "$OUT/$name.script" | grep -v "^calc" > "$OUT/$name.oracle"
        echo >> "$OUT/$name.oracle"
        if cmp -s "$OUT/$name.oracle" "$f"; then
            echo "ok   $name (oracle: $(basename "$f"))"
        else
            echo "FAIL $name (oracle: $(basename "$f"))"; diff "$OUT/$name.oracle" "$f" | head -5; err=1
        fi
    done
done
exit $err
