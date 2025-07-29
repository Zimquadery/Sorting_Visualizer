import java.util.HashMap;
import java.util.Map;

public class AlgorithmDescription {
    public static final Map<String, String> DETAILS = new HashMap<>();
    static {
        DETAILS.put("Bubble Sort", "Bubble Sort is a simple comparison-based sorting algorithm. It repeatedly steps through the list, compares adjacent elements, and swaps them if they are in the wrong order. This process is repeated until the list is sorted.\n\n" +
                "How Bubble Sort Works:\n" +
                "1. Start at the beginning of the array.\n" +
                "2. Compare each pair of adjacent elements.\n" +
                "3. If the elements are in the wrong order, swap them.\n" +
                "4. Continue to the end of the array.\n" +
                "5. After each pass, the largest unsorted element bubbles up to its correct position.\n" +
                "6. Repeat the process for the remaining unsorted part of the array.\n" +
                "7. The algorithm stops when no swaps are needed in a pass, meaning the array is sorted.\n\n" +
                "Pseudocode:\n" +
                "for i = 0 to n-1\n" +
                "    for j = 0 to n-i-2\n" +
                "        if arr[j] > arr[j+1]\n" +
                "            swap arr[j] and arr[j+1]\n" +
                "\n" +
                "Time Complexity: O(n^2) in the worst and average case, O(n) in the best case (when already sorted).\n" +
                "Space Complexity: O(1) (in-place).\n");

        DETAILS.put("Selection Sort", "Selection Sort is a simple comparison-based sorting algorithm. It divides the array into a sorted and an unsorted part and repeatedly selects the minimum element from the unsorted part and moves it to the end of the sorted part.\n\n" +
                "How Selection Sort Works:\n" +
                "1. Start with the first element as the minimum.\n" +
                "2. Find the minimum element in the unsorted part of the array.\n" +
                "3. Swap it with the first unsorted element.\n" +
                "4. Move the boundary of the sorted part one step forward.\n" +
                "5. Repeat until the entire array is sorted.\n\n" +
                "Pseudocode:\n" +
                "for i = 0 to n-2\n" +
                "    min_idx = i\n" +
                "    for j = i+1 to n-1\n" +
                "        if arr[j] < arr[min_idx]\n" +
                "            min_idx = j\n" +
                "    swap arr[i] and arr[min_idx]\n" +
                "\n" +
                "Time Complexity: O(n^2) for all cases.\n" +
                "Space Complexity: O(1) (in-place).\n");

        DETAILS.put("Insertion Sort", "Insertion Sort builds the sorted array one item at a time by repeatedly picking the next element and inserting it into its correct position among the already sorted elements.\n\n" +
                "How Insertion Sort Works:\n" +
                "1. Start from the second element (the first element is considered sorted).\n" +
                "2. Compare the current element with the elements in the sorted part.\n" +
                "3. Shift all larger sorted elements one position to the right.\n" +
                "4. Insert the current element into its correct position.\n" +
                "5. Repeat for all elements until the array is sorted.\n\n" +
                "Pseudocode:\n" +
                "for i = 1 to n-1\n" +
                "    key = arr[i]\n" +
                "    j = i - 1\n" +
                "    while j >= 0 and arr[j] > key\n" +
                "        arr[j+1] = arr[j]\n" +
                "        j = j - 1\n" +
                "    arr[j+1] = key\n" +
                "\n" +
                "Time Complexity: O(n^2) in the worst and average case, O(n) in the best case (when already sorted).\n" +
                "Space Complexity: O(1) (in-place).\n");

        DETAILS.put("Merge Sort", "Merge Sort is a divide and conquer algorithm that divides the array into halves, sorts each half, and then merges the sorted halves to produce the sorted array.\n\n" +
                "How Merge Sort Works:\n" +
                "1. Divide the array into two halves.\n" +
                "2. Recursively sort each half.\n" +
                "3. Merge the two sorted halves into a single sorted array.\n" +
                "4. The merge operation compares elements from both halves and builds a new sorted array.\n" +
                "5. The recursion stops when the sub-array has only one element.\n\n" +
                "Pseudocode:\n" +
                "mergeSort(arr):\n" +
                "    if length(arr) > 1:\n" +
                "        mid = length(arr) // 2\n" +
                "        left = arr[:mid]\n" +
                "        right = arr[mid:]\n" +
                "        mergeSort(left)\n" +
                "        mergeSort(right)\n" +
                "        merge left and right into arr\n" +
                "\n" +
                "Time Complexity: O(n log n) for all cases.\n" +
                "Space Complexity: O(n) (requires additional space for merging).\n");
        DETAILS.put("Quick Sort", "QuickSort is a sorting algorithm based on the Divide and Conquer principle. It picks an element as a pivot and partitions the given array around the picked pivot by placing the pivot in its correct position in the sorted array.\n\n" +
                "It works by breaking down the problem into smaller sub-problems.\n\n" +
                "There are mainly three steps in the algorithm:\n" +
                "1. Choose a Pivot: Select an element from the array as the pivot. The choice of pivot can vary (e.g., first element, last element, random element, or median).\n" +
                "2. Partition the Array: Rearrange the array around the pivot. After partitioning, all elements smaller than the pivot will be on its left, and all elements greater than the pivot will be on its right. The pivot is then in its correct position, and we obtain the index of the pivot.\n" +
                "3. Recursively Call: Recursively apply the same process to the two partitioned sub-arrays (left and right of the pivot).\n" +
                "Base Case: The recursion stops when there is only one element left in the sub-array, as a single element is already sorted.\n\n" +
                "Pseudocode:\n" +
                "quickSort(arr, low, high):\n" +
                "    if low < high:\n" +
                "        pi = partition(arr, low, high)\n" +
                "        quickSort(arr, low, pi-1)\n" +
                "        quickSort(arr, pi+1, high)\n" +
                "\n" +
                "partition(arr, low, high):\n" +
                "    pivot = arr[high]\n" +
                "    i = low - 1\n" +
                "    for j = low to high-1:\n" +
                "        if arr[j] < pivot:\n" +
                "            i = i + 1\n" +
                "            swap arr[i] and arr[j]\n" +
                "    swap arr[i+1] and arr[high]\n" +
                "    return i+1\n" +
                "\n" +
                "Time Complexity: O(n log n) on average, O(n^2) worst case.\n" +
                "Space Complexity: O(log n) auxiliary (due to recursion).\n");
    }
}
