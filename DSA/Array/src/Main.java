import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        p1();
    }

    // Reverse an Array in groups of given size
    private static void p1() {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8};
        int k = 3;

        int left;
        int right;

        for(int i = 0; i < arr.length; i+= k) {
            left = i;
            right = Math.min(i + k, arr.length - 1); // To prevent index out of bound in last iteration if k is not a multiple of arr.length (e.g k = 3, n = 5 -> 2 excess)

            while (left < right) {
                int temp = arr[left];
                arr[left] = arr[right];
                arr[right] = temp;
                left++;
                right--;
            }
        }
        System.out.println(Arrays.toString(arr));
    }


}
