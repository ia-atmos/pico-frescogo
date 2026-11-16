# The oracle of the score

`Oracle.java` has the functions of the score of `placar/placar.pde`
(v5.3.0, at `1d2edb8`) in plain Java, with no Processing: `conf_*`,
`_jogo_tempo`, `_jogo_lado`, `_jogo_lado_ins`, `jogo_kmh`, `jogo_equ`,
`jogo_calc`, and the text of the report of `go_termino`. They are copied
from the sketch, between the marks `BEGIN` and `END`.

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
| `conf <key> <value>` | A field of `data/conf.json` (the defaults are those of the file) |
| `radar 0\|1` | The answer of `conf_radar()`: `0` is the manual mode, with the speeds computed from the distance |
| `esquenta 0\|1 [<ms>]` | `ESQUENTA`, and `ESQUENTA_INICIO` |
| `inicio <ms>` | `JOGO_TEMPO_INICIO` |
| `estado <estado> [<jogando>]` | `ESTADO` (`ocioso`, `jogando`, ...) and `ESTADO_JOGANDO` (`sacando`, `jogando`) |
| `now <ms>` | `NOW` |
| `seq` | Starts a sequence |
| `hit <ms> <jog> <kmh> <bak>` | Adds a hit to the last sequence: the time, the athlete (`0`, `1`), the speed (`0` with no radar), and whether it is a backhand |
| `quedas <n>` | `JOGO_QUEDAS` |
| `manual <n>` | `JOGO_QUEDAS_MANUAL` |
| `calc` | Calls `jogo_calc()` and prints one line (below) |
| `report <date>` | Prints the report, with the given date |

The line of `calc`:

```
calc tempo=<passado>,<restante>,<restante_show> total=<JOGO_TOTAL>
     equ=<p0>,<p1> jog0=<the 12 numbers of JOGO_JOGS[0]> jog1=<...>
     snd=<INSS_SOUND_NEW> ins=<the mark of each hit, sequences split by |>
```

The original calls `jogo_calc` at every frame, and the call changes the
marks of the continuous hits (`IDX_INS`), which the next call reads: a
script that calls `calc` between hits covers that.

## Found in the original

- **The `trinca` mode divides by zero.** `conf_maximas` answers `0` for the
  side of the pivot, and `_jogo_lado` ends with `nrm1 * 100 / maxs`: with
  `"trinca": true`, the first `jogo_calc` throws `ArithmeticException`.
  There is no script of `trinca` for this reason.
