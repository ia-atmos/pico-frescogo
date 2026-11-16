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

## A. Decisions that wait for the author

One at a time, each with a recommended option.

| # | Decision | Recommended |
|---|----------|-------------|
| 2 | The configuration: a small reader of JSON, or `conf.json` becomes a file of the language | The reader of JSON: it keeps the files of the users |
| 3 | Where the port lives: beside the original (`atmos/`) or in its place | Beside, while fidelity is checked |
| 4 | The sounds: convert the formats that pico does not play (to be checked: WAV, OGG, MP3, AIFF) | Convert, and keep the originals |
| 5 | Windows: the serial line over POSIX leaves it out | Accept for now; it returns with `env-posix` |
| 6 | The oracle of the screen: install Processing to run the original under `Xvfb` and compare screenshots | Only after the score and the report are identical |

## B. Work known and not done

| # | Work | Note |
|---|------|------|
| 1 | F0: readme of the port, the setup that fetches the compiler and `env-pico` at fixed commits and builds `atmos-pico`, a window with the logo | Waits for A.1 |
| 3 | F2: the configuration and the score in Atmos, identical to the oracle | Waits for A.1 and A.2 |
| 4 | F3: the flow of a presentation with the keyboard, the clock, the sounds, the report | |
| 5 | F4: the screen, the game mode and the debug mode | |
| 6 | F5: the radar: the filter over a scripted source of packets, then the serial line (option A), tested here over a pseudo-terminal | The real device is tested by the author |
| 7 | F6: the editing of the names, the remaining keys, the launcher | |
| 8 | F7: after full fidelity, the same program written for expressiveness | |
| 9 | Use the case of the serial line to design a second environment (`env-posix`) with the author | Item 57 of the compiler |

For the future, asked by the author on 2026-11-16 (after the faithful port):

| # | Work | Note |
|---|------|------|
| 10 | Remove the `trinca` mode | It divides by zero in the original (C.1) |
| 11 | Adopt the rule of 6 minutes, with the discount of time per fall | The rule is in the readme, to compare with the code |

## C. Reported and not acknowledged

| # | Report | Date |
|---|--------|------|
| 1 | The original divides by zero in the `trinca` mode: `conf_maximas` answers `0` for the side of the pivot, and `_jogo_lado` divides by it (`nrm1 * 100 / maxs`). Found by the oracle. To decide: the port reproduces the failure, or the rule is fixed (and how) | 2026-11-16 |
| 2 | The container refused to build the compiler (`make -C lang`), by the classifier of permissions, and has no SDL2 development packages: nothing in Atmos was run yet | 2026-11-16 |

## D. Done

| # | Work | Where |
|---|------|-------|
| F1 | The oracle of the score: the functions of the score and the report in plain Java, 9 scripts with their expected outputs | `oracle/` |
