ROOT=../../../../../..
TEXTURE_PACKER=java -cp $(ROOT)/../_ModTheSpire/desktop-1.0.jar com.badlogic.gdx.tools.texturepacker.TexturePacker
PACKER_CONFIG=$(ROOT)/tools/texture-packer-config.json

all: skeleton.atlas

skeleton.atlas: images images/*.png
	$(TEXTURE_PACKER) $< . $@ $(PACKER_CONFIG)

clean:
	rm -rf skeleton.atlas
	rm -rf skeleton.png
