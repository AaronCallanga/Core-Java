import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        ex5();
    }

    // Given a sentence, find the occurrence of each word
    private static void ex5() {
        String s = "I am Learning Streams API in Java Java";

        Map<String, Long> occurences = Arrays.stream(s.split(" "))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                                            // you can also write it as  x -> x for Function.identity()

        System.out.println(occurences);
        // {Learning=1, Java=2, in=1, I=1, API=1, am=1, Streams=1}
    }

    // Find the 2nd highest length word in the given sentence
    private static void ex4() {
        String s = "I am Learning Streams API in Java";

        int highestLength = Arrays.stream(s.split(" ")).map(String::length)
                                  .sorted(Comparator.reverseOrder())
                                  .skip(1)
                                  .findFirst()
                                  .get();

        System.out.println(highestLength);
        // 7

    }

    // Find the word that has the second highest length
    private static void ex3() {
        String s = "I am Learning Streams API in Java";

        String ans = Arrays.stream(s.split(" "))
                           .sorted(Comparator.comparing(String::length)     // Sorted in increasing order
                                            .reversed())
                           .skip(1)
                           .findFirst()
                           .get();

        System.out.println(ans);
        // Streams
    }

    // Remove duplicates from the string and return in the same order
    private static void ex2() {
        String s = "dabcadefg";

        String ans = Arrays.stream(s.split(""))
                           .distinct()
                           .collect(Collectors.joining());

        System.out.println(ans);
        // dabcefg
    }

    // Given a sentence, find the word that has the highest length
    private static void ex1() {
        String sentence = "I am Learning Streams API in Java";

        String[] words = sentence.split(" ");

        String longest = Arrays.stream(words)
                               .max(Comparator.comparing(String::length))
                               .get();

        System.out.println(longest); // Learning
    }
}
