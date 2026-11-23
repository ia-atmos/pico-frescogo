#!/usr/bin/env python3
# Gives a report of the port back to the oracle of the score: reads the
# report, and writes the script of `oracle/` that has the same presentation.
# `tst/fluxo.sh` gives it to the oracle, and compares the two reports.
#
#   tst/relatorio.py <report> <conf.atm>
#
# The configuration is that of the test, for the fields of the score.

import re, sys

rel  = open(sys.argv[1], encoding="utf-8").read()
conf = open(sys.argv[2], encoding="utf-8").read()

# the fields of the score: their names in the oracle, and their places
CAMPOS = {
    "tempo":      ("tempos", "jogo"),
    "queda":      ("tempos", "queda"),
    "esquenta":   ("tempos", "esquenta"),
    "descanso":   ("tempos", "descanso"),
    "ataques":    ("pontos", "golpes"),
    "minima":     ("pontos", "minima"),
    "equilibrio": ("pontos", "equilibrio"),
    "saque":      ("radar", "saque"),
}
out = []
for k, (sec, campo) in CAMPOS.items():
    a = conf.index(f"\n    {sec} = [")
    m = re.search(rf"\n        {campo}\s*=\s*(\d+),", conf[a:conf.index("\n    ],", a)])
    out.append(f"conf {k} {m.group(1)}")

ver = re.search(r"^Versão:\s+v\S+ / (?:dupla / )?(.+) / \d+s$", rel, re.M)
out.append("radar " + ("1" if ver.group(1) == "radar" else "0"))
out.append("inicio 0")

nomes = re.findall(r"^(.*?):\s+\d{5} pontos / ", rel, re.M)
for i, n in enumerate(nomes):
    out.append(f"nome {i} {n}")

m = re.search(r"^Quedas:\s+(-?\d+)(?: \((\d+)([+-]\d+)\))?$", rel, re.M)
if m.group(2):
    out += [f"quedas {m.group(2)}", f"manual {int(m.group(3))}"]
else:
    out.append(f"quedas {m.group(1)}")
out.append("descanso %d" % (int(re.search(r"^Descanso:\s+(\d+)$", rel, re.M).group(1)) * 1000))

for s in re.split(r"^SEQUÊNCIA \d+$", rel, flags=re.M)[1:]:
    out.append("seq")
    for l in s.split("\n"):
        m = re.match(r"^(\d{6})   cronômetro pelo árbitro$", l)
        if m:
            out.append(f"crono {int(m.group(1))}")
        m = re.match(r"^(\d{6})   (->|<-)   (\d{3})$", l)
        if m:
            out.append(f"hit {int(m.group(1))} {0 if m.group(2) == '->' else 1} {int(m.group(3))}")

out.append("estado ocioso")
out.append("calc")
ts = re.search(r"^Data:\s+(\S+)$", rel, re.M).group(1)
out.append(f"report {ts}")
print("\n".join(out))
