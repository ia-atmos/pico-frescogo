# TODO

The list of the session `pico-frescogo`: the port of FrescoGO!
(`placar/placar.pde`) to Atmos, over pico. **The reference is v4.1.0**
(decided by the author on 2026-11-19): the source of the release package
`frescogo-v4.1.0-win64.zip`. What is written below about v5.3.0 (the items
`#2`, `#3`, the sections C.1, C.3, C.4, the marks F1 to F3 as first done)
is history: the oracle, the score, and the flow were redone over v4.1.0.

The plan is in the repository of the compiler:
`ia-atmos/lang`, `.claude/plans/261116-pico-frescogo.md`.

## Index of items (`#N`)

Everything this session puts to the author has a number, unique and never
reused (rule of 2026-11-18). The older identifiers (`A.5`, `B.11`, `C.3`,
`F4`) stay valid where they were written, and are given here.

| # | Item | Kind | State | Was |
|---|------|------|-------|-----|
| #1 | Fullscreen: the size of the display, as the original (decided by the author in `env-pico`, its `#1c`: in fullscreen the window layer follows the display) | decision | closed on 2026-11-19 | |
| #2 | The original (v5.3.0) divides by zero in the `trinca` mode | defect of v5.3.0 | closed on 2026-11-19: the author allowed removing it; v4.1.0, the reference, has no such defect, and its `trinca` is kept one to one | C.1 |
| #3 | The rule of the readme against the code of v5.3.0 | remark | closed on 2026-11-19: the author said that rule is wrong, and the reference is v4.1.0. The readme of `frescogo/frescogo` at `v4.1.0` and the code of v4.1.0 agree (5 minutes, 150 hits, 30%, 5 falls of truce, 3% per fall, the 20th ends it) | C.3 |
| #4 | Known differences of the port in the flow | remark | closed on 2026-11-19: `delay(1000)` now blocks as in the original (`SDL_Delay`: the screen stays, and the keys wait in the system), and `millis` is the real time (`pico.get.now`). Only in the tests, with fixed frames, the time is that of the frames and a delay just advances it, as the harness of the original does | C.4 |
| #5 | Remove the `trinca` mode | work, after fidelity | allowed by the author; not needed for v4.1.0 | B.10 |
| #6 | Adopt the rule of 6 minutes, with the discount of time per fall | work | **only at the very end** (author, 2026-11-19), after v4.1.0 is exact and working | B.11 |
| #7 | Windows: the serial line over POSIX leaves it out | decision | closed on 2026-11-19: accepted for now | A.5 |
| #8 | The oracle: run the original | decision | done on 2026-11-19: the original runs here from the jars of its release package, not changed, with stand-ins for sound and serial (`oracle/Roteiro.java`, `oracle/stub/`, `make orig`). It writes the expected outputs of the tests of the flow, and its screen is the reference for `#11` | A.6 |
| #9 | Replace the native literal of the size of the display by the query of pico | work | done on 2026-11-19 (pico `8d4d549`, `pico.get.app().screen`); with no window manager (Xvfb) a run needs `PICO_TESTS=1`, which the tests set | B.1 |
| #10 | Move the data to `data/` and remove the original | work | the data is in `data/` since 2026-11-19; `placar/` keeps only the source and `conf.json` of v4.1.0 | B.2 |
| #11 | F4: the screen (`draw_draw` of v4.1.0), against the screen of the original | work | done on 2026-11-19: `proc.atm` has the drawing of Processing over pico, and the drawing code is ported line by line. 10 screens of 6 states are compared with the original at the same frames (`tst/telas.sh`): mean difference of 2.5 to 3.1 in 255, which is the smoothing of the text; no difference of layout seen | B.5 |
| #12 | F5: the radar | work | done on 2026-11-19: `serial.atm` (the line over POSIX: the list of ports, open at 9600 baud, a read that blocks as in v4.1.0), `radar_be` one to one. Three scripted radars (files of packets, read by the port and by the original: `tst/radar.py`) give identical sounds, accepted readings, reports and `radar.txt`, with the automatic falls and the limit of falls. The real line was tried over a pseudo-terminal at the pace of the radar (`tst/radar-pty.py`). The real device is for the author (`#13`) | B.6 |
| #13 | F6: the remaining keys, the launcher, a full presentation by the author with the device | work | not started | B.7 |
| #14 | F7: the same program written for expressiveness | work | done on `master` (2026-11-20): the flow as a hierarchy of tasks, the data as named tables and state tags, and the screen by regions over drawing primitives with explicit arguments (see the section below). The branch `fidelity` keeps the port that is one to one | B.8 |
| #15 | Design a second environment (`env-posix`) from the case of the serial line | work, with the author | not started | B.9 |
| #16 | In the original (v4.1.0), two hits by key in the same frame have the same time, and `jogo_kmh` divides by zero | defect of the original | fixed on `master` on 2026-11-20, by the author's decision: the first of the two hits gets the highest speed that is accepted (`maxima`), which is what one millisecond between them already gives. `fidelity` still ends with the error, as the original | |
| #17 | In v4.1.0 the automatic mode (`CTRL-A`) only restarts its 3.5 s with a reading of the radar: with the keyboard, the fall comes 3.5 s after `CTRL-A`, and then at the first hit of each sequence. The port does the same | remark | reported on 2026-11-19 | |
| #18 | In v4.1.0 `radar_be` blocks the frame until a whole packet arrives, and the port does the same: with a radar connected and silent, the screen stops while a sequence goes on | remark | reported on 2026-11-19 | |
| #19 | The text of pico: accents and smoothing | work | done on 2026-11-20: pico draws UTF-8 and has `smooth` in the pencil (decided by the author in `env-pico#5`), which the port turns on. With the borders and the ellipses on the pixels that the original uses, the mean difference of the screens is 1.3 to 1.9 in 255 (it was 2.5 to 3.1), and the limit of `tst/telas.sh` is 2.5. The texts are within 1 pixel of the original | |
| #20 | With `serial = ""`, the original takes the last serial port of the system, and the port does the same. On a machine with a port and no radar (here, `/dev/ttyS0`), both open it, show the radar as connected, and stop at the first serve, waiting for a packet. To run with no radar, the configuration must name a port that does not exist | remark | reported on 2026-11-19 | |

