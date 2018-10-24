# Resize relic images

# Notes:
#  Relics should be 128*128 padded to 256*256
#  The source relics can have arbitrary size with the same size padding

ROOT=../../../../../..
IMAGES:=$(notdir $(wildcard source/*.png))
NOT_RELICS:=AbstractConspireRelic
RELICS:=$(basename $(notdir $(wildcard $(ROOT)/src/main/java/conspire/relics/*.java)))
MISSING_IMAGES:=$(filter-out $(NOT_RELICS) $(basename $(IMAGES)), $(RELICS))
EXTRA_IMAGES:=$(filter-out $(RELICS),$(basename $(IMAGES)))
OUTLINE_IMAGES:=$(addprefix outline/,$(IMAGES))
LARGE_DIR=../../../images/largeRelics/conspire
LARGE_IMAGES:=$(addprefix $(LARGE_DIR)/,$(IMAGES))

all: $(IMAGES) $(OUTLINE_IMAGES) $(LARGE_IMAGES) verify

.PHONY: all verify clean

%.png: source/%.png
	convert $^ -resize 128x128 $@

outline:
	mkdir -p $@

outline/%.png: %.png | outline
	convert $< -evaluate set 0 -negate  -blur 12x1.5  -channel A -level 0%,5% +channel $@

$(LARGE_DIR):
	mkdir -p $@

$(LARGE_DIR)/%.png: source/%.png | $(LARGE_DIR)
	convert $< -resize 256x256 $@

define WARN
echo Warning: $(1);
endef

verify:
	@$(foreach c,$(MISSING_IMAGES),$(call WARN,missing image for relic $(c)))
	@$(foreach c,$(EXTRA_IMAGES),$(call WARN,no relic for image $(c)))

clean:
	rm -f *.png
	rm -rf outline large

