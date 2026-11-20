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

## The radar

`serial.atm` is the serial line, a small library over POSIX (the list of
ports, `open` at 9600 baud, a read that blocks until a byte arrives, as the
original reads). With `serial = ""` in `data/conf.atm`, the port is the last
one of the system, as in the original; otherwise, the path given.

It was tried with scripted radars (files of packets, which the original
reads too) and over a pseudo-terminal (`tst/radar-pty.py`), not yet with
the device. Windows is left out.
