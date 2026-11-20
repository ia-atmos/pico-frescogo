# The serial line as a case for a second environment (`#15`)

Written by the session `pico-frescogo` on 2026-11-20, for whoever designs
how two environments live in one program (`atmos-lang` item 57, `atmos-envs`).
No design is decided here: this is what the case needs, and what it does
today.

## What the program is

A scoreboard over pico (window, keys, clock, frames) that also reads a radar
on a serial line: packets of 23 bytes, 40 to 50 per second, at 9600 baud.

## What it does today (option A of the plan)

`serial.atm` is not an environment: it is a library of natives over POSIX
(`open`, `termios`, `read`), called from a task of the program.

    spawn {
        loop on :radar.vez {            ;; once per frame, emitted by the program
            val kmh = radar()           ;; blocks until a whole packet arrives
            if math.abs(kmh) > 1 {
                emit :golpe [...]
            }
        }
    }

The read blocks, because v4.1.0 of the original blocks, and the frame waits
for the radar. It works because the radar never stops sending. It is wrong
as a design:

- While the read blocks, nothing else runs: no key, no clock, no frame. A
  radar that is unplugged freezes the program.
- The pace of the frames becomes the pace of the radar.
- The program polls: the radar is read "at each frame", and not "when a
  packet arrives".

## What it would need from an environment

- **A file descriptor that wakes the program**: the environment waits on
  the descriptor of the serial line together with what pico waits for, and
  emits an event when bytes arrive (`:serial [h=, bytes=]`, or a packet at a
  time, given a delimiter and a size).
- **Pico stays the primary**: it owns the clock and the frames. The serial
  line is secondary: it is polled with no wait at each step of the loop of
  pico (as lua-atmos does), or pico waits on its descriptor too.
- **A read with a timeout**, as an await: `watching 3500ms { await :serial }`
  is the automatic fall of the scoreboard (3.5 seconds with no reading), and
  today it is a comparison of times at each frame.
- **The lifetime of the line as a scope**: open and close with a block (a
  native value that is pinned), and the listening only while a sequence
  goes on, as the task above already is.
- **A scripted source for tests**: the tests feed a file of packets, one
  packet per frame while the program listens; an environment needs the
  same (a file in place of the device, paced by the frames).
- Not needed here: writing to the line, other speeds, Windows.

## What would change in the program

`radar()` would take a packet and not read it, the task would be
`loop pkt on :serial { ... }`, and the frame would no longer wait for the
radar. That changes the timing of the hits (a hit would have the time of its
packet, not of the frame that read it), so the tests against the original,
which compare times, would need the original's model of it.
