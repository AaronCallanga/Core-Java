import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        //bubbleSort();
        insertionSort();
    }



    // Bubble - bubble the highest element
    private static void bubbleSort() {
        int[] array = {1,5,9,2,3,8,7};
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        System.out.println(Arrays.toString(array));
    }
    // Insertion
    private static void insertionSort() {
        int[] array = {1,5,9,2,3,8,7};

        for (int i = 1; i < array.length; i++) {
            int key = array[i];
            int j = i - 1;
            while(j >= 0 && array[j] > key) {
                array[j+1] = array[j];
                j--;
            }
            array[j+1] = key;
        }
        System.out.println(Arrays.toString(array));
    }
    // Selection
    private static void selectionSort() {
    }
    // Merge
    private static void mergeSort() {
    }
    // Quick
    private static void quickSort() {
    }
}
