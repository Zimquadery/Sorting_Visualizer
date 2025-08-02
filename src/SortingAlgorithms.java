import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;

/**
 * A class containing all sorting algorithm implementations for visualization.
 * Each algorithm is designed to work with JavaFX Timeline animations.
 */
import java.util.HashMap;
import java.util.Map;

public class SortingAlgorithms {
    
    private ArrayList<Rectangle> rects;
    private ArrayList<Text> labels;
    private Timeline timeline;
    private int timelineDuration;
    
    // Performance tracking variables
    private int comparisons = 0;
    private int swaps = 0;
    
    // Common state variables
    private byte state = 0;
    private int i = 0;
    private int j = 0;
    
    // Merge sort specific variables
    private int[] mergeSortArray;
    private int[] tempArray;
    private int[] originalArray; // Track original positions to count actual moves
    private int currentSize;
    private int leftStart;
    private int mergeState;
    private int rightStart;
    private int mergeIndex;
    private int leftIndex;
    private int rightIndex;
    private int maxSize;
    
    // Quick sort specific variables
    private int[] quickSortArray;
    private int quickSortState = 0;
    private ArrayList<int[]> quickSortStack = new ArrayList<>();
    private int pivotIndex = 0;
    private int partitionLeft = 0, partitionRight = 0;
    private int partitionI = 0, partitionJ = 0;
    private boolean partitioningInProgress = false;
    

    // Constants for rectangle dimensions and animation
    private final int W = 15;
    private static final int BASE_DURATION_MS = 200; // Base duration for timeline animations

    // Algorithm complexity data
    private static final Map<String, String> ALGORITHM_COMPLEXITY = new HashMap<>();
    static {
        ALGORITHM_COMPLEXITY.put("Bubble Sort", "Time: O(n²), Space: O(1)");
        ALGORITHM_COMPLEXITY.put("Selection Sort", "Time: O(n²), Space: O(1)");
        ALGORITHM_COMPLEXITY.put("Insertion Sort", "Time: O(n²), Space: O(1)");
        ALGORITHM_COMPLEXITY.put("Merge Sort", "Time: O(n log n), Space: O(n)");
        ALGORITHM_COMPLEXITY.put("Quick Sort", "Time: O(n log n), Space: O(log n)");
    }

    /**
     * Returns the time and space complexity for a given algorithm name.
     * @param algorithm The name of the algorithm
     * @return Complexity string
     */
    public static String getComplexity(String algorithm) {
        return ALGORITHM_COMPLEXITY.getOrDefault(algorithm, "");
    }
    
    /**
     * Returns the current number of comparisons performed.
     * @return Number of comparisons
     */
    public int getComparisons() {
        return comparisons;
    }
    
    /**
     * Returns the current number of swaps performed.
     * @return Number of swaps
     */
    public int getSwaps() {
        return swaps;
    }
    
    /**
     * Resets the performance counters.
     */
    public void resetPerformanceCounters() {
        comparisons = 0;
        swaps = 0;
    }
    
    /**
     * Constructor for the SortingAlgorithms class.
     * @param rects List of rectangles to be sorted
     * @param labels List of text labels corresponding to the rectangles
     * @param timelineDuration Duration for animation steps in milliseconds
     */
    public SortingAlgorithms(ArrayList<Rectangle> rects, ArrayList<Text> labels, int timelineDuration) {
        this.rects = rects;
        this.labels = labels;
        this.timelineDuration = timelineDuration;
    }
    
    /**
     * Updates the animation speed for the current timeline.
     * @param newDuration New duration in milliseconds
     */
    public void updateSpeed(int newDuration) {
        this.timelineDuration = newDuration;
        // Update the timeline rate if it's currently running
        if (timeline != null) {
            // Calculate the rate based on the original duration vs new duration
            // Higher rate = faster animation (shorter duration)
            double newRate = (double) BASE_DURATION_MS / newDuration;
            timeline.setRate(newRate);
        }
    }
    
