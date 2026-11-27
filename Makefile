# FrescoGO! in Atmos, over pico.
#
#   make            fetches the compiler and pico at fixed commits, and
#                   builds `atmos-pico` (in .deps/)
#   make run        runs the scoreboard
#   make frescogo   builds the scoreboard as the program `./frescogo`, which
#                   the launcher runs (`launcher/`)
#   make test       the tests, with no display (needs xvfb-run)
#
# With a compiler of your own, already built, give its directory: it is used
# as it is (not fetched, not moved to `LANG_REV`), and pico is built over it.
#
#   make run ATMOS_LANG=../lang
#
# The branch `4.1` (v4.1.0, exact) also runs the original, to check the port
# against it (`make orig`).
#
# Needs git, a C compiler, the SDL2 development packages (see README), and a
# JDK for the oracle.

LANG_URL := https://github.com/ia-atmos/lang
LANG_REV := ab2e32593b67eecf05b116c465ac41b5e4d8df35
PICO_URL := https://github.com/ia-atmos/env-pico
PICO_REV := 125025b5b3bf7eb8435cb251525ed489358b77ea

DEPS  := $(CURDIR)/.deps
ATMOS := $(DEPS)/env-pico/atmos-pico

ATMOS_LANG ?=
LANG_DIR := $(if $(ATMOS_LANG),$(abspath $(ATMOS_LANG)),$(DEPS)/lang)

.PHONY: all run test clean

FONTES := frescogo.atm jogo.atm tela.atm desenho.atm radar.atm serial.atm

all: $(ATMOS)

$(DEPS)/lang/atmos:
	@mkdir -p $(DEPS)
	test -d $(DEPS)/lang || git clone -q $(LANG_URL) $(DEPS)/lang
	git -C $(DEPS)/lang fetch -q origin
	git -C $(DEPS)/lang checkout -q $(LANG_REV)
	$(MAKE) -C $(DEPS)/lang

$(ATMOS): $(LANG_DIR)/atmos
	@mkdir -p $(DEPS)
	test -d $(DEPS)/env-pico || git clone -q $(PICO_URL) $(DEPS)/env-pico
	git -C $(DEPS)/env-pico fetch -q origin
	git -C $(DEPS)/env-pico checkout -q $(PICO_REV)
	$(MAKE) -C $(DEPS)/env-pico ATMOS=$(LANG_DIR)

run: $(ATMOS)
	$(ATMOS) frescogo.atm

frescogo: $(ATMOS) $(FONTES)
	$(ATMOS) -o frescogo frescogo.atm

test: $(ATMOS)
	oracle/run.sh
	tst/jogo.sh
	tst/fluxo.sh
	tst/telas.sh

clean:
	rm -rf $(DEPS) frescogo
