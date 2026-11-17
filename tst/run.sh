#!/bin/sh
# The tests of the screen: runs the port with no display (xvfb-run), at fixed
# frames of 16 ms, saves screenshots, and compares them with `tst/shots/`.
#
#   tst/run.sh        checks
#   tst/run.sh -w     writes the expected screenshots again
#
# Each test is a line of `tst/shots.txt`:
#   <name> <frames> <frame of the shot> [<PICO_ATM_KEYS>]

cd "$(dirname "$0")/.." || exit 1
ATMOS=.deps/env-pico/atmos-pico
OUT=$(mktemp -d)
trap 'rm -rf "$OUT"' EXIT

err=0
while read -r name frames shot keys; do
    case "$name" in ''|'#'*) continue ;; esac
    SDL_AUDIODRIVER=dummy PICO_ATM_FRAMES="$frames:16" \
    PICO_ATM_SHOT="$shot:$OUT/$name.png" PICO_ATM_KEYS="$keys" \
        xvfb-run -a -s "-screen 0 1300x900x24" $ATMOS frescogo.atm \
        > "$OUT/$name.log" 2>&1
    if [ $? -ne 0 ]; then
        echo "FAIL $name (exit)"; cat "$OUT/$name.log"; err=1; continue
    fi
    if [ "$1" = "-w" ]; then
        cp "$OUT/$name.png" "tst/shots/$name.png"
    elif cmp -s "$OUT/$name.png" "tst/shots/$name.png"; then
        echo "ok   $name"
    else
        echo "FAIL $name (screen)"; err=1
    fi
done < tst/shots.txt
exit $err
