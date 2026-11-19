# FrescoGO! in Atmos

The scoreboard of FrescoGO! (`placar/placar.pde`, a Processing sketch)
ported to [Atmos](https://github.com/ia-atmos/lang),
over the environment [pico](https://github.com/ia-atmos/env-pico).

**The port is made with AI** (Claude, in Claude Code), under the direction
of the author of FrescoGO! and of Atmos.

**The reference is v4.1.0**, the version in real use: the source that comes
in its release package
(<https://github.com/frescogo/frescogo/releases/tag/v4.1.0>), kept here as
`placar/placar.pde`. The port is first one to one with it, exact, and only
then rewritten for expressiveness. The rule is that of the readme of
`frescogo/frescogo` at the tag `v4.1.0`.

`README.md` and `README-v5.3.0` describe later versions (5.x), which are
not the reference.

## Files

| Path | What |
|------|------|
| `frescogo.atm` | The port: the screen, the keys, the flow |
| `jogo.atm` | The configuration and the score, with no pico: tested against the oracle |
| `placar/` | The original: `placar.pde` and `conf.json` of v4.1.0, as the reference |
| `data/` | The images, fonts, and sounds of v4.1.0, and `conf.atm` |
| `oracle/` | The oracles. `Oracle.java`: the functions of the score of the original in plain Java, with scripts of hits. `Roteiro.java`: the original itself (the class of the release package, not changed) run with scripted keys and a fixed clock, which writes the expected outputs of the tests of the flow (`make orig`) |
| `tst/` | The tests: the score against `Oracle.java` (`jogo.sh`), scripted presentations against the original (`fluxo.sh`), and the screen at fixed frames (`run.sh`), all with no display |
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
