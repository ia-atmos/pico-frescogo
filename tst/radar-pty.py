#!/usr/bin/env python3
# A radar on a pseudo-terminal, to try the serial line of the port with no
# device: writes the packets of a `.radar` file at the pace of the radar
# (about 45 per second), and runs a command with the path of the terminal in
# FRESCOGO_SERIAL.
#
#   tst/radar-pty.py tst/fluxo/07-radar.radar <command...>

import os, pty, subprocess, sys, threading, time

data = open(sys.argv[1], "rb").read()
master, slave = pty.openpty()
path = os.ttyname(slave)

def radar ():
    for i in range(0, len(data), 23):
        try:
            os.write(master, data[i:i+23])
        except OSError:
            return
        time.sleep(1/45)

threading.Thread(target=radar, daemon=True).start()
env = dict(os.environ, FRESCOGO_SERIAL=path)
sys.exit(subprocess.call(sys.argv[2:], env=env))
