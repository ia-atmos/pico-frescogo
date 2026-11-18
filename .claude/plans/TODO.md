# TODO

The list of the session `pico-frescogo`: the port of FrescoGO!
(`placar/placar.pde`, v5.3.0, at `1d2edb8`) to Atmos, over pico.

The plan is in the repository of the compiler:
`ia-atmos/lang`, `.claude/plans/261116-pico-frescogo.md`.

## Decided by the author

| Date | Decision |
|------|----------|
| 2026-11-16 | The serial port: option A. A small library of the port, natives over POSIX (`open`, `termios`, a read that does not block), polled by a task once per frame, as the original does in `draw`. No new environment for now |
| 2026-11-16 | Packages: the session may install whatever it needs in the container |
| 2026-11-18 | The configuration becomes a table of Atmos with comments, `data/conf.atm`, read with `X.load` (no JSON) |
| 2026-11-17 | The port lives at the root of the repository; the original (`placar/`) is removed later |

## A. Decisions that wait for the author

One at a time, each with a recommended option.

| # | Decision | Recommended |
|---|----------|-------------|
| 4 | The sounds: convert the formats that pico does not play (to be checked: WAV, OGG, MP3, AIFF) | Convert, and keep the originals |
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
- **Caps Lock** is not seen in the typing of the names (pico gives `shift` only): a letter is upper case only with SHIFT.
- **`millis()`** is the clock of pico (the sum of the frames), not the time of the machine: it starts at 0, and the original starts at the time that `setup` took.
- **The keys held for 2 seconds** depend on the repetition of keys of the system, as in the original (`keyPressed` is called at each repetition).
- **`CONF.setString`** of the names changes only the table in memory, as in the original (it never saved `conf.json`).
- The scripted keys of pico (`PICO_ATM_KEYS`) have no modifiers: the port also tracks `Left Ctrl` and `Right Ctrl` as keys. To tell `pico-sdl`.
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
