
all:
	make -C src/main/resources/images/cards
	make -C src/main/resources/images/powers
	mvn package

clean:
	mvn clean
	make clean -C src/main/resources/images/cards
	make clean -C src/main/resources/images/powers
