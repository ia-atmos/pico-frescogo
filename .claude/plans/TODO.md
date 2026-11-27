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
| #5 | Remove the `trinca` mode | done on 2026-11-21, on `master` (the author: "tirar trinca") | the score, the screen, the configuration; the reports still say `dupla`, as v4.1.0, until the version changes. The oracle keeps it (`oracle/tst/05-trinca.txt` tests only the oracle) | B.10 |
| #6 | The rule of time: 6 minutes, each fall takes 12 s of the time | done on 2026-11-22, on `master` (author: "regra do tempo"; the falls only cost time, no percentage) | by the session, to be confirmed: no limit of falls either (30 falls take the 6 minutes); `ataques` is now the 150 strongest hits of each athlete, not tied to `tempo`; a fall that takes the last of the time ends the presentation one second later, and so does a fall added by CTRL-= between sequences (at once); the report has `Tempo: <played>s + <falls>s das quedas` and no longer the line of the percentage; the screen shows `-24 s` under the falls. `data/conf.atm`: `tempo = 360`, `queda = 12`, no `tregua`, `quedas`, `aborta` | B.11 |
| #7 | Windows: the serial line over POSIX leaves it out | decision | closed on 2026-11-19: accepted for now | A.5 |
| #8 | The oracle: run the original | decision | done on 2026-11-19: the original runs here from the jars of its release package, not changed, with stand-ins for sound and serial (`oracle/Roteiro.java`, `oracle/stub/`, `make orig`). It writes the expected outputs of the tests of the flow, and its screen is the reference for `#11` | A.6 |
| #9 | Replace the native literal of the size of the display by the query of pico | work | done on 2026-11-19 (pico `8d4d549`, `pico.get.app().screen`); with no window manager (Xvfb) a run needs `PICO_TESTS=1`, which the tests set | B.1 |
| #10 | Move the data to `data/` and remove the original | work | the data is in `data/` since 2026-11-19; `placar/` keeps only the source and `conf.json` of v4.1.0 | B.2 |
| #11 | F4: the screen (`draw_draw` of v4.1.0), against the screen of the original | work | done on 2026-11-19: `proc.atm` has the drawing of Processing over pico, and the drawing code is ported line by line. 10 screens of 6 states are compared with the original at the same frames (`tst/telas.sh`): mean difference of 2.5 to 3.1 in 255, which is the smoothing of the text; no difference of layout seen | B.5 |
| #12 | F5: the radar | work | done on 2026-11-19: `serial.atm` (the line over POSIX: the list of ports, open at 9600 baud, a read that blocks as in v4.1.0), `radar_be` one to one. Three scripted radars (files of packets, read by the port and by the original: `tst/radar.py`) give identical sounds, accepted readings, reports and `radar.txt`, with the automatic falls and the limit of falls. The real line was tried over a pseudo-terminal at the pace of the radar (`tst/radar-pty.py`). The real device is for the author (`#13`) | B.6 |
| #13 | F6: a full presentation by the author with the device | work, of the author | the steps are in `PORT.md`, "A first run with the device". The keys of v4.1.0 are all done; the launcher and the text of the operation are `#21` and `#22` | B.7 |
| #14 | F7: the same program written for expressiveness | work | done on `master` (2026-11-20): the flow as a hierarchy of tasks, the data as named tables and state tags, and the screen by regions over drawing primitives with explicit arguments (see the section below). The branch `fidelity` keeps the port that is one to one | B.8 |
| #15 | Design a second environment (`env-posix`) from the case of the serial line | work, with the author | the case is written down in `.claude/plans/env-posix.md` (2026-11-20): what the line needs from an environment, and what the program does today. No design decided | B.9 |
| #16 | In the original (v4.1.0), two hits by key in the same frame have the same time, and `jogo_kmh` divides by zero | defect of the original | fixed on `master` on 2026-11-20, by the author's decision: the first of the two hits gets the highest speed that is accepted (`maxima`), which is what one millisecond between them already gives. `fidelity` still ends with the error, as the original | |
| #17 | In v4.1.0 the automatic mode (`CTRL-A`) only restarts its 3.5 s with a reading of the radar: with the keyboard, the fall comes 3.5 s after `CTRL-A`, and then at the first hit of each sequence. The port does the same | remark | reported on 2026-11-19 | |
| #18 | In v4.1.0 `radar_be` blocks the frame until a whole packet arrives, and the port does the same: with a radar connected and silent, the screen stops while a sequence goes on | remark | reported on 2026-11-19 | |
| #19 | The text of pico: accents and smoothing | work | done on 2026-11-20: pico draws UTF-8 and has `smooth` in the pencil (decided by the author in `env-pico#5`), which the port turns on. With the borders and the ellipses on the pixels that the original uses, the mean difference of the screens is 1.3 to 1.9 in 255 (it was 2.5 to 3.1), and the limit of `tst/telas.sh` is 2.5. The texts are within 1 pixel of the original | |
| #20 | With `serial = ""`, the original takes the last serial port of the system, and the port does the same. On a machine with a port and no radar (here, `/dev/ttyS0`), both open it, show the radar as connected, and stop at the first serve, waiting for a packet. To run with no radar, the configuration must name a port that does not exist | remark | reported on 2026-11-19 | |
| #21 | The launcher (`launcher/frescogo.sh`) runs the port (`make frescogo`, then `./frescogo`) and no longer Processing | by the session, to be confirmed | done on 2026-11-20; `frescogo.desktop` still has the paths of the author's machines |  |
| #23 | No hits by the keyboard (`←` `→`, the speeds from `distancia`, `maxima`): only the radar. A second `CTRL-↑`, once the first is released, starts the clock of a sequence before its first hit, in spite of the radar (author, 2026-11-22: "uma 2a tecla caso o arbitro queira disparar o crono a despeito do radar"; 2026-11-23: "outro ctrl-cima", with no hold) | done on 2026-11-22 (`CTRL-T`), on 2026-11-23 the second `CTRL-↑` | by the session, to be confirmed: with no hit after it, the time of the sequence is zero (the clock goes back to the last hit, and there is none); the report has a line `cronômetro pelo árbitro`; without a radar the program still runs, and says `sem radar`. The tests of the flow use scripted radars; their expected outputs are now those of the port, and each report is given back to the oracle | |
| #24 | The version of `master` is 4.2.0, and the report and the screen no longer say `dupla` | by the session, to be confirmed | done on 2026-11-22; the number is the author's to choose |  |
| #25 | `data/conf.atm` in sections (`tempos`, `pontos`, `radar`, `nomes`, `tela`, `som`), each value apart from the others, with the constants that were in the code (5 s to serve, the warning at 30 s, the 30 hits of the balance, the 3.5 s of silence and the 1 s to serve again of the automatic mode, the 1.5 s of the speed and the 0.5 s of the arrow on the screen), the sounds, and the starting names | done on 2026-11-23, on `master` (author: "propor um novo conf.atm separado em seções", then "incluir sons e nomes dos atletas iniciais") | the author renamed `apresentacao` to `tempos` and its `tempo` to `jogo` (2026-11-23); the rest of the names (`ataques` is `pontos.golpes`, `lado_radar` is `radar.lado`) to be confirmed |  |
| #26 | Excerpt, idiomatic: `frescogo.atm:260`, the serve as a `par :any` of the three ways a sequence starts (undo, the referee's second CTRL-UP after the release of the first, a hit of the radar) | shown on 2026-11-26; the author agreed (2026-11-27) | |  |
| #27 | Excerpt, bad: `frescogo.atm:307`, the flag `direto` of the automatic mode in the loop of `Jogo` | shown on 2026-11-26; the author agreed (2026-11-27) with the fix: the task `Ocioso` becomes `Saque(auto)`, which races the referee's CTRL-UP and the automatic serve and gives `:tecla`, `:auto` or nil; the state `:seq.saque` becomes `:seq.espera`; `:ocioso` stays. done on 2026-11-27; all the tests pass | a variable turns `Ocioso` on and off by hand, and its order was wrong once; better in the port: the automatic mode as a task that serves by itself. Nothing missing in the language |  |
| #28 | The answer to the general instruction of the ports: grade 6/10; to redo: the radar (`radar.atm`, transcribed), the loose globals of the flow, the automatic mode as a task (`#27`), `nf` by `string.format`, `resultados.csv` and the log of the radar as tables of Atmos | waits for the author: which to start with (recommended: the radar) | |  |
| #29 | The rules of `IDIOMS.md` applied to the port (author: "ajeite os idioms"): `await` with no parentheses, predicates of more than one term in parentheses, the filter `e.ctrl` in the pattern of the loop of the global keys, `ifs` in the radar | done on 2026-11-26; all the tests pass | `await X * 1ms` is `(await X) * 1ms`: the time goes in a `val` first (found by the tests); no `loop _ in`, no `emit(...)` in the port; the named small functions stay functions |  |
| #30 | Excerpt, idiomatic: `frescogo.atm:446`, the global keys with the filter `e.ctrl` in the pattern | shown on 2026-11-26; the author agreed (2026-11-27), with two changes: the loose globals (`AUTO`, `AUTO_ULTIMA`, `INV`, `ZER`, `ONE`, `MICROS`) go to a table `G`; the `match` gives `:nada` in its `else`, and `jogo_calc()` runs only when it is not `:nada`. done on 2026-11-27; all the tests pass | |  |
| #31 | Excerpt, bad: `radar.atm:91`, the state of the filter in vectors with constant indices, the side kept inverted, the results as magic numbers, the digits parsed by hand | shown on 2026-11-26; the author agreed (2026-11-27) with the fix: `R` = [leituras, proximo, t, kmh], a window of readings with `iter.all`/`iter.max`, the results `:silencio`, `:duvida`, `:golpe [jog=, kmh=]`, `tonumber` over the packet as a string, `string.format`. Done on 2026-11-27; all the tests pass | `tonumber("0740")` is 480 (octal): the digits are summed by a fold, as the original does, so a corrupt packet gives the same number as there (found by the tests) |  |
| #32 | The warm-up as its own task `Esquenta`, apart from the presentation: the serve, then the hits until `tempos.esquenta` from the serve, with no falls; only the speeds and the time on the screen, no points, no report, and the record is not changed (author, 2026-11-27: "esquenta só velocidade e tempo, sem recorde"; then "Recorde pode ficar no aquecimento": the record is shown, in gray). `J.esquenta` is gone; `G.modo` tells the screen which one runs; the reader of the radar is a task `Leitor`, shared | done on 2026-11-27; all the tests pass (the screen of the warm-up and its output are new references) | by the session: the warning of 30 s and the end of a warm-up come one frame (16 ms) earlier, since the warm-up reads its time fresh; a hit emitted by the task `Leitor` goes to its parent with `emit @:parent` (a plain `emit` stays inside the task); no undo of the serve in a warm-up (there is no sequence to undo); CTRL-S during a warm-up ends it, a second CTRL-S does nothing. The oracle has no warm-up (`09-esquenta` removed) | |
| #37 | The screen as tasks: each region is a task that draws at `:tela.fundo` or `:tela.frente`, spawned by the program (`Base`), by a presentation (`Quedas`, `Pontos`, `Descanso`, `Relogio`, `Recorde`, `Luz`), by a warm-up (`Pisca`, `Relogio`, `Recorde`, `Luz`), or by a sequence (`Ultimos`), and gone with it; no `if` over the mode or the state in the screen; `G.modo` is gone; the blink is time (`watching 500ms`) | done on 2026-11-27 (author: "Essas telas todas poderiam ser tasks reagindo ao draw? Não teriam ifs"); the flow and the 15 screens are identical | the end screen and its picture come from `desenha()`, which emits the two layers to every task (`emit @:global`) |  |
| #38 | Rule: what is visible while something lives is a task spawned by it | sent to `atmos` for `IDIOMS.md` on 2026-11-27 (approved by the author) | |  |
| #39 | Rule: a change of state is a new task, not a flag (the red clock of the end) | sent to `atmos` on 2026-11-27 | |  |
| #40 | Rule: a rhythm in time is `watching`/`await`, not arithmetic over the clock (the blink) | sent to `atmos` on 2026-11-27 | |  |
| #41 | Move to lang main past eb2c350 (`:prelude` must be `:prelude.h` or `:prelude.c`): the two preludes of the port (`frescogo.atm`, `serial.atm`), and the pins | done on 2026-11-27 (lang ab2e325, env-pico 125025b), compiled, tests waiting for the author's ok; the radar reads its speed with `tonumber` again | the author's `make run ATMOS_LANG=../lang` failed on it |  |
| #42 | The keys with CTRL, but the arrows, count only when held for `tempos.segurar` (2 s), as in 5.x; once while held (the bar on the screen was removed by the author). No more CTRL-U and CTRL-J. A sound when the referee starts the clock (`som.arbitro`) | done on 2026-11-27 (author: "deixe o tempo configuravel", "use as mesmas teclas de 5.x", "sim", and the sound); all the tests pass; the held commands come 30 ms later in the outputs, and the referee's sound is new in `10-crono` | CTRL alone (its own key down has `ctrl`) was taken as a command and held, which lost the next key: the author saw holds much longer than 2 s; fixed. The keys become `:comando [key=]`, emitted by the task `Teclas`; the tests hold their commands 3 frames, with `tempos.segurar` 0.03 (`tst/fluxo/padrao.conf`); the referee's sound is `peteleco.mp3`, the sound of the slowest hits | |
| #43 | A window instead of the full screen: `tela.cheia = false` in `data/conf.atm`, or `FRESCOGO_JANELA=1`; a window of three quarters of the screen | done on 2026-11-27 (author: "opcao para executar sem ser tela cheia. default tela cheia") | not tried on a display (the tests run full screen) |  |
| #44 | The pre-game: before the first serve of a presentation or a warm-up, "PRÉ-JOGO" in the middle, and a red radar blinking at both sides with no radar; only there the names are typed, and CTRL-1/2/0, CTRL-I, CTRL-A need not be held | done on 2026-11-27 (author: "modo de configuracao", "CONF -> PRÉ-JOGO"); all the tests pass; the screens have new references | the red radar was drawn at x=0 and 1 (`loop x in [...]` gives the indices: `loop _, x in`) |  |
| #45 | The hits by the arrows again, with no radar: a reader `Leitor.Teclado`, beside `Leitor.Radar`; a hit is emitted at the next one, with its time and the speed from `manual.distancia` (750) and `manual.maxima` (85); the last one of a sequence is kept with no speed. `:golpe` carries its time `t` | done on 2026-11-27 (author: "volte com <- e ->", "a emissao deve ser compativel com o radar", "Leitor.Radar e Leitor.Teclado", "tabela manual"); all the tests pass, with a new scenario `11-teclado` | by the session: as with the radar, the first hit is a serve only at `radar.saque` (45 km/h) or more; the arrow is on the screen at the touch (author: "aparecer a seta no golpe manual") |  |
| #46 | The screen: only the version on top (the rest is in the report), no `inv`, no time of the falls under them, no yellow light in a warm-up | done on 2026-11-27 (author: "tire o -xxs", "tira as infos do topo"); all the tests pass, new references of the screens | |  |
| #47 | The screen as panels: each panel is a layer of pico, placed in the window by percentages (the grid of 11 columns and 8 rows of the original); each region draws into its panel (`in <:tela> C.<panel>, do {...}`), in fractions of the panel and with the anchors of pico; the intention of the original, not its pixels | done on 2026-11-27 (author: "o ideal seria usar layers para os paineis", "nao precisa seguir a risca os valores", "sim"); the oracle, the score and the flow pass with no change; the 17 screens have new references | the font and the pencil are of each layer; the final picture composes the panels first (`pico.output.draw.layers`); the falls stay after the end through a pool of the presentation (`regioes`); the lines of the grid are drawn over all, in a transparent panel (author: "desenhar por cima, apos tudo?"); the radar is a bigger icon with a transparent background, green when present, red and blinking when absent (`data/icos/radar-verde.png`, `radar-vermelho.png`, made from `radar.png`); the light of the automatic mode is always there, dark when off, also in the pre-game and the warm-up (author: "modo automatico deveria aparecer sempre"); the speed of a hit centered, with `km/h` under it at a quarter of its size; the bands 30/30/40, the top as logos 15 and names 15 (author: "30-30-40", "15/15"); the version by the referee, the clock only with the time and the rest; the pre-game and the warm-up in a half transparent gray, and the radar half transparent: for the referee more than for the public (author, 2026-11-27) |  |
| #22 | The operation of v4.1.0 (its keys) is written in `PORT.md`; the `README.md` of the root, which describes 5.x, was not touched | by the session, to be confirmed | done on 2026-11-20. To decide: whether `README.md` becomes the text of v4.1.0 |  |

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
| 2026-11-27 | For small changes, show the author the diff |
| 2026-11-27 | The warm-up shows only the speeds and the time, with no record (`#32`) |
| 2026-11-26 | (through `atmos`, atmos#29) Read `ia-atmos/lang` `.claude/plans/IDIOMS.md` before writing Atmos; a general rule that comes out of the author's review of an excerpt goes to `atmos`, with a before and an after |
| 2026-11-26 | (through `atmos`, pico-1oom#29, confirmed by the author) The general instruction for the ports: fidelity is the observable behaviour only; free to reorganize; idiomatic Atmos; own file formats (native: `X.load`/`X.dump`); do not reproduce the bugs of safety of the original. Answered in `#28` |
| 2026-11-26 | (through `atmos`) The excerpts are numbered with the `#N` of this session and listed here; each bad one says why, and what would make it better (the language, the environment, or the port) |
| 2026-11-26 | (through `atmos`) For each group of changes, show the author one pair of short Atmos excerpts of that work, with file and line: one idiomatic, one bad (awkward, verbose, a workaround) |
| 2026-11-26 | (through `atmos`, said in `pico-1oom`) At each big step (a milestone, a change of what is on the screen), show the author a few screenshots in the report |
| 2026-11-24 | (through `atmos`, the new managing session, atmos-lang#119) Coordination, status and questions for the author go to `atmos`; measurements of the compiler on this port and its merges stay with `atmos-lang`. A pause until next week: no new work, only what arrives |
| 2026-11-23 | Do not run the tests on my own: ask the author when testing is relevant (the whole suite takes 10 to 12 minutes). `pontos.equilibrio` is a table (`limite`, `golpes`) |
| 2026-11-22 | The key of the referee starts the clock in spite of the radar (CTRL-UP does not: it waits for the radar); the falls only cost time, no percentage |
| 2026-11-21 | Branch `4.1` = v4.1.0 exact; on `master` the real changes: remove trinca, remove the manual marking (with a key to start the clock), the rule of time |
| 2026-11-20 | (through `atmos-lang`) Work with no input: do what needs no decision; go on with a recommendation that is reversible and stays in the repository, marked "by the session, to be confirmed" |
| 2026-11-20 | Fix `#16` and rewrite the screen on `master` ("ambos") |
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