    /**
     * Sets the initial rate for a newly created timeline based on current duration.
     */
    private void setInitialTimelineRate() {
        if (timeline != null) {
            double initialRate = (double) BASE_DURATION_MS / timelineDuration;
            timeline.setRate(initialRate);
        }
    }
    
    /**
     * Stops the current animation timeline if it's running.
     */
    public void stopAnimation() {
        if (timeline != null) {
            timeline.stop();
        }
    }
    
    /**
     * Returns the current timeline for external control.
     * @return Current Timeline object
     */
    public Timeline getTimeline() {
        return timeline;
    }
    
    /**
     * Bubble Sort algorithm implementation with visualization.
     * @return Timeline object for the bubble sort animation
     */
    public Timeline bubbleSort() {
        resetState();
        
        timeline = new Timeline(new KeyFrame(Duration.millis(BASE_DURATION_MS), e -> {
            int arraySize = rects.size();

            switch (state) {
                case 0: // Initialize
                    i = 0;
                    j = 0;
                    state = 1;
                    break;

                case 1: // Compare
                    if (i >= arraySize - 1) {
                        state = 3;
                        break;
                    }

                    // Reset colors
                    for (int k = 0; k < arraySize - i; k++) {
                        rects.get(k).setFill(Color.RED);
                    }

                    // Highlight current comparison
                    if (j < arraySize - 1) {
                        rects.get(j).setFill(Color.BLUE);
                        rects.get(j + 1).setFill(Color.BLUE);
                    }

                    if (j < arraySize - i - 1 && rects.get(j).getHeight() > rects.get(j + 1).getHeight()) {
                        comparisons++; // Increment comparison counter
                        state = 2; // Need to swap
                    } else {
                        if (j < arraySize - i - 1) {
                            comparisons++; // Increment comparison counter
                        }
                        j++;
                        if (j >= arraySize - i - 1) {
                            rects.get(arraySize - i - 1).setFill(Color.GREEN);
                            i++;
                            j = 0;
                        }
                    }
                    break;

                case 2: // Swap
                    swap(j, j + 1);

                    // Move to next comparison
                    j++;
                    if (j >= arraySize - i - 1) {
                        rects.get(arraySize - i - 1).setFill(Color.GREEN);
                        i++;
                        j = 0;
                    }
                    state = 1;
                    break;

                case 3: // Done
                    for (int k = 0; k < arraySize; k++) {
                        rects.get(k).setFill(Color.GREEN);
                    }
                    timeline.stop();
                    return;
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        setInitialTimelineRate(); // Apply the correct speed from the start
        return timeline;
    }
    
    /**
     * Selection Sort algorithm implementation with visualization.
     * @return Timeline object for the selection sort animation
     */
    public Timeline selectionSort() {
        resetState();
        
        timeline = new Timeline(new KeyFrame(Duration.millis(BASE_DURATION_MS), e -> {
            int arraySize = rects.size();

            switch (state) {
                case 0: // Initialize
                    i = 0;
                    j = i + 1;
                    state = 1;
                    break;

                case 1: // Finding minimum
                    if (i >= arraySize - 1) {
                        state = 3; // Done
                        break;
                    }

                    // Reset colors
                    for (int k = 0; k < arraySize; k++) {
                        if (k < i) {
                            rects.get(k).setFill(Color.GREEN);
                        } else {
                            rects.get(k).setFill(Color.RED);
                        }
                    }

                    // Current minimum and current element
                    if (j < arraySize) {
                        rects.get(i).setFill(Color.BLUE);
                        rects.get(j).setFill(Color.BLUE);

                        // Check if new minimum found
                        if (rects.get(j).getHeight() < rects.get(i).getHeight()) {
                            comparisons++; // Increment comparison counter
                            rects.get(j).setFill(Color.ORCHID);
                            int temp = i;
                            i = j;
                            j = temp;
                            state = 2; // Need to swap
                        } else {
                            comparisons++; // Increment comparison counter
                            j++;
                            if (j >= arraySize) {
                                rects.get(i).setFill(Color.GREEN);
                                i++;
                                j = i + 1;
                                if (j >= arraySize) {
                                    state = 3;
                                }
                            }
                        }
                    }
                    break;

                case 2: // Swap
                    swap(i, j);

                    // Swap back indices
                    int temp = i;
                    i = j;
                    j = temp;

                    // Move to next element
                    j++;
                    if (j >= arraySize) {
                        rects.get(i).setFill(Color.GREEN);
                        i++;
                        j = i + 1;
                        if (j >= arraySize) {
                            state = 3;
                        }
                    }
                    state = 1;
                    break;

                case 3: // Done
                    for (int k = 0; k < arraySize; k++) {
                        rects.get(k).setFill(Color.GREEN);
                    }
                    timeline.stop();
                    return;
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        setInitialTimelineRate(); // Apply the correct speed from the start
        return timeline;
    }
    
    /**
     * Insertion Sort algorithm implementation with visualization.
     * @return Timeline object for the insertion sort animation
     */
    public Timeline insertionSort() {
        resetState();
        
        timeline = new Timeline(new KeyFrame(Duration.millis(BASE_DURATION_MS), e -> {
            int arraySize = rects.size();

            switch (state) {
                case 0: // Initialize
                    i = 1; // Start with second element
                    j = i;
                    state = 1;
                    
                    // Reset all rectangle colors
                    for (int k = 0; k < arraySize; k++) {
                        if (k < 1) {
                            rects.get(k).setFill(Color.GREEN);
                        } else {
                            rects.get(k).setFill(Color.RED);
                        }
                    }
                    break;

                case 1: // Process element
                    if (i >= arraySize) {
                        state = 3; // Done
                        break;
                    }

                    // Reset colors to show sorted vs unsorted portions
                    for (int k = 0; k < arraySize; k++) {
                        if (k < i) {
                            rects.get(k).setFill(Color.GREEN);
                        } else {
                            rects.get(k).setFill(Color.RED);
                        }
                    }

                    // Current key element we're trying to insert
                    rects.get(i).setFill(Color.BLUE);
                    
                    // Set j to current element index to start insertion process
                    j = i;
                    state = 2; // Move to comparison/swap phase
                    break;

                case 2: // Compare and swap if needed
                    // First, reset any other comparison colors
                    for (int k = 0; k < i; k++) {
                        if (k != j && k != j-1) {
                            rects.get(k).setFill(Color.GREEN);
                        }
                    }
                    
                    // Highlight the current element being inserted
                    rects.get(j).setFill(Color.BLUE);
                    
                    // Highlight the element we're comparing with
                    if (j > 0) {
                        rects.get(j-1).setFill(Color.ORCHID);
                    }
                    
                    // Check if element needs to be moved
                    if (j > 0 && rects.get(j - 1).getHeight() > rects.get(j).getHeight()) {
                        comparisons++; // Increment comparison counter
                        swap(j, j - 1);
                        j--;
                    } else {
                        if (j > 0) {
                            comparisons++; // Increment comparison counter
                        }
                        // Found correct position
                        for (int k = 0; k <= i; k++) {
                            rects.get(k).setFill(Color.GREEN);
                        }
                        
                        // Move to next element in unsorted portion
                        i++;
                        state = 1;
                        
                        // Check if we're done
                        if (i >= arraySize) {
                            state = 3;
                        }
                    }
                    break;

                case 3: // Done
                    for (int k = 0; k < arraySize; k++) {
                        rects.get(k).setFill(Color.GREEN);
                    }
                    timeline.stop();
                    return;
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        setInitialTimelineRate(); // Apply the correct speed from the start
        return timeline;
    }
    
    /**
     * Merge Sort algorithm implementation with visualization.
     * @return Timeline object for the merge sort animation
     */
    public Timeline mergeSort() {
        int arraySize = rects.size();
        
        // No need to sort if array has 1 or fewer elements
        if (arraySize <= 1) {
            if (arraySize == 1) {
                rects.get(0).setFill(Color.GREEN);
            }
            return null;
        }
        
        // Reset performance counters
        resetPerformanceCounters();
        
        // Initialize arrays and variables
        mergeSortArray = new int[arraySize];
        tempArray = new int[arraySize];
        originalArray = new int[arraySize]; // Store original array for comparison
        
        // Copy rectangle heights to arrays
        for (int i = 0; i < arraySize; i++) {
            mergeSortArray[i] = (int)rects.get(i).getHeight();
            originalArray[i] = (int)rects.get(i).getHeight(); // Keep original for comparison
            rects.get(i).setFill(Color.RED);
        }
        
        // Initialize the merge sort state variables
        mergeState = 0;
        currentSize = 1;
        leftStart = 0;
        maxSize = arraySize;
        
        timeline = new Timeline(new KeyFrame(Duration.millis(BASE_DURATION_MS), e -> {
            switch (mergeState) {
                case 0: // Initialize a new size for merging
                    if (currentSize >= maxSize) {
                        mergeState = 4;
                        break;
                    }
                    
                    leftStart = 0;
                    mergeState = 1;
                    break;
                    
                case 1: // Start merging for a specific left position
                    if (leftStart >= maxSize - 1) {
                        currentSize = currentSize * 2;
                        mergeState = 0;
                        break;
                    }
                    
                    // Calculate boundaries for this merge operation
                    rightStart = Math.min(leftStart + currentSize, maxSize);
                    int end = Math.min(leftStart + 2 * currentSize - 1, maxSize - 1);
                    
                    // Initialize indices for merging
                    leftIndex = leftStart;
                    rightIndex = rightStart;
                    mergeIndex = leftStart;
                    
                    // Highlight the subarrays being merged
                    for (int i = 0; i < maxSize; i++) {
                        if (i >= leftStart && i < rightStart) {
                            rects.get(i).setFill(Color.BLUE);
                        } else if (i >= rightStart && i <= end) {
                            rects.get(i).setFill(Color.ORCHID);
                        } else {
                            rects.get(i).setFill(Color.RED);
                        }
                    }
                    
                    // Copy data to temporary array for merging
                    for (int i = leftStart; i <= end; i++) {
                        tempArray[i] = mergeSortArray[i];
                    }
                    
                    mergeState = 2;
                    break;
                    
                case 2: // Perform one step of the merge
                    int rightEnd = Math.min(leftStart + 2 * currentSize - 1, maxSize - 1);
                    
                    // If we still have elements to merge
                    if (mergeIndex <= rightEnd) {
                        // Choose smallest element from either left or right subarray
                        if (leftIndex < rightStart && 
                           (rightIndex > rightEnd || tempArray[leftIndex] <= tempArray[rightIndex])) {
                            comparisons++; // Increment comparison counter
                            mergeSortArray[mergeIndex] = tempArray[leftIndex];
                            rects.get(mergeIndex).setHeight(tempArray[leftIndex]);
                            updateLabelPosition(mergeIndex);
                            
                            // Only count as a move if element changed position from original
                            if (originalArray[mergeIndex] != tempArray[leftIndex]) {
                                swaps++; // Count only actual position changes
                            }
                            leftIndex++;
                        } else {
                            if (rightIndex <= rightEnd) {
                                comparisons++; // Increment comparison counter
                            }
                            mergeSortArray[mergeIndex] = tempArray[rightIndex];
                            rects.get(mergeIndex).setHeight(tempArray[rightIndex]);
                            updateLabelPosition(mergeIndex);
                            
                            // Only count as a move if element changed position from original
                            if (originalArray[mergeIndex] != tempArray[rightIndex]) {
                                swaps++; // Count only actual position changes
                            }
                            rightIndex++;
                        }
                        
                        // Mark sorted element
                        rects.get(mergeIndex).setFill(Color.GREEN);
                        mergeIndex++;
                    } else {
                        // This merge is complete, move to next subarray pair
                        leftStart = leftStart + 2 * currentSize;
                        mergeState = 1;
                    }
                    break;
                    
                case 4: // Verify and finalize
                    // Verify the array is sorted
                    boolean sorted = true;
                    for (int i = 1; i < maxSize; i++) {
                        if (mergeSortArray[i - 1] > mergeSortArray[i]) {
                            sorted = false;
                            break;
                        }
                    }
                    
                    if (sorted) {
                        // Final completion - mark all sorted
                        for (int i = 0; i < maxSize; i++) {
                            rects.get(i).setFill(Color.GREEN);
                        }
                        timeline.stop();
                    } else {
                        // Reset to a smaller subarray size to ensure we get there
                        currentSize = maxSize / 4;
                        if (currentSize < 1) currentSize = 1;
                        mergeState = 0;
                    }
                    break;
            }
        }));
        
        timeline.setCycleCount(Timeline.INDEFINITE);
        setInitialTimelineRate(); // Apply the correct speed from the start
        return timeline;
    }
    
    /**
     * Quick Sort algorithm implementation with visualization.
     * @return Timeline object for the quick sort animation
     */
    public Timeline quickSort() {
        int arraySize = rects.size();
        
        // Reset performance counters
        resetPerformanceCounters();
        
        // Initialize an array to track the current state of quick sort
        quickSortArray = new int[arraySize];
        
        // Reset state variables
        quickSortStack.clear();
        quickSortState = 0;
        partitioningInProgress = false;
        
        // Copy heights to quickSortArray and set initial color to red (unsorted)
        for (int k = 0; k < arraySize; k++) {
            rects.get(k).setFill(Color.RED);
            quickSortArray[k] = (int) rects.get(k).getHeight();
        }
        
        // Add initial range (full array)
        quickSortStack.add(new int[]{0, arraySize - 1});
        
        timeline = new Timeline(new KeyFrame(Duration.millis(BASE_DURATION_MS), e -> {
            switch (quickSortState) {
                case 0: // Get next subarray to partition
                    if (quickSortStack.isEmpty()) {
                        quickSortState = 3; // Done
                        break;
                    }
                    
                    // Get next range to process
                    int[] range = quickSortStack.remove(0);
                    partitionLeft = range[0];
                    partitionRight = range[1];
                    
                    // If the range is invalid or contains one element, it's already sorted
                    if (partitionLeft >= partitionRight) {
                        if (partitionLeft == partitionRight) {
                            rects.get(partitionLeft).setFill(Color.GREEN);
                        }
                        break;
                    }
                    
                    // Reset colors to show the current range being processed
                    for (int k = 0; k < arraySize; k++) {
                        if (k >= partitionLeft && k <= partitionRight) {
                            rects.get(k).setFill(Color.RED);
                        } else {
                            rects.get(k).setFill(Color.GREEN);
                        }
                    }
                    
                    // Choose the rightmost element as pivot
                    pivotIndex = partitionRight;
                    rects.get(pivotIndex).setFill(Color.ORCHID); // Highlight pivot
                    
                    // Initialize partition pointers
                    partitionI = partitionLeft - 1;
                    partitionJ = partitionLeft;
                    partitioningInProgress = true;
                    
                    quickSortState = 1; // Move to partitioning
                    break;
                    
                case 1: // Partitioning in progress
                    // If done with partitioning
                    if (!partitioningInProgress || partitionJ >= partitionRight) {
                        // Swap pivot with position i+1
                        partitionI++;
                        
                        // Swap heights and update visual
                        swapQuickSort(partitionI, pivotIndex);
                        
                        // Mark pivot as sorted
                        rects.get(partitionI).setFill(Color.GREEN);
                        
                        // Add left and right subarrays to stack
                        if (partitionI - 1 > partitionLeft) {
                            quickSortStack.add(new int[]{partitionLeft, partitionI - 1});
                        }
                        if (partitionI + 1 < partitionRight) {
                            quickSortStack.add(new int[]{partitionI + 1, partitionRight});
                        }
                        
                        quickSortState = 0; // Get next range
                        break;
                    }
                    
                    // Highlight current comparison
                    for (int k = partitionLeft; k <= partitionRight; k++) {
                        if (k == partitionJ) {
                            rects.get(k).setFill(Color.BLUE);
                        } else if (k == pivotIndex) {
                            rects.get(k).setFill(Color.ORCHID);
                        } else if (k >= partitionLeft && k <= partitionRight) {
                            rects.get(k).setFill(Color.RED);
                        }
                    }
                    
                    // Compare current element with pivot
                    if (quickSortArray[partitionJ] < quickSortArray[pivotIndex]) {
                        comparisons++; // Increment comparison counter
                        partitionI++;
                        swapQuickSort(partitionI, partitionJ);
                    } else {
                        comparisons++; // Increment comparison counter
                    }
                    
                    // Move to next element
                    partitionJ++;
                    break;
                    
                case 3: // Done
                    // Check if array is sorted
                    boolean sorted = true;
                    for (int k = 1; k < arraySize; k++) {
                        if (quickSortArray[k - 1] > quickSortArray[k]) {
                            sorted = false;
                            break;
                        }
                    }
                    
                    if (sorted) {
                        for (int k = 0; k < arraySize; k++) {
                            rects.get(k).setFill(Color.GREEN);
                        }
                        timeline.stop();
                    }
                    break;
            }
        }));
        
        timeline.setCycleCount(Timeline.INDEFINITE);
        setInitialTimelineRate(); // Apply the correct speed from the start
        return timeline;
    }
    
    /**
     * Swaps the heights of two rectangles and updates their labels.
     * @param index1 The index of the first rectangle.
     * @param index2 The index of the second rectangle.
     */
    private void swap(int index1, int index2) {
        if (index1 < 0 || index1 >= rects.size() || index2 < 0 || index2 >= rects.size()) {
            return; // Invalid indices
        }
        
        Rectangle rect1 = rects.get(index1);
        Rectangle rect2 = rects.get(index2);
        
        double tempHeight = rect1.getHeight();
        rect1.setHeight(rect2.getHeight());
        rect2.setHeight(tempHeight);
        
        updateLabelPosition(index1);
        updateLabelPosition(index2);
        
        // Increment swap counter
        swaps++;
    }
    
    /**
     * Swaps elements in the quickSortArray and updates the visual representation.
     * @param index1 First index to swap
     * @param index2 Second index to swap
     */
    private void swapQuickSort(int index1, int index2) {
        if (index1 < 0 || index1 >= quickSortArray.length || index2 < 0 || index2 >= quickSortArray.length) {
            return;
        }
        
        // Swap in array
        int temp = quickSortArray[index1];
        quickSortArray[index1] = quickSortArray[index2];
        quickSortArray[index2] = temp;
        
        // Update visual rectangles
        rects.get(index1).setHeight(quickSortArray[index1]);
        rects.get(index2).setHeight(quickSortArray[index2]);
        
        // Update labels
        updateLabelPosition(index1);
        updateLabelPosition(index2);
        
        // Increment swap counter
        swaps++;
    }
    
    /**
     * Updates the position and text of a label for a specific rectangle.
     * @param index the index of the rectangle whose label needs updating
     */
    private void updateLabelPosition(int index) {
        if (index < 0 || index >= rects.size() || index >= labels.size()) {
            return; // Skip invalid indices
        }
        
        Rectangle rect = rects.get(index);
        Text label = labels.get(index);
        
        // Update label text and position
        label.setText(String.valueOf((int)rect.getHeight()));
        // Recalculate X position to center the label over its rectangle
        label.setX(rect.getX() + W / 2 - label.getBoundsInLocal().getWidth() / 2);
        label.setY(rect.getY() - 5); // Position above the bar
    }
    
    /**
     * Resets the state variables for a new sorting operation.
     */
    private void resetState() {
        state = 0;
        i = 0;
        j = 0;
        resetPerformanceCounters();
    }
}
