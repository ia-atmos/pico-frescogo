#!/bin/sh
# Runs the scoreboard (the port to Atmos) from the directory of the
# repository, where `data/` is and where `relatorios/` goes. Builds it the
# first time, and again when its sources change.
cd "$(dirname "$(readlink -f "$0")")/.." || exit 1
make -s frescogo || exit 1
exec ./frescogo
