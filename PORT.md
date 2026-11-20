# FrescoGO! in Atmos

The scoreboard of FrescoGO! (`placar/placar.pde`, a Processing sketch)
ported to [Atmos](https://github.com/ia-atmos/lang),
over the environment [pico](https://github.com/ia-atmos/env-pico).

**The port is made with AI** (Claude, in Claude Code), under the direction
of the author of FrescoGO! and of Atmos.

**The reference is v4.1.0**, the version in real use: the source that comes
in its release package
(<https://github.com/frescogo/frescogo/releases/tag/v4.1.0>), kept here as
`placar/placar.pde`.

There are two versions. The branch **`fidelity`** has the port that is one
to one with the original, function by function. **`master`** has the same
program written for Atmos: the flow of a presentation is a hierarchy of
tasks, each state is a place in the code, and the screen and the radar are
separate from it. Both are checked against the original itself, run over
the same scripted keys. The rule is that of the readme of
`frescogo/frescogo` at the tag `v4.1.0`.

`README.md` and `README-v5.3.0` describe later versions (5.x), which are
not the reference.

## Files

| Path | What |
|------|------|
| `frescogo.atm` | The program: the flow of a presentation as tasks, the keys, the sounds |
| `tela.atm` | The screen, by regions: a function of the state of the game |
| `radar.atm` | The radar: the packets and the filter of the readings |
| `desenho.atm` | The drawing that the screen uses, over pico: boxes, discs, lines, text, images |
| `serial.atm` | The serial line of the radar, over POSIX |
| `jogo.atm` | The configuration and the score, with no pico: tested against the oracle |
| `placar/` | The original: `placar.pde` and `conf.json` of v4.1.0, as the reference |
| `data/` | The images, fonts, and sounds of v4.1.0, and `conf.atm` |
| `oracle/` | The oracles. `Oracle.java`: the functions of the score of the original in plain Java, with scripts of hits. `Roteiro.java`: the original itself (the class of the release package, not changed) run with scripted keys and a fixed clock, which writes the expected outputs of the tests of the flow (`make orig`) |
| `tst/` | The tests: the score against `Oracle.java` (`jogo.sh`), scripted presentations against the original (`fluxo.sh`), and the screen against the screen of the original at the same frames (`telas.sh`), all with no display |
| `.claude/plans/TODO.md` | What waits for a decision, and what is not done |

## Build and run

```
sudo apt-get install libsdl2-dev libsdl2-image-dev libsdl2-mixer-dev libsdl2-ttf-dev libsdl2-gfx-dev
make            # fetches the compiler and pico at fixed commits, in .deps/
make run
make test       # needs a JDK and xvfb-run
```

## Operation

The keys of v4.1.0 (the `README.md` of the root describes 5.x, which has
other keys and holds them for seconds). None is held here.

| Key | What |
|-----|------|
| `CTRL-R` | A new presentation |
| `CTRL-E` | A warm-up: the time runs, and there are no falls |
| `CTRL-↑` or `CTRL-U` | The serve: a sequence starts |
| `←` `→` | A hit of the athlete at the left or at the right (with no radar) |
| `CTRL-↓` or `CTRL-J` | The ball fell |
| `CTRL-BACKSPACE` | Between sequences, or before the first hit: discards the last sequence and its fall |
| `CTRL-=` `CTRL--` | One fall more, or less |
| `CTRL-1` `CTRL-2` `CTRL-0` | Types the name of the athlete at the left, at the right, or of the referee, until `ENTER` (between sequences) |
| `CTRL-I` | The athletes change sides |
| `CTRL-A` | The automatic mode: a fall after 3.5 seconds with no reading of the radar, and the serve one second later |
| `CTRL-S` | Ends the presentation, and writes its report; again, writes it again |
| `CTRL-Q` | Closes the program |

A presentation also ends when the time is over, or at the limit of falls.
The report, a picture of the screen, and a line of `resultados.csv` go to
`relatorios/`; with a radar, its readings go to `radar.txt`.

`launcher/` has an icon for the desktop, which runs `make frescogo` and
`./frescogo`.

## The radar

`serial.atm` is the serial line, a small library over POSIX (the list of
ports, `open` at 9600 baud, a read that blocks until a byte arrives, as the
original reads). With `serial = ""` in `data/conf.atm`, the port is the last
one of the system, as in the original; otherwise, the path given. On a
machine with a serial port and no radar, name a port that does not exist
(`serial = "nenhuma"`), or the program waits for the radar at the first
serve.

It was tried with scripted radars (files of packets, which the original
reads too) and over a pseudo-terminal (`tst/radar-pty.py`), not yet with
the device. Windows is left out.

### A first run with the device

What only the author can do. Each step says what to look for.

1. `make` and `make frescogo`, then `make test` (about 8 minutes): all `ok`.
2. Connect the radar, configured as `radar.md` says. Check its port
   (`ls /dev/ttyUSB*`) and that the user may read it (the group `dialout`).
   Leave `serial = ""`, or write the port in `data/conf.atm`.
3. `./frescogo`. The top of the screen must say `radar` (not `750cm`), and
   the icon of the radar must be at the side of `lado_radar`. If it says
   `750cm`, the terminal has `Erro na comunicação com o radar:` and the port
   that was tried.
4. `CTRL-↑`, and hit a ball towards the radar and away from it. Each hit
   must show its speed at the side of who hit, with its sound, and
   `radar.txt` must have a line `>>> [....] -> NNN <<<` for each. A hit of
   the wrong side means `lado_radar`.
5. Stop hitting. The screen stops with the radar silent only if the radar
   itself stops sending (it sends packets with no speed all the time, so it
   should not): if the screen freezes, tell the session (`#18`).
6. `CTRL-↓`, then `CTRL-A` and `CTRL-↑`: with the automatic mode, 3.5
   seconds with no hit must give a fall, and the serve one second later.
7. `CTRL-S`: `relatorios/` must have the report, the picture, and the line
   of the csv. Compare the hits of the report with what was played.
8. If something differs from what v4.1.0 does with the same radar, the
   branch `fidelity` is the port that is one to one: try the same there
   (`git checkout fidelity && make frescogo`... it has no `frescogo` target:
   `make run`), and tell which of the two differs.
