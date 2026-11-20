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
    o = []
    for k,v in kw.items():
        c,n = re.subn(rf"(\n    {k}\s*= )\S+,", rf"\g<1>{v},", c); assert n == 1
        o.append(f"conf {k} {v}")
    open(f"tst/fluxo/{name}.conf","w").write(c)
    open(f"tst/fluxo/{name}.oracle","w").write("\n".join(o)+"\n")

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
# second, and the end by the limit of falls (4, with one minute)
conf("08-radar-auto", tempo=60)
r = Radar(8); k = Keys()
k.ctrl("A"); k.ctrl("Up")
r.nada(30)
for i in range(4):
    r.troca(r.r.randint(8,14), dir=i%2)
    r.nada(260)                         # 3.5 s (219 frames), and more
k.wait(r.n + 60)
r.save("08-radar-auto"); k.save("08-radar-auto", extra=260)

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
