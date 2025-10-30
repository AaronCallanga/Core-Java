import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        // Stream - think of it a pipelines that your data goes which you can implement aggregate/set of operations

        // Ways to Create Streams

        // Collection to Streams
        List<Integer> numbersCollection = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Stream<Integer> numbersCollectionToStream = numbersCollection.stream(); // return a general stream but you can mapToInt (or other primitive streams, to perform calculations or specialized methods from primitive stream)
        numbersCollectionToStream.mapToInt(Integer::intValue).sum();

        // Array to Streams
        int[] numbersArray = {1,2,3,4,5};
        Stream<Integer> numbersArrayToStream = Arrays.stream(numbersArray) // return a primitive streams
                                                     .boxed(); //you can also do asDoubleStream() (ret DoubleStream) then .boxed() (ret Double);

        // Direct Values
        Stream<Integer> numbersStream = Stream.of(1, 2, 3, 4, 5);

        // Through Files
        //Stream<Integer> fileStreams = Files.lines(Path.of("file.txt")); // File must be existing
        //aa

        // Primitive Streams (can perform specialized methods for numerical operations, sum(), average(), min(), max(), and range().
        // These methods return primitive type (e.g int) or Optional Primitive (e.g OptionalInt) for min(), max(), average()
        IntStream intStream = IntStream.range(0, 10); // IntStream.of(values...);

        DoubleStream doubleStream = DoubleStream.generate(Math::random)
                                                .limit(10); // limit to 10 element only

        LongStream longStream = LongStream.iterate(0, i -> i + 1)
                                          .takeWhile(i -> i < 10); // as long as the condition is true, it will add the element

        // Infinite Streams (generate() or iterate()) and can be handled by
        // intermediate short-circuit operations such as limit(size) and takeWhile(condition/predicate)
        // (e.g, limit, findFirst/findAny (terminal ops), anyMatch/allMatch (terminal ops), takeWhile, dropWhile)
        Stream.generate(() -> 1).limit(5).forEach(System.out::println); // Prints 1, 2, 3, 4, 5
        Stream.iterate(1, n -> n + 1).takeWhile(n -> n <= 5).forEach(System.out::println); // Prints 1, 2, 3, 4, 5
        Stream.iterate(1, n -> n <= 5, n -> n + 1).forEach(System.out::println); // Prints 1, 2, 3, 4, 5

        // Stream intermediate operations are naturally LAZY - it will do nothing until a TERMINATION operation is called
        // Intermediate ops build a pipeline. Execution occurs only when a terminal op runs. Good for efficiency.
        List<String> list = List.of("A", "BB", "CCC");
        Stream<String> stream = list.stream();

        // Filter is not invoked
        stream
            .filter(s -> {
                System.out.println("Filtering: " + s);
                return s.length() > 1;
            }); // Nothing printed yet!

        // Doesn't work, since the STREAM pipeline is consumed already
        // Streams cannot be reused after terminal op; recreate for another pipeline.
        stream
            .filter(s -> {
                System.out.println("Filtering: " + s);
                return s.length() > 1;
            })
            .forEach(System.out::println);

        // Create new Stream object, but intermediate operations are not still getting invoke
        list.stream()
            .filter(s -> {
                System.out.println("Filtering: " + s);
                return s.length() > 1;
            }); // Nothing printed yet!

        // Now TERMINAL operations are added, the whole pipeline workflow will now work.
        // Also, notices that the operations works per individual element and immediately goes to the terminal operations
        // It works like this: Individual Element -> Apply Intermediate Operation/s to that element -> Terminal Operation
        // Not like this: Whole Set of Elements -> Apply Intermediate Operation/s for all element -> Terminal Operation
        list.stream()
            .filter(s -> {
                System.out.println("Filtering: " + s);
                return s.length() > 1;
            })
            .forEach(System.out::println);
    }
}
