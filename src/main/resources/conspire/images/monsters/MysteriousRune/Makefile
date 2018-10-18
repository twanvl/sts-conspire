ROOT=../../../../../../..
TEXTURE_PACKER=java -cp $(ROOT)/../_ModTheSpire/desktop-1.0.jar com.badlogic.gdx.tools.texturepacker.TexturePacker
PACKER_CONFIG=$(ROOT)/tools/texture-packer-config.json
IMAGES=$(addprefix images/,shadow.png glowA.png glowB1.png glowB2.png runeA.png runeB1.png runeB2.png)
COPIED=$(addprefix images/,shadow.png glowA.png glowB1.png glowB2.png runeA.png runeB1.png runeB2.png)

CROP_RUNE=256x411+22+22
CROP_GLOW1=300x227+0+0
CROP_GLOW2=300x228+0+227
CROP_RUNE1=256x204+22+22
CROP_RUNE2=256x208+22+225

all: skeleton.atlas

images: 
	mkdir -p images

images/shadow.png: source/shadow.png images
	cp $< $@
images/glowA.png: source/glowA.png images
	cp $< $@
images/runeA.png: source/runeA.png images
	convert $< -crop $(CROP_RUNE) $@
images/glowB1.png: source/glowB.png images
	convert $< -crop $(CROP_GLOW1) $@
images/glowB2.png: source/glowB.png images
	convert $< -crop $(CROP_GLOW2) $@
images/runeB1.png: source/runeB.png images
	convert $< -crop $(CROP_RUNE1) $@
images/runeB2.png: source/runeB.png images
	convert $< -crop $(CROP_RUNE2) $@

skeleton.atlas: images $(IMAGES)
	$(TEXTURE_PACKER) $< . $@ $(PACKER_CONFIG)

clean:
	rm -rf skeleton.atlas
	rm -rf skeleton.png
	rm -rf images

