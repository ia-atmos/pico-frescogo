#!/bin/sh
# The tests of the screen: runs the port over the scripted presentations of
# `tst/telas.txt`, with no display, and compares each screenshot with the
# screen of THE ORIGINAL at the same frame (`tst/telas/orig/`, written by
# `oracle/orig.sh <package> telas`).
#
# The two are not equal pixel by pixel: the smoothing of the edges, the
# scaling of the images, and the sizes of the text (whole pixels in pico)
# differ. Today the differences are of 1.3 to 1.9. A screen passes if the mean difference of
# its pixels (0 to 255) is at most LIMITE.
#
#   tst/telas.sh            checks
#   tst/telas.sh <dir>      also keeps the screenshots of the port in <dir>

LIMITE=2.5

cd "$(dirname "$0")/.." || exit 1
ATMOS=.deps/env-pico/atmos-pico
OUT=$(mktemp -d)
trap 'rm -rf "$OUT"' EXIT
$ATMOS -o "$OUT/frescogo" frescogo.atm || exit 1      # built once
[ -n "$1" ] && mkdir -p "$1"

err=0
while read -r name frames shot; do
    case "$name" in ''|'#'*) continue ;; esac
    conf="tst/fluxo/$name.conf"
    [ -f "$conf" ] || conf="data/conf.atm"
    serial="tst/fluxo/$name.radar"      # the packets of the radar, or no radar
    [ -f "$serial" ] || serial=/nenhuma
    FRESCOGO_SERIAL="$serial" FRESCOGO_RADAR="$OUT/radar.txt" FRESCOGO_TS=TESTE FRESCOGO_REL="$OUT/rel" FRESCOGO_CONF="$conf" \
    PICO_TESTS=1 SDL_AUDIODRIVER=dummy PICO_ATM_FRAMES="$frames:16" \
    PICO_ATM_KEYS="$(sed -n 2p "tst/fluxo/$name.keys")" PICO_ATM_SHOT="$shot:$OUT/$name-$shot.png" \
        xvfb-run -a -s "-screen 0 1300x900x24" "$OUT/frescogo" > "$OUT/log" 2>&1
    if [ $? -ne 0 ] || [ ! -f "$OUT/$name-$shot.png" ]; then
        echo "FAIL $name-$shot (exit)"; tail -5 "$OUT/log"; err=1; continue
    fi
    [ -n "$1" ] && cp "$OUT/$name-$shot.png" "$1/"
    d=$(python3 -I - "tst/telas/orig/$name-$shot.png" "$OUT/$name-$shot.png" <<'PY'
import sys
from PIL import Image, ImageChops
a = Image.open(sys.argv[1]).convert('RGB')
b = Image.open(sys.argv[2]).convert('RGB')
if a.size != b.size:
    print("999")
else:
    h = ImageChops.difference(a, b).convert('L').histogram()
    print("%.2f" % (sum(i*c for i, c in enumerate(h)) / (a.size[0]*a.size[1])))
PY
    )
    if [ "$(echo "$d <= $LIMITE" | bc)" = 1 ]; then
        echo "ok   $name-$shot ($d)"
    else
        echo "FAIL $name-$shot ($d > $LIMITE)"; err=1
    fi
done < tst/telas.txt
exit $err
