#!/usr/bin/env python3
# Writes the scripted radars of the tests of the flow (`tst/fluxo/*.radar`),
# and their keys and configurations. Run once, by hand; the files are kept.
#
# A radar is a file with the bytes that the device sends, in the format that
# the sketch reads (`radar_be`): packets of 23 bytes that start with 0x88,
# with the direction in the bit 1 of the byte 7 (0=out, 1=in) and the speed
# in the bytes 8 to 11, as four digits, in tenths of km/h. While a sequence
# goes on, the sketch reads one packet per frame.

import random, re

def packet (vel=0, dir=0):
    s = bytearray(b'0' * 22)
    s[0]  = ord('A')
    s[7]  = 0x02 if dir == 1 else 0x00
    s[8:12] = b'%04d' % vel
    s[21] = ord('\r')
    return bytes([0x88]) + bytes(s)

class Radar:
    def __init__ (self, seed):
        self.r = random.Random(seed)
        self.b = bytearray()
        self.n = 0
    def nada (self, n):
        for _ in range(n):
            self.b += packet(); self.n += 1
    def golpe (self, kmh, dir, reps=5):
        # the speed rises to its peak and falls, as the radar reports it
        for i in range(reps):
            v = int(kmh*10 * (0.8 + 0.2*min(i, reps-1-i)/max(1,(reps-1)//2)))
            self.b += packet(min(v, kmh*10) if i != reps//2 else kmh*10, dir); self.n += 1
    def lixo (self, n):
        self.b += bytes(self.r.choice(b'0123456789ACR \r') for _ in range(n))
    def troca (self, n, kmh=60, dir=0, lo=28, hi=45):
        for _ in range(n):
            self.golpe(self.r.randint(kmh-25, kmh+35), dir, self.r.randint(3,7))
            self.nada(self.r.randint(lo, hi))
            dir = 1 - dir
        return dir
    def save (self, name):
        self.nada(400)      # the radar goes on
        open(f"tst/fluxo/{name}.radar", "wb").write(self.b)

class Keys:
    def __init__ (self): self.k = []; self.f = 5
    def ctrl (self, key):
        self.k += [(self.f,"dn","Left Ctrl"), (self.f+1,"dn",key), (self.f+2,"up","Left Ctrl")]
        self.f += 5
    def wait (self, n): self.f += n
    def save (self, name, extra=20):
        open(f"tst/fluxo/{name}.keys","w").write(f"{self.f+extra}\n" + ",".join(f"{f}:{d}:{k}" for f,d,k in self.k) + "\n")

def conf (name, **kw):
    c = open("data/conf.atm").read()
    c = c.replace("which takes the place of `conf.json` of the original (v4.1.0).", f"here with other values, for the test `{name}`.")
    for k,v in kw.items():
        c,n = re.subn(rf"(\n    {k}\s*= )\S+,", rf"\g<1>{v},", c); assert n == 1
    open(f"tst/fluxo/{name}.conf","w").write(c)

# A sequence: the serve, the packets that `build` adds to the radar, which
# are read one per frame, and then the key that ends it (or none).
def seq (k, r, build, fim="Down", saque="Up"):
    k.ctrl(saque); n0 = r.n
    build(r)
    k.wait(r.n - n0)
    if fim:
        k.ctrl(fim)

# 01: a presentation: the falls by key (CTRL-DOWN, CTRL-J), the falls added
# and removed (CTRL-=, CTRL--), the serve by CTRL-U, a sequence undone after
# its fall (CTRL-BACKSPACE), the sides inverted (CTRL-I), a serve undone,
# and the end by CTRL-S
r = Radar(1); k = Keys()
seq(k, r, lambda r: (r.nada(30), r.troca(24)))
k.ctrl("="); k.ctrl("="); k.ctrl("-")
seq(k, r, lambda r: (r.nada(25), r.troca(12, kmh=70)), fim="J", saque="U")
k.ctrl("Backspace")
seq(k, r, lambda r: (r.nada(20), r.troca(10, kmh=80)))
k.ctrl("I")
seq(k, r, lambda r: (r.nada(20), r.troca(14, kmh=65, dir=1)), fim="Backspace")
k.ctrl("Down")
k.ctrl("Up"); k.ctrl("Backspace")
k.wait(20); k.ctrl("S")
r.save("01-jogo"); k.save("01-jogo")

# 02: the automatic mode turned on and off (CTRL-A): a fall by 3.5 s of
# silence, the serve by itself one second later, and a fall by key
r = Radar(2); k = Keys()
k.ctrl("A")
k.ctrl("Up"); n0 = r.n
r.nada(30); r.troca(8); r.nada(240)     # 3.5 s (219 frames), the fall, 1 s
r.troca(6, dir=1)
k.wait(r.n - n0); k.ctrl("A"); n0 = r.n
r.troca(8); r.nada(300)                 # no fall by silence now
k.wait(r.n - n0); k.ctrl("Down"); k.ctrl("S")
r.save("02-auto"); k.save("02-auto")

# 03: the end by the time, in a presentation of one minute
conf("03-tempo", tempo=60)
r = Radar(3); k = Keys()
seq(k, r, lambda r: (r.nada(30), r.troca(30)))
seq(k, r, lambda r: (r.nada(30), r.troca(30, kmh=75)))
seq(k, r, lambda r: (r.nada(30), r.troca(80, kmh=70)), fim=None)
r.save("03-tempo"); k.save("03-tempo", extra=200)

# 04: a warm-up (CTRL-E): the time runs from the serve, and there are no falls
r = Radar(4); k = Keys()
k.ctrl("E")
seq(k, r, lambda r: (r.nada(30), r.troca(40), r.nada(200), r.troca(40, kmh=80)), fim="Down")
k.wait(3000)
r.save("04-esquenta"); k.save("04-esquenta")

# 05: the names (CTRL-1, CTRL-2, CTRL-0): shift, caps lock, backspace, and
# ENTER of the keypad, then a short sequence, and the end
r = Radar(5); k = Keys()
def tecla (key, d="dn"):
    k.k.append((k.f, d, key)); k.f += 3
k.ctrl("1"); tecla("Left Shift"); tecla("A"); tecla("Left Shift","up"); tecla("N"); tecla("A")
tecla("Left Shift"); tecla("-"); tecla("X"); tecla("Left Shift","up"); tecla("Backspace"); tecla("Return")
k.ctrl("2"); tecla("CapsLock"); tecla("B"); tecla("I"); tecla("Left Shift"); tecla("A"); tecla("Left Shift","up")
tecla("CapsLock"); tecla("Return")
k.ctrl("0"); tecla("X"); tecla("Backspace"); tecla("Backspace"); tecla("J"); tecla("O"); tecla("S"); tecla("E")
tecla("Keypad Enter")
seq(k, r, lambda r: (r.nada(20), r.troca(8)))
k.ctrl("S"); k.ctrl("Q")
r.save("05-nomes"); k.save("05-nomes")

# 06: the end by the falls, which take 12 s each of a presentation of one
# minute, with a fall added by the referee between sequences (CTRL-=); and
# the rest that runs out (3 seconds)
conf("06-quedas", tempo=60, descanso=3)
r = Radar(6); k = Keys()
for i in range(3):
    seq(k, r, lambda r: (r.nada(30 + 600*(i%2)), r.troca(10, dir=i%2)))   # 10 s to serve: 5 s of rest
k.ctrl("=")
k.wait(100)
r.save("06-quedas"); k.save("06-quedas")

# 10: the clock started by the referee (CTRL-T): the radar misses the serve;
# a sequence with no hit at all; and in the automatic mode, where the silence
# counts from the key
r = Radar(10); k = Keys()
def fraco (r):
    r.nada(20); r.golpe(35, 0); r.nada(40)          # below the speed of a serve
k.ctrl("Up"); n0 = r.n; fraco(r)
k.wait(r.n - n0 - 20); k.ctrl("T"); n0 = r.n
r.nada(10); r.troca(12, dir=1)
k.wait(r.n - n0); k.ctrl("Down")
k.ctrl("Up"); k.wait(40); k.ctrl("T"); k.wait(120); k.ctrl("Down")
r.nada(170)                                     # read meanwhile
k.ctrl("A")
k.ctrl("Up"); n0 = r.n; fraco(r)
k.wait(r.n - n0 - 20); k.ctrl("T"); n0 = r.n
r.nada(10); r.troca(6)
k.wait(r.n - n0 + 300)
k.ctrl("A"); k.ctrl("Down"); k.ctrl("S")
r.save("10-crono"); k.save("10-crono")

# 07: two sequences with the radar, the falls by key
r = Radar(7); k = Keys()
k.ctrl("Up")                            # the sketch reads from here on
r.nada(40); r.troca(24); n1 = r.n
k.wait(n1); k.ctrl("Down"); k.wait(30)  # nothing is read meanwhile
k.ctrl("Up"); n0 = r.n
r.nada(20); r.golpe(40, 0); r.nada(30)  # below the speed of a serve: no hit
r.troca(16, kmh=75, dir=1)
k.wait(r.n - n0 - 5); k.ctrl("Down"); k.ctrl("S")
r.save("07-radar"); k.save("07-radar")

# 08: the automatic mode: falls by 3.5 s of silence, the serve after one
# second, and the end by the time, with one minute (the falls take 12 s each)
conf("08-radar-auto", tempo=60)
r = Radar(8); k = Keys()
k.ctrl("A"); k.ctrl("Up")
r.nada(30)
for i in range(4):
    r.troca(r.r.randint(8,14), dir=i%2)
    r.nada(260)                         # 3.5 s (219 frames), and more
k.wait(r.n + 60)
r.save("08-radar-auto"); k.save("08-radar-auto", extra=257)

# 09: what the filter refuses, with the radar on the right side
conf("09-radar-ruido", lado_radar=2, radar_reps=4)
r = Radar(9); k = Keys()
k.ctrl("Up")
r.nada(30)
d = r.troca(4)
r.b += packet(900, d); r.n += 1; r.nada(30)             # a single reading
r.golpe(70, d, 2); r.golpe(70, 1-d, 2); r.nada(30)      # the direction changes inside the readings
d = r.troca(3, dir=d)
r.golpe(66, 1-d, 6); r.nada(8)                          # the same athlete again, too soon
r.golpe(80, d, 6); r.nada(5); r.golpe(77, 1-d, 6)       # the other one, too soon
r.nada(40); r.lixo(37)                                  # bytes that are no packet
d = r.troca(6, dir=d)
r.golpe(104, d, 9); r.nada(40); r.golpe(120, 1-d, 4); r.nada(40)
d = r.troca(5, dir=d, lo=10, hi=20)                     # fast
k.wait(r.n); k.ctrl("Down"); k.ctrl("S")
r.save("09-radar-ruido"); k.save("09-radar-ruido")
