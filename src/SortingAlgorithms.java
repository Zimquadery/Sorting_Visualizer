import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SortingAlgorithms {
    
    private ArrayList<Rectangle> rects;
    private ArrayList<Text> labels;
    private Timeline timeline;
    private int timelineDuration;
    private int comparisons = 0;
    private int swaps = 0;
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
    
    private final int W = 15;
    private static final int BASE_DURATION_MS = 200;

    // Algorithm complexity data
    private static final Map<String, String> ALGORITHM_COMPLEXITY = new HashMap<>();
    static {
        ALGORITHM_COMPLEXITY.put("Bubble Sort", "Time: O(n²), Space: O(1)");
        ALGORITHM_COMPLEXITY.put("Selection Sort", "Time: O(n²), Space: O(1)");
        ALGORITHM_COMPLEXITY.put("Insertion Sort", "Time: O(n²), Space: O(1)");
        ALGORITHM_COMPLEXITY.put("Merge Sort", "Time: O(n log n), Space: O(n)");
        ALGORITHM_COMPLEXITY.put("Quick Sort", "Time: O(n log n), Space: O(log n)");
    }

    public static String getComplexity(String algorithm) {
        return ALGORITHM_COMPLEXITY.getOrDefault(algorithm, "");
    }
    
    public int getComparisons() {
        return comparisons;
    }
    
    public int getSwaps() {
        return swaps;
    }

    public void resetPerformanceCounters() {
        comparisons = 0;
        swaps = 0;
    }
    
    public SortingAlgorithms(ArrayList<Rectangle> rects, ArrayList<Text> labels, int timelineDuration) {
        this.rects = rects;
        this.labels = labels;
        this.timelineDuration = timelineDuration;
    }
    
    public void updateSpeed(int newDuration) {
        this.timelineDuration = newDuration;
        if (timeline != null) {
            double newRate = (double) BASE_DURATION_MS / newDuration;
            timeline.setRate(newRate);
        }
    }
    
    private void setInitialTimelineRate() {
        if (timeline != null) {
            double initialRate = (double) BASE_DURATION_MS / timelineDuration;
            timeline.setRate(initialRate);
        }
    }
    
    // Stops the current animation timeline if it's running.
    public void stopAnimation() {
        if (timeline != null) {
            timeline.stop();
        }
    }
    
    public Timeline getTimeline() {
        return timeline;
    }
    
    public Timeline bubbleSort() {
        resetState();
        timeline = new Timeline(new KeyFrame(Duration.millis(BASE_DURATION_MS), e -> {
            int arraySize = rects.size();
            switch (state) {
                case 0:
                    i = 0;
                    j = 0;
                    state = 1;
                    break;
                case 1:
                    if (i >= arraySize - 1) {
                        state = 3;
                        break;
                    }
                    for (int k = 0; k < arraySize - i; k++) {
                        rects.get(k).setFill(Color.RED);
                    }
                    if (j < arraySize - 1) {
                        rects.get(j).setFill(Color.BLUE);
                        rects.get(j + 1).setFill(Color.BLUE);
                    }
                    if (j < arraySize - i - 1 && rects.get(j).getHeight() > rects.get(j + 1).getHeight()) {
                        comparisons++;
                        state = 2;
                    } else {
                        if (j < arraySize - i - 1) {
                            comparisons++;
                        }
                        j++;
                        if (j >= arraySize - i - 1) {
                            rects.get(arraySize - i - 1).setFill(Color.GREEN);
                            i++;
                            j = 0;
                        }
                    }
                    break;
                case 2:
                    swap(j, j + 1);
                    j++;
                    if (j >= arraySize - i - 1) {
                        rects.get(arraySize - i - 1).setFill(Color.GREEN);
                        i++;
                        j = 0;
                    }
                    state = 1;
                    break;
                case 3:
                    for (int k = 0; k < arraySize; k++) {
                        rects.get(k).setFill(Color.GREEN);
                    }
                    timeline.stop();
                    return;
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        setInitialTimelineRate();
        return timeline;
    }
    
    
    public Timeline selectionSort() {
        resetState();
        timeline = new Timeline(new KeyFrame(Duration.millis(BASE_DURATION_MS), e -> {
            int arraySize = rects.size();
            switch (state) {
                case 0:
                    i = 0;
                    j = i + 1;
                    state = 1;
                    break;
                case 1:
                    if (i >= arraySize - 1) {
                        state = 3;
                        break;
                    }
                    for (int k = 0; k < arraySize; k++) {
                        if (k < i) {
                            rects.get(k).setFill(Color.GREEN);
                        } else {
                            rects.get(k).setFill(Color.RED);
                        }
                    }
                    if (j < arraySize) {
                        rects.get(i).setFill(Color.BLUE);
                        rects.get(j).setFill(Color.BLUE);
                        if (rects.get(j).getHeight() < rects.get(i).getHeight()) {
                            comparisons++;
                            rects.get(j).setFill(Color.ORCHID);
                            int temp = i;
                            i = j;
                            j = temp;
                            state = 2;
                        } else {
                            comparisons++;
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
                case 2:
                    swap(i, j);
                    int temp = i;
                    i = j;
                    j = temp;
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
                case 3:
                    for (int k = 0; k < arraySize; k++) {
                        rects.get(k).setFill(Color.GREEN);
                    }
                    timeline.stop();
                    return;
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        setInitialTimelineRate();
        return timeline;
    }
    
    public Timeline insertionSort() {
        resetState();
        timeline = new Timeline(new KeyFrame(Duration.millis(BASE_DURATION_MS), e -> {
            int arraySize = rects.size();
            switch (state) {
                case 0:
                    i = 1;
                    j = i;
                    state = 1;
                    for (int k = 0; k < arraySize; k++) {
                        if (k < 1) {
                            rects.get(k).setFill(Color.GREEN);
                        } else {
                            rects.get(k).setFill(Color.RED);
                        }
                    }
                    break;
                case 1:
                    if (i >= arraySize) {
                        state = 3;
                        break;
                    }
                    for (int k = 0; k < arraySize; k++) {
                        if (k < i) {
                            rects.get(k).setFill(Color.GREEN);
                        } else {
                            rects.get(k).setFill(Color.RED);
                        }
                    }
                    rects.get(i).setFill(Color.BLUE);
                    j = i;
                    state = 2;
                    break;
                case 2:
                    for (int k = 0; k < i; k++) {
                        if (k != j && k != j-1) {
                            rects.get(k).setFill(Color.GREEN);
                        }
                    }
                    rects.get(j).setFill(Color.BLUE);
                    if (j > 0) {
                        rects.get(j-1).setFill(Color.ORCHID);
                    }
                    if (j > 0 && rects.get(j - 1).getHeight() > rects.get(j).getHeight()) {
                        comparisons++;
                        swap(j, j - 1);
                        j--;
                    } else {
                        if (j > 0) {
                            comparisons++;
                        }
                        for (int k = 0; k <= i; k++) {
                            rects.get(k).setFill(Color.GREEN);
                        }
                        i++;
                        state = 1;
                        if (i >= arraySize) {
                            state = 3;
                        }
                    }
                    break;
                case 3:
                    for (int k = 0; k < arraySize; k++) {
                        rects.get(k).setFill(Color.GREEN);
                    }
                    timeline.stop();
                    return;
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        setInitialTimelineRate();
        return timeline;
    }
    
    public Timeline mergeSort() {
        int arraySize = rects.size();
        
        if (arraySize <= 1) {
            if (arraySize == 1) {
                rects.get(0).setFill(Color.GREEN);
            }
            return null;
        }
        
        resetPerformanceCounters();
        
        mergeSortArray = new int[arraySize];
        tempArray = new int[arraySize];
        originalArray = new int[arraySize]; // Store original array for comparison
        for (int i = 0; i < arraySize; i++) {
            mergeSortArray[i] = (int)rects.get(i).getHeight();
            originalArray[i] = (int)rects.get(i).getHeight(); // Keep original for comparison
            rects.get(i).setFill(Color.RED);
        }
        
        mergeState = 0;
        currentSize = 1;
        leftStart = 0;
        maxSize = arraySize;
        
        timeline = new Timeline(new KeyFrame(Duration.millis(BASE_DURATION_MS), e -> {
            switch (mergeState) {
                case 0:
                    if (currentSize >= maxSize) {
                        mergeState = 4;
                        break;
                    }
                    
                    leftStart = 0;
                    mergeState = 1;
                    break;
                    
                case 1: 
                    if (leftStart >= maxSize - 1) {
                        currentSize = currentSize * 2;
                        mergeState = 0;
                        break;
                    }
                    
                    // Calculate boundaries for this merge operation
                    rightStart = Math.min(leftStart + currentSize, maxSize);
                    int end = Math.min(leftStart + 2 * currentSize - 1, maxSize - 1);
                    leftIndex = leftStart;
                    rightIndex = rightStart;
                    mergeIndex = leftStart;
                    
                    for (int i = 0; i < maxSize; i++) {
                        if (i >= leftStart && i < rightStart) {
                            rects.get(i).setFill(Color.BLUE);
                        } else if (i >= rightStart && i <= end) {
                            rects.get(i).setFill(Color.ORCHID);
                        } else {
                            rects.get(i).setFill(Color.RED);
                        }
                    }
                    for (int i = leftStart; i <= end; i++) {
                        tempArray[i] = mergeSortArray[i];
                    }
                    
                    mergeState = 2;
                    break;
                    
                case 2:
                    int rightEnd = Math.min(leftStart + 2 * currentSize - 1, maxSize - 1);
                    
                    // If we still have elements to merge
                    if (mergeIndex <= rightEnd) {
                        if (leftIndex < rightStart && 
                           (rightIndex > rightEnd || tempArray[leftIndex] <= tempArray[rightIndex])) {
                            comparisons++;
                            mergeSortArray[mergeIndex] = tempArray[leftIndex];
                            rects.get(mergeIndex).setHeight(tempArray[leftIndex]);
                            updateLabelPosition(mergeIndex);
                            if (originalArray[mergeIndex] != tempArray[leftIndex]) {
                                swaps++;
                            }
                            leftIndex++;
                        } else {
                            if (rightIndex <= rightEnd) {
                                comparisons++;
                            }
                            mergeSortArray[mergeIndex] = tempArray[rightIndex];
                            rects.get(mergeIndex).setHeight(tempArray[rightIndex]);
                            updateLabelPosition(mergeIndex);
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
                    
                case 4:
                    // Verify the array is sorted
                    boolean sorted = true;
                    for (int i = 1; i < maxSize; i++) {
                        if (mergeSortArray[i - 1] > mergeSortArray[i]) {
                            sorted = false;
                            break;
                        }
                    }
                    
                    if (sorted) {
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
    
    public Timeline quickSort() {
        int arraySize = rects.size();
        resetPerformanceCounters();
        quickSortArray = new int[arraySize];
        quickSortStack.clear();
        quickSortState = 0;
        partitioningInProgress = false;
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
                    for (int k = 0; k < arraySize; k++) {
                        if (k >= partitionLeft && k <= partitionRight) {
                            rects.get(k).setFill(Color.RED);
                        } else {
                            rects.get(k).setFill(Color.GREEN);
                        }
                    }
                    pivotIndex = partitionRight;
                    rects.get(pivotIndex).setFill(Color.ORCHID);
                    partitionI = partitionLeft - 1;
                    partitionJ = partitionLeft;
                    partitioningInProgress = true;
                    
                    quickSortState = 1; // Move to partitioning
                    break;
                    
                case 1:
                    if (!partitioningInProgress || partitionJ >= partitionRight) {
                        partitionI++;
                        swapQuickSort(partitionI, pivotIndex);
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
                        comparisons++;
                        partitionI++;
                        swapQuickSort(partitionI, partitionJ);
                    } else {
                        comparisons++;
                    }
                    partitionJ++;
                    break;
                    
                case 3:
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

    private void swap(int index1, int index2) {
        if (index1 < 0 || index1 >= rects.size() || index2 < 0 || index2 >= rects.size()) {
            return;
        }
        Rectangle rect1 = rects.get(index1);
        Rectangle rect2 = rects.get(index2);
        double tempHeight = rect1.getHeight();
        rect1.setHeight(rect2.getHeight());
        rect2.setHeight(tempHeight);
        updateLabelPosition(index1);
        updateLabelPosition(index2);
        swaps++;
    }
    
    private void swapQuickSort(int index1, int index2) {
        if (index1 < 0 || index1 >= quickSortArray.length || index2 < 0 || index2 >= quickSortArray.length) {
            return;
        }
        int temp = quickSortArray[index1];
        quickSortArray[index1] = quickSortArray[index2];
        quickSortArray[index2] = temp;
        rects.get(index1).setHeight(quickSortArray[index1]);
        rects.get(index2).setHeight(quickSortArray[index2]);
        updateLabelPosition(index1);
        updateLabelPosition(index2);
        swaps++;
    }
    
    private void updateLabelPosition(int index) {
        if (index < 0 || index >= rects.size() || index >= labels.size()) {
            return;
        }
        Rectangle rect = rects.get(index);
        Text label = labels.get(index);
        label.setText(String.valueOf((int)rect.getHeight()));
        label.setX(rect.getX() + W / 2 - label.getBoundsInLocal().getWidth() / 2);
        label.setY(rect.getY() - 5);
    }
    
    private void resetState() {
        state = 0;
        i = 0;
        j = 0;
        resetPerformanceCounters();
    }
}
