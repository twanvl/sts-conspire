.PHONY: all clean

all:
	make -C src/main/resources/images/cards
	make -C src/main/resources/images/powers
	make -C src/main/resources/images/monsters
	mvn package

clean:
	mvn clean
	make clean -C src/main/resources/images/cards
	make clean -C src/main/resources/images/powers
	make clean -C src/main/resources/images/monsters
