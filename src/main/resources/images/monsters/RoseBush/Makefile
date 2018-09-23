ROOT=../../../../../..
TEXTURE_PACKER=java -cp $(ROOT)/../_ModTheSpire/desktop-1.0.jar com.badlogic.gdx.tools.texturepacker.TexturePacker
PACKER_CONFIG=$(ROOT)/tools/texture-packer-config.json

all: skeletonR.atlas skeletonY.atlas skeletonW.atlas

skeletonR.atlas: images images/*.png
	$(TEXTURE_PACKER) $< . $@ $(PACKER_CONFIG)

skeletonY.atlas: images-yellow images-yellow/*.png
	$(TEXTURE_PACKER) $< . $@ $(PACKER_CONFIG)

skeletonW.atlas: images-white images-white/*.png
	$(TEXTURE_PACKER) $< . $@ $(PACKER_CONFIG)

clean:
	rm -rf skeletonR.atlas
	rm -rf skeletonY.atlas
	rm -rf skeletonW.atlas
	rm -rf skeletonR.png
	rm -rf skeletonY.png
	rm -rf skeletonW.png
