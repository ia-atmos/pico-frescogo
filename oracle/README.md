# The oracle of the score

`Oracle.java` has the functions of the score of `placar/placar.pde`
(v4.1.0, the source of the release package) in plain Java, with no
Processing: `conf_*`, `_jogo_tempo`, `_jogo_lado`, `jogo_kmh`,
`jogo_desconto`, `jogo_equ`, `jogo_calc`, and the text of the report of
`go_termino`. They were copied from the sketch by a script, between the
marks `BEGIN` and `END` (as the branch `4.1` has them). Here they have the
changes of the rule since v4.1.0, written apart from the port, and marked
with their items of `.claude/plans/TODO.md`.

It reads a script and prints what the scoreboard computes. The port to Atmos
must print the same lines for the same scripts: the pairs `tst/*.txt` and
`tst/*.out` are its tests of the score. The tests of the flow also give
each report back to it (`tst/relatorio.py`).

```
./run.sh        # builds, and checks tst/*.txt against tst/*.out
./run.sh -w     # writes tst/*.out again
```

It needs a JDK (`javac`, `java`).

## Scripts

One command per line; `#` starts a comment.

| Command | Effect |
|---------|--------|
| `conf <key> <value>` | A field of `conf.json` (the defaults are those of the file) |
| `radar 0\|1` | The answer of `conf_radar()` (only the report shows it: the speeds are those of the hits) |
| `inicio <ms>` | `JOGO_TEMPO_INICIO` |
| `estado <estado> [<jogando>]` | `ESTADO` (`ocioso`, `jogando`, ...) and `ESTADO_JOGANDO` (`sacando`, `jogando`) |
| `now <ms>` | `NOW` |
| `seq` | Starts a sequence |
| `hit <ms> <jog> <kmh>` | Adds a hit to the last sequence: the time, the athlete (`0`, `1`), and the speed |
| `crono <ms>` | The referee started the clock of the last sequence (a second `CTRL-↑`): without it, its first hit starts it |
| `nome <0\|1\|2> <name>` | The name of an athlete, or of the referee: the rest of the line |
| `descanso <ms>` | `JOGO_DESCANSO_TOTAL` |
| `quedas <n>` | `JOGO_QUEDAS` |
| `manual <n>` | `JOGO_QUEDAS_MANUAL` |
| `calc` | Calls `jogo_calc()` and prints one line (below) |
| `report <date>` | Prints the report, with the given date |

The line of `calc`:

```
calc tempo=<passado>,<restante>,<restante_show> total=<JOGO_TOTAL>
     equ=<p0>,<p1> desconto=<jogo_desconto>
     jog0=<the 5 numbers of JOGO_JOGS[0]>/<conf_ataques(0)> jog1=<...>
```
