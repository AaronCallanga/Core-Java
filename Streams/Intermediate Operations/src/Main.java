import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        // Operations are categorized by:
        // Stateless - process element independently regardless of the other element in the streams - filter(), map(), flatMap(), peek()
        // Stateful - track and are dependent to other elements - distinct(), sorted(), limit(), skip()

        List<String> names = List.of("Anna","Bob","Charlie","David");

        // Key Intermediate Operations:
        // 1. filter(Predicate<? super T> predicate) - keeps only elements matching predicate.
        names.stream()
             .filter(s -> s.length() >= 4)
             .forEach(System.out::println);
        // Output: Anna, Charlie, David

        // 2. map(Function<? super T,? extends R> mapper) - transform each element to another object.
        List<Integer> lengths = names.stream()
                                     .map(String::length)
                                     .collect(Collectors.toList());
        // lengths = [4, 3, 7]

        names.stream()
             .map(String::toUpperCase)
             .forEach(System.out::println);
        // ANNA, BOB, CHARLIE, DAVID

        // 3. flatMap(Function<? super T,? extends Stream<? extends R>> mapper) - map each element to a Stream, then flatten into one stream.
        // Stream the Nested Data -> Make a stream for each nested data using flatMap(expects Stream obj) -> Become one Stream
        List<List<Integer>> nested = List.of(List.of(1,2), List.of(3,4));
        List<Integer> flattened = nested.stream()
                                        .flatMap(Collection::stream)  // s -> s.stream()
                                        .collect(Collectors.toList());
        // flattened = [1,2,3,4]

        List<String> lines = List.of("apple,banana", "cat");
        List<String> tokens = lines.stream()
                .flatMap(s -> Arrays.stream(s.split(",")))
                .collect(Collectors.toList());
        // tokens = ["apple","banana","cat"]

        // 4. distinct() - removes duplicates based on equals() + hashCode(). - Stateful: needs storage of seen elements.
        Stream.of(1,2,2,3,1)
              .distinct()
              .forEach(System.out::println);
        // 1,2,3

        // 5. sorted() / sorted(Comparator<? super T>) - sorts elements. Requires comparing elements; stable with List sources - Stateful: needs to buffer all elements.
        List<String> names2 = List.of("bob","alice","claire");
        names2.stream()
              .sorted()
              .forEach(System.out::println);
        // alice, bob, claire

        names2.stream()
              .sorted(Comparator.comparing(String::length))
              .forEach(System.out::println);
        // bob, alice, claire

        // 6. peek(Consumer<? super T> action) - intersperse an action (for debugging) without modifying elements.
        Stream.of("a","bb","ccc")
              .peek(s -> System.out.println("Peek: " + s))
              .map(String::length)
              .peek(s -> System.out.println("After map: " + s))
              .forEach(System.out::println); // or collect

        // 7. limit(long maxSize) and skip(long n)
        // limit short-circuits: stops after maxSize elements.
        // skip: ignores first n elements (stateful).
        Stream.iterate(0, n -> n + 1)
              .limit(5)
              .forEach(System.out::println); // 0, 1, 2, 3, 4
        Stream.iterate(0, n -> n < 10,n -> n + 1)
              .skip(5)
              .forEach(System.out::println); // 5, 6, 7, 8, 9

        // 8. takeWhile(Predicate) and dropWhile(Predicate)
        // takeWhile: keeps prefix where predicate true, stops on first false — excellent for sorted or ordered streams.
        // dropWhile: drops prefix while predicate true, then keeps rest. If it became false, it will keep the rest even the predicate for specific element became true
        List<Integer> list = List.of(2,4,6,7,8);
        list.stream()
            .takeWhile(n -> n % 2 == 0)
            .forEach(System.out::println); // 2,4,6
        list.stream()
                .dropWhile(n -> n % 2 == 0)
                .forEach(i -> System.out.println("Drop while: " + i)); // 7, 8 (keeps 8 even predicate is true)

        // 9. mapToInt / mapToLong / mapToDouble - Convert object streams to primitive streams (IntStream/LongStream/DoubleStream) for efficient numeric ops.
        // boxed() - Converts primitive stream back to Stream<T>
        List<String> names3 = List.of("Anna", "Bob");
        int sumLen = names3.stream()
                           .mapToInt(String::length)
                           .sum();

        List<Integer> numbers = IntStream.range(0, 5)
                                         .boxed()
                                         .collect(Collectors.toList()); // List<Integer>

        names3.stream().flatMapToInt(String::chars).forEach(System.out::println);

        // TEST
    }
}
