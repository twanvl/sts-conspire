.PHONY: all clean

all:
	make -C src/main/resources/conspire/images/cards
	make -C src/main/resources/conspire/images/powers
	make -C src/main/resources/conspire/images/monsters
	make -C src/main/resources/conspire/images/relics
	mvn package

clean:
	mvn clean
	make clean -C src/main/resources/conspire/images/cards
	make clean -C src/main/resources/conspire/images/powers
	make clean -C src/main/resources/conspire/images/monsters
	make clean -C src/main/resources/conspire/images/relics
