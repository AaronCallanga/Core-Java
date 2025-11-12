import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        p2();
    }

    // Rotate an Array by d - Counterclockwise or Left
    private static void p2() {
        int arr[] = {1, 2, 3, 4, 5, 6};
        int n = 2;


        for (int i = 0; i < n; i++) {
            // Take note of the first element
            int firstElement = arr[0];
            for (int j = 1; j < arr.length; j++) {
                // Shift the element after the first element
                arr[j - 1] = arr[j];
            }
            // Place it at the very last
            arr[arr.length - 1] = firstElement;
        }
        System.out.println(Arrays.toString(arr)); // [3, 4, 5, 6, 1, 2]
    }

    // Reverse an Array in groups of given size
    private static void p1() {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8};
        int k = 3;


        for(int i = 0; i < arr.length; i += k) {
            int left = i;
            int right = Math.min(i + k - 1, arr.length - 1); // To prevent index out of bound in last iteration if k is not a multiple of arr.length (e.g k = 3, n = 5 -> 2 excess)

            while (left < right) {
                int temp = arr[left];
                arr[left] = arr[right];
                arr[right] = temp;
                left++;
                right--;
            }
        }
        System.out.println(Arrays.toString(arr)); // [3, 2, 1, 6, 5, 4, 8, 7]
    }


}
