# FrescoGO! in Atmos, over pico.
#
#   make            fetches the compiler and pico at fixed commits, and
#                   builds `atmos-pico` (in .deps/)
#   make run        runs the scoreboard
#   make frescogo   builds the scoreboard as the program `./frescogo`, which
#                   the launcher runs (`launcher/`)
#   make test       the tests, with no display (needs xvfb-run)
#   make orig       fetches the release package of the original (v4.1.0,
#                   85 MB), runs it over the scripted presentations, and
#                   writes what it gives as the expected outputs of the tests
#
# Needs git, a C compiler, the SDL2 development packages (see README), and a
# JDK for the oracle.

LANG_URL := https://github.com/ia-atmos/lang
LANG_REV := 009d99b0c19056f81c5724709b7747234d0f4a31
PICO_URL := https://github.com/ia-atmos/env-pico
PICO_REV := cb9529fe73ed25ccb19c22b8627353652381c6b4

ORIG_URL := https://github.com/frescogo/frescogo/releases/download/v4.1.0/frescogo-v4.1.0-win64.zip

DEPS  := $(CURDIR)/.deps
ORIG  := $(DEPS)/frescogo-v4.1.0-win64
ATMOS := $(DEPS)/env-pico/atmos-pico

.PHONY: all run test orig clean

FONTES := frescogo.atm jogo.atm tela.atm desenho.atm radar.atm serial.atm

all: $(ATMOS)

$(DEPS)/lang/atmos:
	@mkdir -p $(DEPS)
	test -d $(DEPS)/lang || git clone -q $(LANG_URL) $(DEPS)/lang
	git -C $(DEPS)/lang fetch -q origin
	git -C $(DEPS)/lang checkout -q $(LANG_REV)
	$(MAKE) -C $(DEPS)/lang

$(ATMOS): $(DEPS)/lang/atmos
	test -d $(DEPS)/env-pico || git clone -q $(PICO_URL) $(DEPS)/env-pico
	git -C $(DEPS)/env-pico fetch -q origin
	git -C $(DEPS)/env-pico checkout -q $(PICO_REV)
	$(MAKE) -C $(DEPS)/env-pico ATMOS=$(DEPS)/lang

run: $(ATMOS)
	$(ATMOS) frescogo.atm

frescogo: $(ATMOS) $(FONTES)
	$(ATMOS) -o frescogo frescogo.atm

test: $(ATMOS)
	oracle/run.sh
	tst/jogo.sh
	tst/fluxo.sh
	tst/telas.sh

$(ORIG):
	@mkdir -p $(DEPS)
	curl -sSL -o $(DEPS)/orig.zip $(ORIG_URL)
	cd $(DEPS) && unzip -q -o orig.zip && rm orig.zip

orig: $(ORIG)
	oracle/orig.sh $(ORIG)

clean:
	rm -rf $(DEPS) frescogo