## The expressive version (#14)

`master` since 2026-11-20. The one-to-one port stays in the branch
`fidelity` (marked at `c69eead`; one fix since, `e35fde0`: BACKSPACE did not
erase a name of one letter, in both versions).

What changed in the code:

- `frescogo.atm`: the flow is `Apresentacao` > `Jogo` > `Ocioso` (> `Nome`)
  and `Sequencia`, tasks that are awaited in sequence; a state is a place in
  the code, and what is only valid in a state lives in it (the reader of the
  radar and the arrows of the hits in `Sequencia`, the typing in `Nome`).
  `watching` gives the keys that are valid over a whole level (CTRL-R and
  CTRL-E over a presentation, CTRL-S over a game), `par :any` the ways a
  sequence ends, and the second after a fall is `await 1s`.
- No `keyPressed` or `draw` with the cases of each state. `ESTADO` remains
  as what the screen reads, set where each state starts.
- `tela.atm` is the screen by regions (`tela_tempo`, `tela_meio`,
  `tela_atleta`, `tela_total`), a function of the state, over `desenho.atm`:
  boxes, discs, lines, text, and images with all they need as arguments.
  The emulation of the state of Processing (`proc.atm`: fill, stroke,
  alignment, size) is gone. The 14 screens are the same, byte by byte, as
  before the rewrite. `radar.atm` (the filter) is a file of its own.
- `jogo.atm`: the game is one table, `J` (`J.quedas`, `J.seqs`, `J.atletas`),
  a hit is `[t=, jog=, kmh=]`, an athlete `[pontos=, golpes=, media=, min=,
  max=]`, and the configuration is the table that is read (`CONF.tempo`),
  with no copies in `CONF_*` variables. The state is a hierarchical tag:
  `:ocioso`, `:nome`, `:seq.saque`, `:seq.golpes`, `:fim` (`ESTADO ?? :seq`
  holds in a sequence).

