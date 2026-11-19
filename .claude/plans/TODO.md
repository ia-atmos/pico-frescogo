# TODO

The list of the session `pico-frescogo`: the port of FrescoGO!
(`placar/placar.pde`, v5.3.0, at `1d2edb8`) to Atmos, over pico.

The plan is in the repository of the compiler:
`ia-atmos/lang`, `.claude/plans/261116-pico-frescogo.md`.

## Index of items (`#N`)

Everything this session puts to the author has a number, unique and never
reused (rule of 2026-11-18). The older identifiers (`A.5`, `B.11`, `C.3`,
`F4`) stay valid where they were written, and are given here.

| # | Item | Kind | State | Was |
|---|------|------|-------|-----|
| #1 | Fullscreen: keep reading the size of the display (recommended, as the original), or a fixed logical size of 16:9 | decision | open, asked on 2026-11-18 | |
| #2 | The original divides by zero in the `trinca` mode | defect of the original | reported, not acknowledged | C.1 |
| #3 | The rule of `frescogo/frescogo` against the code: the 6th strongest of 10 continuous hits doubles (the rule says the 5th), and others | remark | reported, not acknowledged | C.3 |
| #4 | Known differences of the port in the flow (the `delay` of the automatic fall, `millis`; Caps Lock was solved by pico) | remark | reported, not acknowledged | C.4 |
| #5 | Remove the `trinca` mode | work, after fidelity | asked by the author | B.10 |
| #6 | Adopt the rule of 6 minutes, with the discount of time per fall | work, after fidelity | asked by the author | B.11 |
| #7 | Windows: the serial line over POSIX leaves it out | decision | open; recommended: accept for now | A.5 |
| #8 | The oracle of the screen: install Processing to run the original under `Xvfb` and compare screenshots | decision | open; recommended: try it during F4 | A.6 |
| #9 | Replace the native literal of the size of the display when pico has the query | work | waits for pico | B.1 |
| #10 | Move `placar/data/` to `data/` and remove the original | work | waits for fidelity | B.2 |
| #11 | F4: the screen, the game mode and the debug mode | work | next | B.5 |
| #12 | F5: the radar: the filter, a scripted source, the serial line over POSIX | work | not started | B.6 |
| #13 | F6: the remaining keys, the launcher, a full presentation by the author with the device | work | not started | B.7 |
| #14 | F7: the same program written for expressiveness | work | after fidelity | B.8 |
| #15 | Design a second environment (`env-posix`) from the case of the serial line | work, with the author | not started | B.9 |

## Decided by the author

| Date | Decision |
|------|----------|
| 2026-11-16 | The serial port: option A. A small library of the port, natives over POSIX (`open`, `termios`, a read that does not block), polled by a task once per frame, as the original does in `draw`. No new environment for now |
| 2026-11-16 | Packages: the session may install whatever it needs in the container |
| 2026-11-18 | The configuration becomes a table of Atmos with comments, `data/conf.atm`, read with `X.load` (no JSON) |
| 2026-11-18 | (no decision needed) The sounds: pico plays the 15 files as they are, the MP3 included |
| 2026-11-17 | The port lives at the root of the repository; the original (`placar/`) is removed later |

## A. Decisions that wait for the author

One at a time, each with a recommended option.

| # | Decision | Recommended |
|---|----------|-------------|
| 5 | Windows: the serial line over POSIX leaves it out | Accept for now; it returns with `env-posix` |
| 6 | The oracle of the screen: install Processing to run the original under `Xvfb` and compare screenshots | Only after the score and the report are identical |

## B. Work known and not done

| # | Work | Note |
|---|------|------|
| 1 | Fullscreen: pico gives no size of the display. `pico-sdl` answered on 2026-11-17 that it will propose a query to the author; meanwhile the port reads the size with a native literal over SDL (`SDL_GetCurrentDisplayMode`), marked TEMPORARY in `frescogo.atm`. Replace it when pico has the query | Waits for pico |
| 2 | Move `placar/data/` to `data/` when the original is removed (the constant `DIR` of `frescogo.atm`) | |
| 5 | F4: the screen, the game mode and the debug mode | |
| 6 | F5: the radar: the filter over a scripted source of packets, then the serial line (option A), tested here over a pseudo-terminal | The real device is tested by the author |
| 7 | F6: the editing of the names, the remaining keys, the launcher | |
| 8 | F7: after full fidelity, the same program written for expressiveness | |
| 9 | Use the case of the serial line to design a second environment (`env-posix`) with the author | Item 57 of the compiler |

For the future, asked by the author on 2026-11-16 (after the faithful port):

| # | Work | Note |
|---|------|------|
| 10 | Remove the `trinca` mode | It divides by zero in the original (C.1) |
| 11 | Adopt the rule of 6 minutes, with the discount of time per fall | The rule is in `frescogo/frescogo`, `README.md`; compared with the code in C.3 |

