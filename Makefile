# FrescoGO! in Atmos, over pico.
#
#   make            fetches the compiler and pico at fixed commits, and
#                   builds `atmos-pico` (in .deps/)
#   make run        runs the scoreboard
#   make test       the tests: the oracle of the score, and the screen with
#                   no display (needs xvfb-run)
#
# Needs git, a C compiler, the SDL2 development packages (see README), and a
# JDK for the oracle.

LANG_URL := https://github.com/ia-atmos/lang
LANG_REV := ff5a6936ef4ab4644c6dd942cbec17276011e198
PICO_URL := https://github.com/ia-atmos/env-pico
PICO_REV := 3436fcff21a37f3256d6a5d9054f02a21a9dc8b9

DEPS  := $(CURDIR)/.deps
ATMOS := $(DEPS)/env-pico/atmos-pico

.PHONY: all run test clean

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

test: $(ATMOS)
	oracle/run.sh
	tst/jogo.sh
	tst/fluxo.sh
	tst/run.sh

clean:
	rm -rf $(DEPS)
