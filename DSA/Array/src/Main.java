import java.util.Arrays;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {
        p3();
    }

    // Maximum product of a triplet (subsequence of size 3) in array (3 greatest number including negative numbers, -2 * -2 = 4)
    private static void p3() {
        int[] arr = {1, -4, 3, -6, 7, 0};
        int n = arr.length;
        int d = 3;
        Arrays.sort(arr);
        int maxProduct = Math.max(arr[0] * arr[1] * arr[n - 1], arr[n - 1] * arr[n - 2] * arr[n - 3]);
        System.out.println(maxProduct); // 168
    }

    // Rotate an Array by d - Counterclockwise or Left
    private static void p2() {
        int arr[] = {1, 2, 3, 4, 5, 6};
        int n = 2;
        // Method 1 - Shift elements O(n * d) Time and O(1) Space
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

        // Method 2 - Copying the array O(n) Time and O(n) Space
        int arr2[] = {1, 2, 3, 4, 5, 6};
        int n2 = 2;
        int cpy[] = new int[arr2.length];
        // Copy the array starting from arr[n2]
        for (int i = 0; i < arr2.length - n2; i++) {
            cpy[i] = arr2[i + n2];
        }
        // Copy the n first element
        for (int i = 0; i < n2; i++) {
            cpy[i + arr.length - n2] = arr2[i];
        }
        System.out.println(Arrays.toString(cpy)); // or recopy cpy to arr
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
