🎲 Ludo Star Game

A desktop-based Ludo board game developed using Java and Swing as a Software Development project.

The project implements the core rules and gameplay of Ludo, including multiple players, dice rolling, token movement, capturing, safe zones, animations, sound effects, player/color selection, and winner detection.

---

📌 Project Information

Information| Details
Project Name| Ludo Star Game
Language| Java
GUI Framework| Java Swing
Course| Software Development I
Project Type| Academic / University Project
Players| 2–4 Players

---

🎮 Features

- 🎲 Dice rolling system
- 👥 Support for 2, 3, and 4 players
- 🎨 Player color selection
- 🧩 Four tokens for each player
- 🚶 Token movement animation
- 🏠 Token entry from the yard
- 🛡️ Safe zones
- ⚔️ Token capture system
- ⭐ Extra turn after rolling a 6
- 🔄 Extra turn after capturing an opponent
- 🚫 Three consecutive sixes rule
- 🏁 Token HOME/finish system
- 🏆 Automatic winner detection
- 🔊 Dice, movement, capture, HOME, and victory sound effects
- 🎵 Lobby and game-start audio
- 📢 Game status notifications
- 📝 In-game activity log
- 🔄 New Game system
- 🖥️ Graphical user interface using Java Swing

---

🏗️ Project Structure

Ludo Star/
│
├── Main.java
├── GameController.java
├── GameFrame.java
├── Board.java
├── Player.java
├── Token.java
├── Die.java
├── GameAnimator.java
├── SoundManager.java
├── GameConstants.java
├── PlayerColor.java
│
├── sounds/
│   ├── dice.wav
│   ├── move.wav
│   ├── enter.wav
│   ├── capture.wav
│   ├── home.wav
│   ├── win.wav
│   ├── lobby.wav
│   └── game_start.wav
│
└── README.md

---

🧩 Main Classes

"Main.java"

The entry point of the application. It starts the game using the Swing Event Dispatch Thread.

"GameController.java"

Handles the main game logic, including:

- Player turns
- Dice rolls
- Token movement
- Token selection
- Capturing
- Extra turns
- Winning conditions
- New Game functionality
- Active player and color management

"GameFrame.java"

Creates the main graphical user interface and displays:

- Game board
- Player information
- Dice information
- Game status
- Activity log
- Game controls

"Board.java"

Responsible for drawing the Ludo board, tokens, paths, safe zones, and handling board-related calculations.

"Player.java"

Represents a player and manages the player's four tokens.

"Token.java"

Represents an individual Ludo token and stores its current position and state.

"Die.java"

Handles dice rolling and stores the latest dice result.

"GameAnimator.java"

Provides simple animations for token movement.

"SoundManager.java"

Handles game audio and sound effects.

"GameConstants.java"

Stores important game constants such as board size, token count, path length, and finishing position.

"PlayerColor.java"

Defines the available player colors and their corresponding starting positions.

---

🎯 Game Rules Implemented

The game follows the main rules of Ludo implemented in the project.

Rolling a 6

A player receives an extra turn after rolling a 6, provided the three-consecutive-sixes rule is not triggered.

Entering the Board

A token in the yard can enter the board when the player rolls a 6.

Token Movement

Tokens move according to the number rolled on the dice.

Capturing

When a token lands on an opponent's non-safe position, the opponent's token is sent back to its yard.

A successful capture gives the current player an extra turn.

Safe Zones

Tokens located on safe positions cannot be captured.

Three Consecutive Sixes

If a player rolls three sixes consecutively, the player's turn is forfeited.

Winning

A player wins when all four of their tokens reach HOME.

---

🖥️ Running the Project

Requirements

- Java Development Kit (JDK) 17 or later
- Windows, Linux, or macOS
- Java Swing support

Compile

Open a terminal in the project directory and run:

javac *.java

Run

java Main

---

🔊 Sound Files

The project uses ".wav" audio files for different game events.

File| Purpose
"dice.wav"| Dice rolling
"move.wav"| Token movement
"enter.wav"| Token entering the board
"capture.wav"| Capturing an opponent
"home.wav"| Token reaching HOME
"win.wav"| Winning the game
"lobby.wav"| Lobby background music
"game_start.wav"| Starting a new game

---

🧠 Technologies Used

- Java
- Java Swing
- Java AWT
- Java Timer
- Object-Oriented Programming
- Event-Driven Programming
- File-based audio playback

---

📚 Academic Purpose

This project was developed as part of the Software Development I course.

The project demonstrates practical implementation of:

- Object-Oriented Programming
- Java GUI development
- Event handling
- Game logic
- Class relationships
- Animation
- Audio integration
- User interaction
- Debugging and testing

---

👨‍💻 Developer

Maisha Jaman Mahi

Software Development I Project

---

📄 Project Status

Completed

The project includes the main Ludo gameplay system, graphical interface, player/color selection, animations, sound effects, game notifications, and winner detection.
