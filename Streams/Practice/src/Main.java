import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        ex2();

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
