# 💡 Rush Hour Solver
An interactive CLI and GUI program in Java, implements various _Pathfinding_ algorithm to find solutions in the [**Rush Hour**](https://youtu.be/IWigXwmfcNY?t=40) game.

---

<!-- CONTRIBUTOR -->
 <div align="center" id="contributor">
   <strong>
     <h3> Contributors </h3>
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

### Preview

<div align="center">

<img src="https://github.com/user-attachments/assets/5f1c493b-01df-46de-b841-fc4fd50e4956" width="100%" alt="cover">

</div>

<br>

---

### Description

Finding your Rush Hour Puzzle solution by using pathfinding algorithms, including:

- **Uniform Cost Search (UCS)**: Uninformed search algorithm designed to find the least-cost path $g(n)$ in a weighted graph
- **Greedy Best First Search (GBFS)**: Informed search algorithm that selects the path to expand based on a heuristic evaluation $h(n)$ of the current state’s desirability in terms of reaching the goal.
- **A\* (A Star)**: Combination of UCS and GBFS and utilizes both $g(n)$ and $h(n)$ into it's evalution function $f(n)$, more robust algorithm.
- **Fringe Search**: Alternative to the A* algorithm that is more efficient in terms of memory usage, combines the concept of A* with _iterative deepening_ to control node expansion.

All informed search implemented by two heuristics:

- **Manhattan Distance**: The minimum number of moves based on the sum of horizontal and vertical distances the primary car must travel to reach the exit.
- **Blocking Cars**: Counts how many other vehicles are in the way of the primary car’s path to the exit.

The program is developed using Java and JavaFX, featuring an animated user interface (GUI) that makes it easy for users to visualize the solution’s movement path.

### Input Format

To succesfully load a `.txt` file, the format of the `.txt` should follow this layout:

```txt
# A B                 (Rows and Columns of the actual puzzle grid)
# N                   (Numbers of Car without the primary car)
# Grid Configuration

# Sample:

6 6
11
GBB.L.
GHI.LM
GHIPPMK
CCCZ.M
..JZDD
EEJFF.
```
> [!IMPORTANT]
> Exit door always using the letter "K" and Primary car using the letter "P" 

---

### Features

### *This project contains:*

- **Main Program as Puzzle Solver with various _pathfinding_ and _heuristics_ approaches**
- **`[Bonus]` Animation Preview of the Solutions**
- **`[Bonus]` GUI with including Available Test Cases, Outputs, File & Result Preview**

### *Take a peek:*

- **The core logic is located at `~/src/app/src/main/java/src/`**
- **Create Pull Request and Collaborate for project improvement**

---

### ️Running Locally

> [!NOTE]  
> Before you start, install these dependencies first with links given :D
> - [**Git**](Git-url) - 2.47.0 or later
> - [**Java**](Java-url) - 21 or later
> - [**Gradle**](Gradle-url) - 8.12 or later *(if using build)*
> - [**JavaFX**](JavaFX-url) - 23 *(if using build)*

### Initialization

- **Clone the repository**

  ```
  git clone https://github.com/zirachw/Tucil3_13523004_13523098
  ```
  
### Command-Line Interface (CLI) Mode

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

### Graphical User Interface (GUI) Mode

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

> [!TIP]  
>
> Or just double-click it as usual executables :D

### ️Build Locally

- **Clone the repository**

  ```
  git clone https://github.com/zirachw/Tucil3_13523004_13523098
  ```

- Run the following command to start the build:

  ```bash
   cd bin/src
   ./gradlew clean
   /gradlew build
   ```
  
<br>

> [!IMPORTANT]  
>
> The `app.jar` will be in `~/src/app/bin/libs/app.jar`, you may move it to `~/bin/src` to use the same run command:
> 
> Build using `Gradle` requires you to install additional dependencies.

---

### Project Structure

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
### Miscellaneous

<br>

<div>
 
| No | Specification | Yes | No |
| --- | --- | --- | --- |
| 1 | Program compiled successfully without errors | ✔️ | |
| 2 | The program runs successfully | ✔️ | |
| 3 | The solution provided by the program is correct and complies with the rules of the game | ✔️ | |
| 4 | The program can read the input .txt file and save the solution in the form of a print board step by step in the .txt file | ✔️ | |
| 5 | **[Bonus]** Implementation of alternative pathfinding algorithms | ✔️ | |
| 6 | **[Bonus]** Implementation of 2 or more alternative heuristics | ✔️ | |
| 7 | **[Bonus]** Program has GUI | ✔️ | |
| 6 | Program and report made by (group) itself | ✔️ | |

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
