# FrescoGO! in Atmos

The scoreboard of FrescoGO! (`placar/placar.pde`, a Processing sketch,
v5.3.0 at `1d2edb8`) ported to [Atmos](https://github.com/ia-atmos/lang),
over the environment [pico](https://github.com/ia-atmos/env-pico).

**The port is made with AI** (Claude, in Claude Code), under the direction
of the author of FrescoGO! and of Atmos.

It is first one to one with the original, and only then rewritten for
expressiveness. The rules and the operation are those of `README.md`.

## Files

| Path | What |
|------|------|
| `frescogo.atm` | The port: the screen, the keys, the flow |
| `jogo.atm` | The configuration and the score, with no pico: tested against the oracle |
| `data/conf.atm` | The configuration, a table of Atmos with comments (the original has `conf.json`) |
| `placar/` | The original, kept as the reference while the port is checked; it will be removed. The port still reads its data (`placar/data/`) |
| `oracle/` | The functions of the score of the original in plain Java, and scripts of hits with the numbers they give: the tests of the score |
| `tst/` | The tests: the score against the oracle (`jogo.sh`), and the screen at fixed frames, with no display (`run.sh`) |
| `.claude/plans/TODO.md` | What waits for a decision, and what is not done |

## Build and run

```
sudo apt-get install libsdl2-dev libsdl2-image-dev libsdl2-mixer-dev libsdl2-ttf-dev libsdl2-gfx-dev
make            # fetches the compiler and pico at fixed commits, in .deps/
make run
make test       # needs a JDK and xvfb-run
```

## The radar

The radar is read from a serial line, polled once per frame, as the
original does: a small library over POSIX (`open`, `termios`, a read that
does not block). Not done yet.
