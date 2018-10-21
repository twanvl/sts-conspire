# Resize power images

# Notes:
#  Small power images are (usually) 32*32 padded to 48*48. This is a padding by a factor 1.5
#  Large images are then around 85*85 to 90*90 padded to 128*128
#  The atlas does NOT use premultiplied alpha

ROOT=../../../../../..
TEXTURE_PACKER=java -cp $(ROOT)/../_ModTheSpire/desktop-1.0.jar com.badlogic.gdx.tools.texturepacker.TexturePacker
PACKER_CONFIG=$(ROOT)/tools/texture-packer-powers.json
IMAGES:=$(notdir $(wildcard large/*.png))
NOT_POWERS:=AbstractConspire
POWERS:=$(patsubst %Power.java,%, $(notdir $(wildcard $(ROOT)/src/main/java/conspire/powers/*.java)))
MISSING_IMAGES:=$(filter-out $(NOT_POWERS) $(basename $(IMAGES)), $(POWERS))
EXTRA_IMAGES:=$(filter-out $(POWERS),$(basename $(IMAGES)))
IMAGES32:=$(addprefix 32/,$(IMAGES))
IMAGES64:=$(addprefix 64/,$(IMAGES))
IMAGES48:=$(addprefix images/48/,$(IMAGES))
IMAGES128:=$(addprefix images/128/,$(IMAGES))

all: powers.atlas verify

powers.atlas: $(IMAGES128) $(IMAGES48)
	$(TEXTURE_PACKER) images . $@ $(PACKER_CONFIG)

.PHONY: all verify clean

32:
	mkdir -p $@
64:
	mkdir -p $@
images/48:
	mkdir -p $@
images/128:
	mkdir -p $@

32/%.png: large/%.png | 32
	convert $< -resize 32x32 $@

64/%.png: large/%.png | 64
	convert $< -resize 64x64 $@

images/48/%.png: large/%.png | images/48
	convert $< -resize 48x48 $@

images/128/%.png: large/%.png | images/128
	convert $< -resize 128x128 $@

define WARN
echo Warning: $(1);
endef

verify:
	@$(foreach c,$(MISSING_IMAGES),$(call WARN,missing image for power $(c)))
	@$(foreach c,$(EXTRA_IMAGES),$(call WARN,no power for image $(c)))

clean:
	rm -rf 32 64 images skeleton.png skeleton.atlas
