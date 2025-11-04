import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {

        ex12();
    }

    // Given a string, find the first repeated character
    private static void ex12() {
        String s = "Hello World";

        String ans = Arrays.stream(s.split(""))
                           .filter(c -> s.indexOf(c) != s.lastIndexOf(c))
                           .findFirst()
                           .get();

        LinkedHashMap<Character, Long> collect = s.chars().mapToObj(c -> (char) c)
                                                  .collect(Collectors.groupingBy(
                                                          Function.identity(),
                                                          LinkedHashMap::new,
                                                          Collectors.counting()
                                                                                ));
        char ans2 = collect.entrySet().stream()
                           .filter(entry -> entry.getValue() != 1)
                           .findFirst().get().getKey();

        System.out.println(ans); // l
        System.out.println(ans2); // l
    }

    // Given a string, find the first non-repeated character
    private static void ex11() {
        String s = "Hello World";

        String ans = Arrays.stream(s.split(""))
                         .filter(c -> s.indexOf(c) == s.lastIndexOf(c))
                         .findFirst()
                         .get();

        LinkedHashMap<Character, Long> collect = s.chars()
                                                  .mapToObj(c -> (char) c)
                                                  .collect(Collectors.groupingBy(
                                                          Function.identity(),
                                                          LinkedHashMap::new,
                                                          Collectors.counting()
                                                                                ));
        char ans2 = collect.entrySet().stream()
                           .filter(entry -> entry.getValue() == 1)
                           .findFirst().get().getKey();

        System.out.println(ans); // H
        System.out.println(ans2); // H
    }

    // Given an array, find the sum of unique elements
    private static void ex10() {
        int[] arr = {1, 6, 7, 8, 1, 1, 8, 8, 7}; // should be 1 + 6 + 7 + 8 = 22

        int sumOfUniqueElements = Arrays.stream(arr).distinct().reduce(0, (x, y) -> x + y); // Integer::sum
        int sumOfUniqueElements2 = Arrays.stream(arr).distinct().sum();
        System.out.println(sumOfUniqueElements); // 22
    }

    // Arrange the numbers in Descending/Ascending Order
    private static void ex9() {
        int[] arr = {5,2,3,1,4,6,7,9,8};
        String lowestToHighest = Arrays.stream(arr)
                                       .sorted()
                                       .mapToObj(String::valueOf)  // cant use String::valueof in map if it is not Stream<Integer>
                                       .collect(Collectors.joining(""));  // optional delimiter
        String lowestToHighest2 = Arrays.stream(arr)
                                        .boxed()
                                        .sorted(Comparator.naturalOrder()) // or just sorted(), default
                                        .map(String::valueOf)
                                        .collect(Collectors.joining());

        System.out.println(lowestToHighest2);
        // 123456789

        String highestToLowest = Arrays.stream(arr)
                                       .boxed()
                                       .sorted(Comparator.reverseOrder())
                                       .map(String::valueOf)
                                       .collect(Collectors.joining(""));
        System.out.println(highestToLowest);
        // 987654321
    }

    // Given a word, find the occurrence of each character
    private static void ex8() {
        String word = "Mississippi";

        Map<String, Long> countPerCharacter = Arrays.stream(word.split(""))
                                          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.println(countPerCharacter);
        // {p=2, s=4, i=4, M=1}
    }

    // Divide given integer array into lists of even and odd numbers
    private static void ex7() {
        int[] arr = {1,2,3,4,5,6,7,8};

        // You have to use .boxed() method to be able to make numerical operations such as % (modulo)
        // Both Instream.of and Arrays.stream returns Instream, then used boxed to make it Stream<Integer>

        Map<Boolean, List<Integer>> method1 = IntStream.of(arr)
                                                       .boxed()
                                                       .collect(Collectors.partitioningBy(n -> n % 2 == 0));
        Map<Boolean, List<Integer>> method2 = Arrays.stream(arr)
                                                    .boxed()
                                                    .collect(Collectors.groupingBy(n -> n % 2 == 0));
        System.out.println(method1.get(true));
        System.out.println(method2.get(false));

        // You can also do convert the arr to List<Integer> then do normal stream partitioningBy.
        List<Integer> list = Arrays.stream(arr).boxed().collect(Collectors.toList());
        Map<Boolean, List<Integer>> method3 = list.stream()
                                                  .collect(Collectors.partitioningBy(e -> e % 2 == 0));
    }

    // Given a sentence, find the words with a specified number of vowels
    private static void ex6() {
        String s = "I am Learning Streams API in Java";
        int numOfVowels = 2; // a, e, i, o, u - Return list of words with 2 vowels in it

        String[] words = s.split(" ");
        
        List<String> wordsWith2Vowels = Arrays.stream(words).filter(w -> {
            long countVowels = w.toLowerCase().chars()
                                .filter(c -> "aeiou".indexOf(c) != -1)  // -1 if it doesn't find. If it finds, count it
                                .count();
            return countVowels == numOfVowels;
        }).collect(Collectors.toList());

        System.out.println(wordsWith2Vowels);
        // [Streams, API, Java]


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