The tests still compare with the original run over the same keys: the 10
presentations are identical (sounds with their times, reports, csv,
`radar.txt`), and the 14 screens. For that, the conditions over the time
are checked once per frame (`await :quadro until ...`), as the original
does, and not by timers of their own.

What differs from the original, by choice (the intent is kept):

- The second after a fall does not block: the screen goes on, and a key
  typed meanwhile is handled then (a hit is ignored), not after it.
  `oracle/Roteiro.java` models the `delay` of the original accordingly: the
  sketch goes on at the first frame after the second.
- The keys are those of pico, with none of what Java does with CTRL (CTRL-H
  as BACKSPACE, CTRL-J and CTRL-M as ENTER, CTRL-ESC to quit).
- A restart or CTRL-S while a name is typed ends the typing (the original
  leaves its cursor on the screen).
- The time is the clock of the frames, not the time of the machine.

What is left of `#14`, to decide with the author: the screen (`tela.atm` is
still the drawing of the original, line by line, over `proc.atm`), and
whether `#16` is fixed here.

Still as in the original: `#16` (two hits in one frame divide by zero),
`#17`, `#18`, `#20`.

## Decided by the author

| Date | Decision |
|------|----------|
| 2026-11-16 | The serial port: option A. A small library of the port, natives over POSIX (`open`, `termios`, a read that does not block), polled by a task once per frame, as the original does in `draw`. No new environment for now |
| 2026-11-16 | Packages: the session may install whatever it needs in the container |
| 2026-11-20 | Start `#14` (the expressive version) on `master`, as far as it goes with no questions; the one-to-one port is marked as the branch `fidelity` |
| 2026-11-19 | **The reference is v4.1.0** (`frescogo-v4.1.0-win64.zip`), exact; the rule of 6 minutes only at the very end. Windows left out for now. The original may be run for the oracle of the screen |
| 2026-11-18 | The configuration becomes a table of Atmos with comments, `data/conf.atm`, read with `X.load` (no JSON) |
| 2026-11-18 | (no decision needed) The sounds: pico plays the 15 files as they are, the MP3 included |
| 2026-11-17 | The port lives at the root of the repository; the original (`placar/`) is removed later |

## A. Decisions that wait for the author

One at a time, each with a recommended option.

| # | Decision | Recommended |
|---|----------|-------------|

## B. Work known and not done

| # | Work | Note |
|---|------|------|
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
| v4.1.0, exact | The tests of the flow compare the port with THE ORIGINAL run over the same scripted keys (`make orig`): the sounds with their times, the reports and `resultados.csv` are identical in the 6 presentations. Keys as Java gives them (`key` and `keyCode`: with CTRL a letter is its control code, so CTRL-H is BACKSPACE as there) | 2026-11-19 |
| v4.1.0 | The original of v4.1.0 in `placar/`, its data in `data/`; the oracle (12 scripts), the score (`jogo.atm`, identical in all), the flow and the keyboard (`frescogo.atm`: 6 scripted presentations, each report given back to the oracle). 37 tests | 2026-11-19 |
| F0 | The setup (`Makefile`: the compiler and pico at fixed commits), `PORT.md`, a window with the logos, the test of the screen | `Makefile`, `frescogo.atm`, `tst/` |
| F3 | The flow (`go_*`), the keyboard (`keyPressed`, the keys held for 2 s, the names), the sounds, the report, the screenshot and `resultados.csv`. Five scripted presentations, each report given back to the oracle | `frescogo.atm`, `tst/fluxo.sh` |
| F2 | The configuration and the score in Atmos: identical to the oracle in its 12 scripts, and in 40 random ones (not kept). The `1.2` of the powerful hits is a float in Processing: a native literal reproduces it (it differs from a double at 45->54, 50->60, ...) | `jogo.atm`, `tst/jogo.sh` |
| F1 | The oracle of the score: the functions of the score and the report in plain Java, 9 scripts with their expected outputs | `oracle/` |
