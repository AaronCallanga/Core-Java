import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        //bubbleSort();
        //insertionSort();
        //selectionSort();
        //mergeSortInit();
        quickSortInit();
    }



    // Bubble - bubble the highest element
    private static void bubbleSort() {
        int[] array = {1,5,9,2,3,8,7};
        for (int i = 0; i < array.length - 1; i++) {
            // Loop until length - i, since the highest element is already sorted at the top(bubbled up)
            for (int j = 0; j < array.length - i - 1; j++) {
                // Bubble the element at index j
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        System.out.println(Arrays.toString(array));
    }
    // Insertion - insert each element at the right index while shifting other element
    private static void insertionSort() {
        int[] array = {1,5,9,2,3,8,7};

        for (int i = 1; i < array.length; i++) {
            // Get the element to be inserted
            int key = array[i];
            int j = i - 1;

            // Shift other element if it is greater than the key(selected element)
            while(j >= 0 && array[j] > key) {
                array[j+1] = array[j];
                j--;
            }
            // Insert the key to its proper place after shifting other elements
            array[j+1] = key;
        }
        System.out.println(Arrays.toString(array));
    }
    // Selection - select the lowest element and swap with element at index i
    private static void selectionSort() {
        int[] array = {1,5,9,2,3,8,7};
        for (int i = 0; i < array.length - 1; i++) {
            // Initialize i as the minimum index
            int minIndex = i;
            int j = i + 1;

            // Find the lowest element using j pointer
            while (j < array.length) {
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
                j++;
            }

            // Swap the place of the lowest element and index i
            int temp = array[i];
            array[i] = array[minIndex];
            array[minIndex] = temp;
        }
        System.out.println(Arrays.toString(array));
    }

    // Merge - divide into sub-arrays then merge it while sorting
    private static void mergeSortInit() {
        int[] array = {1,5,9,2,3,8,7};
        mergeSort(array, 0, array.length - 1);
        System.out.println(Arrays.toString(array));

        int[] array2 = {1,5,9,2,3,8,7};
        mergeSort2(array2);
        System.out.println(Arrays.toString(array2));
    }
    private static void mergeSort(int[] arr, int left, int right) {
        if (left < right) {
            // Find the mid pointer (divider)
            int mid = (left + right) / 2;

            // Divide each array by half until it become a single element (left points where right is/beyond right is >=)
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);

            // Merge each divided array
            merge(arr, left, mid, right);
        }
    }
    private static void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        // Make a temporary copy of the left and right array
        // First sub-array is array[left..mid]
        // Second sub-array is array[mid+1..right]
        int[] L = new int[n1];
        int[] R = new int[n2];


        // Copy the element to the temporary array (can do Arrays.copyOf())
        for(int i = 0; i < n1; i++) {
            L[i] = arr[left + i];
        }
        for (int j = 0; j < n2; j++) {
            R[j] = arr[mid + 1 + j];
        }

        // Merge both sub-array(left and right) to the original array
        // Pointer: i - left sub-array, j - right sub-array, k - original array
        int i = 0, j = 0, k = left;

        // Choose and insert the lowest element between Left and Right sub-array
        while(i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }

        // Insert the left-over (if there is any)
        while (i < n1) {
            arr[k++] = L[i++];
        }
        while (j < n2) {
            arr[k++] = R[j++];
        }
    }

    private static void mergeSort2(int[] arr) {
        if (arr.length > 1) {
            // Find the mid pointer (divider)
            int mid = arr.length / 2;
            // Divide the array into two halves
            int[] L = Arrays.copyOfRange(arr, 0, mid);
            int[] R = Arrays.copyOfRange(arr, mid, arr.length);

            // Recursively sort the left and right halves
            mergeSort2(L);
            mergeSort2(R);

            // Merge each divided array
            merge2(arr, L, R);
        }
    }

    private static void merge2(int[] arr, int[] L, int[] R) {
        // Merge both sub-array(left and right) to the original array
        // Pointer: i - left sub-array, j - right sub-array, k - original array
        int i = 0, j = 0, k = 0;
        while(i < L.length && j < R.length) {
            if (L[i] <= R[j]) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }

        // Insert the left-over (if there is any)
        while (i < L.length) {
            arr[k++] = L[i++];
        }
        while (j < R.length) {
            arr[k++] = R[j++];
        }
    }

    // Quick - get a partition then put on the left elements that are <= than the pivot, right greater than the pivot then do it recursively at the left and right part
    private static void quickSortInit() {
        int[] array = {1,5,9,2,3,8,7};
        quickSort(array, 0, array.length - 1);
        System.out.println(Arrays.toString(array));
    }

    private static void quickSort(int[] arr, int left, int right) {
        // Choose a pivot (anywhere) - a good pivot reduces recursion depth making sorting faster
        // Partition(divide) the array into two parts
        // Left part contains smaller than the pivot, right part contains greater than the pivot
        // Then put the pivot between them
        // Recursively sort the sub-arrays (each array getting new pivot)
        if (left < right) {
            int pivot = partition(arr, left, right);

            // Recursively sort on the left side of the pivot
            quickSort(arr, left, pivot - 1);
            // Recursively sort on the right side of the pivot
            quickSort(arr, pivot + 1, right);
        }
    }

    private static int partition(int[] arr, int left, int right) {
        // Choose the rightmost element as pivot
        int pivot = arr[right];

        // Pointer for placing the element lower than the pivot
        int i = left - 1;

        // Compare each element with pivot
        for (int j = left; j < right; j++) {
            // If element smaller than pivot is found
            // Swap it with the greater element pointed by i (increment i first since it is initially pointing at left - 1)
            if (arr[j] <= pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // Swap pivot to the correct position (putting it in between so increment i first)
        i++;
        int temp = arr[i];
        arr[i] = arr[right];
        arr[right] = temp;

        // Return the position from where partition is done
        return i;
    }
}
