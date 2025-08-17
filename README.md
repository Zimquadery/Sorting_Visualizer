# Sorting_Vizualizer

Sorting_Vizualizer is a JavaFX desktop application for visualizing and interacting with classic sorting algorithms. It provides an educational, animated interface to help users understand how different sorting algorithms work step by step.

## Features
- Visualize Bubble Sort, Selection Sort, Insertion Sort, Merge Sort, and Quick Sort
- Step-by-step animations using JavaFX Timeline
- Custom array input and random array generation
- Adjustable animation speed
- View detailed explanations and pseudocode for each algorithm
- Modern, responsive UI with clear color legends

## Getting Started
1. **Clone the repository**
2. **Open in your Java IDE** (VS Code, IntelliJ, etc.)
3. **Run `src/App.java`** as a JavaFX application

> **Note:** JavaFX must be configured in your IDE. No external dependencies or build scripts are required.

## Project Structure
- `src/App.java` - Application entry point
- `src/view.fxml` - UI layout (FXML)
- `src/Controller.java` - Main controller for UI logic
- `src/SortingAlgorithms.java` - Sorting algorithm implementations with animation
- `src/AlgorithmDescription.java` - Algorithm explanations and pseudocode
- `src/App.css` - Centralized UI styling

## How to Add a New Algorithm
1. Implement the algorithm in `SortingAlgorithms.java` (with animation support)
2. Add its name to the `Algorithm` ChoiceBox in `Controller.java` and `view.fxml`
3. Add a description and pseudocode in `AlgorithmDescription.java`

## Animation System
- The application uses JavaFX's `Timeline` and `KeyFrame` classes to create smooth animations.
- Each sorting algorithm is implemented as a state machine where different states represent different phases of the algorithm execution
- The states are `initial`, `comparing`, `swapping` and `sorted`.
- The visualization updates at each step to show the current state of the sorting process
- Color coding is used to indicate different element states (unsorted, comparing, sorted, etc.)
- Performance metrics (comparisons and swaps) are tracked and displayed in real-time

## Contributing
Pull requests are welcome! Please follow the existing code style and update documentation as needed.