## C. Reported and not acknowledged

| # | Report | Date |
|---|--------|------|
| 1 | The original divides by zero in the `trinca` mode: `conf_maximas` answers `0` for the side of the pivot, and `_jogo_lado` divides by it (`nrm1 * 100 / maxs`). Found by the oracle. To decide: the port reproduces the failure, or the rule is fixed (and how) | 2026-11-16 |
| 3 | The rule (`frescogo/frescogo`, `README.md`) against the code of v5.3.0 with the default `conf.json`. See the table below | 2026-11-17 |
| 4 | Known differences of the port from the original, in the flow (F3). See the list below | 2026-11-18 |

The differences in the flow (C.4):

- **`delay(1000)` of the automatic fall.** The original blocks the whole sketch for one second, with the previous frame on the screen, and then serves. The port goes on drawing: the state is the same at every moment, the keys wait as they do in the original, but the screen shows the fall one second earlier.
- (solved on 2026-11-19, `env-pico#3`) Caps Lock: pico now gives `caps` in the key events, and the names follow it as in the original.
- **`millis()`** is the clock of pico (the sum of the frames), not the time of the machine: it starts at 0, and the original starts at the time that `setup` took.
- **The keys held for 2 seconds** depend on the repetition of keys of the system, as in the original (`keyPressed` is called at each repetition).
- **`CONF.setString`** of the names changes only the table in memory, as in the original (it never saved `conf.json`).
- (solved on 2026-11-19, `env-pico#3`) The scripted keys of pico now carry the modifiers: the port no longer tracks `Left Ctrl` itself. With that version of pico the scripted keys arrive one frame earlier: the expected traces moved by 16 ms, and nothing else changed.
- Performance: `jogo_calc` at every frame, as the original, costs about 2 ms with 576 hits: fine for 60 frames per second.

The rule against the code (C.3):

| Rule | Code | Same? |
|------|------|-------|
| 6 minutes | `conf_tempo()` = `tempo` + `conf_tregua()` x `conf_quedas()` = 300 + 5 x 12 = 360 s | Yes, in total; the report says `300s` |
| Each fall subtracts 12 s | `conf_quedas()` = 2400 x 300 / 60 / 1000 = 12 s per fall | Yes |
| Balance: at most 30% | `equilibrio` 130; but in the first 100 s of play the cap is relaxed (`pct = 100 - passado`) | Not in the rule |
| A hit scores `vel . (50+vel) / 100` (table) | The same, in integers: 55 km/h gives 57, not 57.75 | Truncated |
| The 150 strongest hits of each athlete | `conf_golpes` = 300 x 60 / 60 / 2 = 150 | Yes, but tied to `tempo`: with `tempo` 360 it would be 180 |
| Continuous: of each 10, the 5th strongest scores double | `kmhs.get(CONF_INTENSIDADE/2)`, index 5 of the sorted list: the **6th** strongest (checked in the oracle: of 91..100, it doubles 95) | **No** |
| Continuous: within 1.5 s of the previous hit of the same athlete | `INSS_DIFF` 1500 | Yes |
| Powerful: the 25 strongest forehands and the 25 strongest backhands of each athlete score double | `conf_maximas` = 300 x 10 / 60 / 2 = 25, each list | Yes, but tied to `tempo`, as the hits |
| Powerful: 20% stronger than the previous hit | Also accepted when more than 1 s passed since the previous hit, or when the previous hit is of the same athlete; and only at or above `minima`, capped at 100 | Wider than the rule |

To adopt the rule of 6 minutes (B.11): `tempo` 360 with no truce (`tregua` 0) and 12 s per fall, with the 150 hits and the 25 powerful ones no longer computed from `tempo`.

## D. Done

| # | Work | Where |
|---|------|-------|
| F0 | The setup (`Makefile`: the compiler and pico at fixed commits), `PORT.md`, a window with the logos, the test of the screen | `Makefile`, `frescogo.atm`, `tst/` |
| F3 | The flow (`go_*`), the keyboard (`keyPressed`, the keys held for 2 s, the names), the sounds, the report, the screenshot and `resultados.csv`. Five scripted presentations, each report given back to the oracle | `frescogo.atm`, `tst/fluxo.sh` |
| F2 | The configuration and the score in Atmos: identical to the oracle in its 12 scripts, and in 40 random ones (not kept). The `1.2` of the powerful hits is a float in Processing: a native literal reproduces it (it differs from a double at 45->54, 50->60, ...) | `jogo.atm`, `tst/jogo.sh` |
| F1 | The oracle of the score: the functions of the score and the report in plain Java, 9 scripts with their expected outputs | `oracle/` |
