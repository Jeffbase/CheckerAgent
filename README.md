
# Checker Agent

This project implements a checker agent using Java, designed to simulate and optimize checker game moves using advanced heuristics and alpha-beta pruning.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Introduction

The checker agent simulates a checker game where moves are calculated based on a heuristic score. The agent is capable of performing both single moves and a series of moves using alpha-beta pruning to forecast the opponent's potential responses.

## Features

- **Single Move Calculation:** Quickly calculates the best single move based on the current game state.
- **Alpha-Beta Pruning:** Implements alpha-beta pruning to optimize move selection process during gameplay, enhancing performance by pruning unnecessary branches in the search tree.
- **Heuristic Analysis:** Utilizes complex heuristics to evaluate board states, considering factors such as piece count, king status, and board control.
- **Adaptive Depth Adjustment:** Dynamically adjusts the depth of search based on the remaining time to ensure timely responses without sacrificing decision quality.

## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

- Java 11 or higher
- Basic knowledge of Java and command line operations

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/checker-agent.git
   ```
2. Navigate to the cloned directory:
   ```sh
   cd checker-agent
   ```

## Usage

To run the checker agent, follow these steps:

1. Compile the Java files (if your IDE does not handle this):
   ```sh
   javac agent.java
   ```
2. Run the compiled Java program:
   ```sh
   java agent
   ```

Input and output files are used to provide the game state and receive the move sequence. Make sure `input.txt` is in the root directory with the appropriate format as expected by the program.

## Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

Distributed under the MIT License. See `LICENSE` for more information.
