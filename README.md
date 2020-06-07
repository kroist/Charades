# Charades
Java drawing and word-guessing game, inspired by charades.
### Rules:
#### Multiplayer
One player gets a word from the program and he draws that object it on the whiteboard.
Other players have to guess this word in the limited time. The player who guesses first, wins and will be drawing in the next game. If no one guesses, the next player will be chosen randomly.


#### Singleplayer
Player has to draw a guessed object in 30 seconds, so the computer guesses it.

### Features:
* You can start a new game lobby, or connect to existing one
* A large library of words to play with
* Leaderboards
* Convenient GUI

### Frameworks and Libraries used:
* JavaFX
* Deeplearning4j and TensorFlow

### Running application
* Jar files are in the release section
* First, you have run charades-server.jar file with your port_number
```properties
java -jar charades-server.jar port_number
```  
* To start client application, run the charades-windows.jar or charades-linux.jar file
```properties
java -jar charades-windows.jar
java -jar charades-linux.jar
```
* If you have Mac OS, or you want to compile code to binaries, ```git pull``` the repository and execute following commands in the root folder of repository ```mvn install -pl tools```, ```mvn javafx:run -pl client``` (for Mac Os uncomment macosx-x86_64 properties in pom.xml in client module and comment other properties) 
