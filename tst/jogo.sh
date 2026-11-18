#!/bin/sh
# The test of the score: the port (`jogo.atm`, through `tst/jogo.atm`) must
# print, for each script of `oracle/tst/`, the same as the oracle.

cd "$(dirname "$0")/.." || exit 1
BIN=$(mktemp -d)
trap 'rm -rf "$BIN"' EXIT
.deps/lang/atmos -o "$BIN/jogo" tst/jogo.atm || exit 1

err=0
for f in oracle/tst/*.txt; do
    if "$BIN/jogo" < "$f" 2>&1 | cmp -s - "${f%.txt}.out"; then
        echo "ok   $f"
    else
        echo "FAIL $f"
        err=1
    fi
done
exit $err
