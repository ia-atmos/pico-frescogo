#!/bin/sh
# Runs the original (v4.1.0, as it comes in its release package, not changed)
# over the scripted presentations of `tst/fluxo/`, and writes what it gives
# as the expected output of each: the sounds with their times, the reports,
# and `resultados.csv`. With screenshots as the second argument, also saves
# the screen of the original at the frames of `tst/telas.txt`.
#
#   oracle/orig.sh <dir of frescogo-v4.1.0-win64> [telas]
#
# `make orig` fetches the package and calls this. It needs a JDK and
# xvfb-run. The sound and the serial libraries are replaced by stand-ins
# (`oracle/stub/`): no audio device, and no radar.

V=$(cd "$1" && pwd) || exit 1
cd "$(dirname "$0")/.." || exit 1
ROOT=$PWD
unset JAVA_TOOL_OPTIONS
B=$ROOT/oracle/build/orig
mkdir -p "$B"
javac -nowarn -Xlint:none -cp "$V/lib/core.jar:$V/lib/placar.jar" -d "$B" \
    oracle/stub/processing/sound/SoundFile.java \
    oracle/stub/processing/serial/Serial.java \
    oracle/Roteiro.java || exit 1
CP="$B:$V/lib/placar.jar:$V/lib/core.jar"

# A sketch folder for a test: the data of the package, and the `conf.json`
# of the original with the changes of `<name>.oracle` (`conf <key> <value>`).
pasta () {
    rm -rf "$2"; mkdir -p "$2/data"
    for f in "$V"/data/*; do
        [ "$(basename "$f")" = conf.json ] || ln -s "$f" "$2/data/"
    done
    cp placar/conf.json "$2/data/conf.json"
    if [ -f "tst/fluxo/$1.oracle" ]; then
        while read -r _ key val; do
            sed -i -E "s/(\"$key\": *)[^,]*/\1$val/" "$2/data/conf.json"
        done < "tst/fluxo/$1.oracle"
    fi
}

OUT=$(mktemp -d)
trap 'rm -rf "$OUT"' EXIT

if [ "$2" = telas ]; then
    mkdir -p tst/telas/orig
    while read -r name frames shot; do
        case "$name" in ''|'#'*) continue ;; esac
        pasta "$name" "$OUT/$name"
        ( cd "$OUT/$name" && PICO_ATM_FRAMES="$frames:16" \
            PICO_ATM_KEYS="$(sed -n 2p "$ROOT/tst/fluxo/$name.keys")" \
            PICO_ATM_SHOT="$shot:$ROOT/tst/telas/orig/$name-$shot.png" \
            xvfb-run -a -s "-screen 0 1300x900x24" java -cp "$CP" Roteiro > /dev/null 2>&1 )
        echo "tela $name-$shot"
    done < tst/telas.txt
    exit 0
fi

for k in tst/fluxo/*.keys; do
    name=$(basename "$k" .keys)
    pasta "$name" "$OUT/$name"
    ( cd "$OUT/$name" && PICO_ATM_FRAMES="$(sed -n 1p "$ROOT/$k"):16" \
        PICO_ATM_KEYS="$(sed -n 2p "$ROOT/$k")" \
        xvfb-run -a -s "-screen 0 1300x900x24" java -cp "$CP" Roteiro > out.txt 2>&1 )
    {
        grep '^\[' "$OUT/$name/out.txt"
        for f in "$OUT/$name"/relatorios/*.txt "$OUT/$name"/relatorios/resultados.csv; do
            [ -f "$f" ] || continue
            echo "=== $(basename "$f")"
            cat "$f"
        done
    } | sed -E 's/[0-9]{4}(_[0-9]{2}){5}/TESTE/g' > "tst/fluxo/$name.out"
    echo "orig $name"
done
