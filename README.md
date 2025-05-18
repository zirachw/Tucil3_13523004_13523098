# 💡 Rush Hour Solver
An interactive CLI and GUI program in Java, implements various _Pathfinding_ algorithm to find solutions in the [**Rush Hour**](https://youtu.be/IWigXwmfcNY?t=40) game.

---

<!-- CONTRIBUTOR -->
 <div align="center" id="contributor">
   <strong>
     <h3> Acep Villagers 🏚 </h3>
     <table align="center">
       <tr align="center">
         <td>NIM</td>
         <td>Name</td>
         <td>GitHub</td>
       </tr>
       <tr align="center">
         <td>13523004</td>
         <td>Razi Rachman Widyadhana</td>
         <td><a href="https://github.com/zirachw">@zirachw</a></td>
       </tr>
       <tr align="center">
         <td>13523098</td>
         <td>Muhammad Adha Ridwan</td>
         <td><a href="https://github.com/adharidwan">@adharidwan</a></td>
       </tr>
     </table>
   </strong>
 </div>

<div align="center">
  <h3 align="center"> Tech Stacks </h3>

  <p align="center">
    
[![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)][Java-url]
[![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)][Gradle-url]
[![JavaFX](https://img.shields.io/badge/javafx-%23FF0000.svg?style=for-the-badge&logo=javafx&logoColor=white)][JavaFX-url]
  
  </p>
</div>

---

### 🔎 Preview

<br>

---

### ✨ Features

### *This project contains:*

- **Main Program as Puzzle Solver with various _pathfinding_ and _heuristics_ approaches**
- **`[Bonus]` Image Preview of the Solutions**
- **`[Bonus]` GUI with including Available Test Cases, Outputs, File & Result Preview**

### *Space for Improvement:*

- **Lorem Ipsum**

### *Take a peek:*

- **The core logic is located at `~/src/app/src/main/java/src/`**
- **There is an additional `GUI` folder for the GUI program**
- **Create Pull Request and Collaborate for project improvement**

---

### ️📦 Running Locally

> [!NOTE]  
> Before you start, install these dependencies first with links given :D
> - [**Git**](Git-url) - 2.47.0 or later
> - [**Java**](Java-url) - 21 or later
> - [**Gradle**](Gradle-url) - 8.12 or later *(if using build)*
> - [**JavaFX**](JavaFX-url) - 23 *(if using build)*

### 🔧 Initialization

- **Clone the repository**

  ```
  git clone https://github.com/zirachw/Tucil3_13523004_13523098
  ```
  
### 💻 Command-Line Interface (CLI) Mode

- Run the following command to start the application in `CLI` mode:

  ```bash
   cd bin/src
   java -jar app.jar -cli
   ```
  
- Alternatively, if you want to build and run it using `Gradle`:
   ```bash
   cd src
   ./gradlew run --quiet --warning-mode=none --console=plain --args="-cli"
   ```

### 🖼 Graphical User Interface (GUI) Mode

- Run the following command to start the application in `GUI` mode:

  ```bash
   cd bin/src
   java -jar app.jar -gui
   ```
  
- Alternatively, if you want to build and run it using `Gradle`:
   ```bash
   cd src
   ./gradlew run --quiet --warning-mode=none --console=plain --args="-gui"
   ```
   
<br>

> [!IMPORTANT]  
>
> Build using `Gradle` requires you to install additional dependencies.

---

### 🔧 Project Structure

<br> 

```
📂 bin          
│   └── 📂 src
│       └── app.jar                         # the program .jar file
📂 doc          
│   └── Tucil3_13523004_13523098.pdf        # Explanation document of the program
│                       
📂 src                                      # contains Java source codes
│   └── 📂 app    
│        ├── ...
│        │    └── ...
│        │
│        └── 📂 src
│            ├── 📂 main
│            │    └── 📂 java
│            │          └── 📂 src
│            │               ├── 📂 ADT
│            │               │   ├── Board.java
│            │               │   └── Car.java
│            │               │   └── State.java
│            │               │
│            │               ├── 📂 Algorithm
│            │               │   ├── AStar.java
│            │               │   ├── GBFS.java
│            │               │   └── UCS.java
│            │               │
│            │               ├── 📂 CLI
│            │               │   ├── CLI.java
│            │               │
│            │               ├── 📂 GUI
│            │               │   ├── GUI.java
│            │               │   ├── OutputGUI.java
│            │               │   └── PuzzleImage.java
│            │               │
│            │               ├── 📂 IO
│            │               │   ├── Input.java
│            │               │   └── Output.java
│            │               │
│            │               └── Main.java
│            │
│            └── build.gradle.kts     # build setups
│                       
├── 📂 test                          # test cases
│   └── ...
│
└── README.md                         # brief explanation of the program

```

---
### 📃 Miscellaneous

<br>

<div>
 
| No | Specification | Yes | No |
| --- | --- | --- | --- |
| 1 | Program compiled successfully without errors | | |
| 2 | The program runs successfully | | |
| 3 | The solution provided by the program is correct and complies with the rules of the game | | |
| 4 | The program can read the input .txt file and save the solution in the form of a print board step by step in the .txt file | | |
| 5 | **[Bonus]** Implementation of alternative pathfinding algorithms | | |
| 6 | **[Bonus]** Implementation of 2 or more alternative heuristics | | |
| 7 | **[Bonus]** Program has GUI | | |
| 6 | Program and report made by (group) itself | | |

</div>

---

<h3 align="center">
Acep Villagers • © 2025 • 13523004 - 13523098
</h3>

<!-- MARKDOWN LINKS & IMAGES -->
[Java-url]: https://www.java.com/en/
[Gradle-url]: https://gradle.org/
[JavaFX-url]: https://openjfx.io/
[Git-url]: https://git-scm.com/
