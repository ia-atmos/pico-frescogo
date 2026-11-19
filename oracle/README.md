# The oracle of the score

`Oracle.java` has the functions of the score of `placar/placar.pde`
(v4.1.0, the source of the release package) in plain Java, with no
Processing: `conf_*`, `_jogo_tempo`, `_jogo_lado`, `jogo_kmh`,
`jogo_quedas_pct`, `jogo_equ`, `jogo_calc`, and the text of the report of
`go_termino`. They are copied from the sketch by a script, between the
marks `BEGIN` and `END`.

It reads a script and prints what the original computes. The port to Atmos
must print the same lines for the same scripts: the pairs `tst/*.txt` and
`tst/*.out` are its tests of the score.

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
| `radar 0\|1` | The answer of `conf_radar()`: `0` is the manual mode, with the speeds computed from the distance |
| `esquenta 0\|1 [<ms>]` | `ESQUENTA`, and `ESQUENTA_INICIO` |
| `inicio <ms>` | `JOGO_TEMPO_INICIO` |
| `estado <estado> [<jogando>]` | `ESTADO` (`ocioso`, `jogando`, ...) and `ESTADO_JOGANDO` (`sacando`, `jogando`) |
| `now <ms>` | `NOW` |
| `seq` | Starts a sequence |
| `hit <ms> <jog> <kmh>` | Adds a hit to the last sequence: the time, the athlete (`0`, `1`), and the speed (`0` with no radar) |
| `nome <0\|1> <name>` | The name of an athlete |
| `descanso <ms>` | `JOGO_DESCANSO_TOTAL` |
| `quedas <n>` | `JOGO_QUEDAS` |
| `manual <n>` | `JOGO_QUEDAS_MANUAL` |
| `calc` | Calls `jogo_calc()` and prints one line (below) |
| `report <date>` | Prints the report, with the given date |

The line of `calc`:

```
calc tempo=<passado>,<restante>,<restante_show> total=<JOGO_TOTAL>
     equ=<p0>,<p1> pct=<jogo_quedas_pct> aborta=<conf_aborta>
     jog0=<the 5 numbers of JOGO_JOGS[0]>/<conf_ataques(0)> jog1=<...>
```
