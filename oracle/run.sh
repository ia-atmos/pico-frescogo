#!/bin/sh
# Builds the oracle and checks each script of `tst/` against its expected
# output. With `-w`, writes the expected outputs instead of checking them.
#
#   ./run.sh        checks tst/*.txt against tst/*.out
#   ./run.sh -w     writes tst/*.out
#
# The same pairs are the tests of the port: its score must print the same
# lines for the same scripts.

cd "$(dirname "$0")" || exit 1
unset JAVA_TOOL_OPTIONS
mkdir -p build
javac -nowarn -Xlint:none -d build Oracle.java || exit 1

err=0
for f in tst/*.txt; do
    out="${f%.txt}.out"
    if [ "$1" = "-w" ]; then
        java -cp build Oracle < "$f" > "$out" || exit 1
    elif java -cp build Oracle < "$f" | cmp -s - "$out"; then
        echo "ok   $f"
    else
        echo "FAIL $f"
        err=1
    fi
done
exit $err
