GS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java  -cp ./src/ -d ./

CLASSES = \
	./src/Constants.java \
	./src/FNV1aHash.java \
	./src/Node.java \
	./src/Finger.java \
	./src/Server.java \
	./src/DictionaryLoader.java\
	./src/Client.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
