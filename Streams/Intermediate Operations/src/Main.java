import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        // Operations are categorized by:
        // Stateless - process element independently regardless of the other element in the streams - filter(), map(), flatMap(), peek()
        // Stateful - track and are dependent to other elements - distinct(), sorted(), limit(), skip()

        // Key Intermediate Operations:
        filter();
        map();
        flatMap();
        distinct();
        sorted();
        peek();
        limit();
        skip();
        takeWhile();
        dropWhile();
        mapToInt();
        mapToDouble();
        mapToLong();
        boxed();
        flatMapToInt();
        flatMapToLong();
        flatMapToDouble();
        unordered();
        asDoubleStream();
        asLongStream();
        summaryStatistics();
    }

    // 1. filter(Predicate<? super T> predicate) - keeps only elements matching predicate.
    private static void filter() {
        List<String> names = List.of("Anna","Bob","Charlie","David");
        names.stream()
             .filter(s -> s.length() >= 4)
             .forEach(System.out::println);
        // Output: Anna, Charlie, David
    }

    // 2. map(Function<? super T,? extends R> mapper) - transform each element to another object.
    private static void map() {
        List<String> names = List.of("Anna","Bob","Charlie","David");
        List<Integer> lengths = names.stream()
                                     .map(String::length)
                                     .collect(Collectors.toList());
        // lengths = [4, 3, 7]

        names.stream()
             .map(String::toUpperCase)
             .forEach(System.out::println);
        // ANNA, BOB, CHARLIE, DAVID
    }

    // 3. flatMap(Function<? super T,? extends Stream<? extends R>> mapper) - map each element to a Stream, then flatten into one stream.
    // Stream the Nested Data -> Make a stream for each nested data using flatMap(expects Stream obj) -> Become one Stream
    private static void flatMap() {
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
    }

    // 4. distinct() - removes duplicates based on equals() + hashCode(). - Stateful: needs storage of seen elements.
    private static void distinct() {
        Stream.of(1,2,2,3,1)
              .distinct()
              .forEach(System.out::println);
        // 1,2,3
    }

    // 5. sorted() / sorted(Comparator<? super T>) - sorts elements. Requires comparing elements; stable with List sources - Stateful: needs to buffer all elements.
    private static void sorted() {
        List<String> names2 = List.of("bob","alice","claire");
        names2.stream()
              .sorted()
              .forEach(System.out::println);
        // alice, bob, claire

        names2.stream()
              .sorted(Comparator.comparing(String::length))
              .forEach(System.out::println);
        // bob, alice, claire
    }

    // 6. peek(Consumer<? super T> action) - intersperse an action (for debugging) without modifying elements.
    private static void peek() {
        Stream.of("a","bb","ccc")
              .peek(s -> System.out.println("Peek: " + s))
              .map(String::length)
              .peek(s -> System.out.println("After map: " + s))
              .forEach(System.out::println); // or collect
    }

    // 7. limit(long maxSize) and skip(long n)
    // limit short-circuits: stops after maxSize elements.
    private static void limit() {
        Stream.iterate(0, n -> n + 1)
              .limit(5)
              .forEach(System.out::println); // 0, 1, 2, 3, 4
    }

    // skip: ignores first n elements (stateful).
    private static void skip() {
        Stream.iterate(0, n -> n < 10,n -> n + 1)
              .skip(5)
              .forEach(System.out::println); // 5, 6, 7, 8, 9
    }

    // 8. takeWhile(Predicate) and dropWhile(Predicate)
    // takeWhile: keeps prefix where predicate true, stops on first false — excellent for sorted or ordered streams.
    private static void takeWhile() {
        List<Integer> list = List.of(2,4,6,7,8);
        list.stream()
            .takeWhile(n -> n % 2 == 0)
            .forEach(System.out::println); // 2,4,6
    }

    // dropWhile: drops prefix while predicate true, then keeps rest.
    // If it became false, it will keep the rest even the predicate for specific element became true
    private static void dropWhile() {
        List<Integer> list = List.of(2,4,6,7,8);
        list.stream()
            .dropWhile(n -> n % 2 == 0)
            .forEach(i -> System.out.println("Drop while: " + i)); // 7, 8 (keeps 8 even predicate is true)
    }

    // 9. Convert object streams to primitive streams (IntStream/LongStream/DoubleStream) for efficient numeric ops.
    // mapToInt(ToIntFunction) transforms a Stream<T> into an IntStream.
    private static void mapToInt() {
        List<String> names3 = List.of("Anna", "Bob");
        int sumLen = names3.stream()
                           .mapToInt(String::length)
                           .sum();
        System.out.println("Sum of Character Length: " + sumLen);
    }

    // mapToLong(ToLongFunction) transforms a Stream<T> into a LongStream.
    private static void mapToLong() {
        List<String> populations = Arrays.asList(
                "8100000000", // World population estimate
                "1400000000", // Example country A
                "330000000"   // Example country B
                                                );

        LongStream popStream = populations.stream()
                                          .mapToLong(Long::parseLong); // Use Long.parseLong to get the long value

        System.out.println("Total population sum: " + popStream.sum());
        // Output: Total population sum: 9830000000
    }

    // mapToDouble(ToDoubleFunction) transforms a Stream<T> into a DoubleStream.
    private static void mapToDouble() {
        List<Integer> scores = Arrays.asList(92, 85, 78, 95);

        DoubleStream gpaStream = scores.stream()
                                       .mapToDouble(score -> score / 100.0); // Map integer score to a double GPA

        System.out.print("GPA values: ");
        gpaStream.forEach(gpa -> System.out.print(gpa + " "));
        // Output: GPA values: 0.92 0.85 0.78 0.95
    }

    // boxed() is used to convert a primitive stream (IntStream, LongStream, or DoubleStream)
    // back into an object stream (Stream<Integer>, Stream<Long>, or Stream<Double>).
    private static void boxed() {
        IntStream intStream = IntStream.rangeClosed(1, 5); // Produces 1, 2, 3, 4, 5 as primitives

        // We want a List<Integer>, not just an int[] or a printout.
        // We must use .boxed() to convert the 'int' primitives to 'Integer' objects.
        List<Integer> integerList = intStream
                .boxed()
                .collect(Collectors.toList());

        System.out.println("Boxed list: " + integerList);
        // Output: Boxed list: [1, 2, 3, 4, 5]
    }

    // 10. For flattening to primitive streams: e.g., splitting strings into char codes.
    // flatMapToInt(IntStream val) expects an IntStream of the nested data
    private static void flatMapToInt() {
        List<String> data = Arrays.asList("Items123", "Data45", "Codes678");

        IntStream digitStream = data.stream().flatMapToInt(s -> {
            // For each string, create an IntStream of its integer digits
            return s.chars()
                    .filter(Character::isDigit)
                    .map(Character::getNumericValue);
        });

        System.out.print("flatMapToInt results: ");
        digitStream.forEach(i -> System.out.print(i + " "));
        // Output: flatMapToInt results: 1 2 3 4 5 6 7 8
    }

    // flatMapToLong(LongStream val) expects an LongStream of the nested data
    private static void flatMapToLong() {
        // Format: "ID,Salary"
        List<String> employees = Arrays.asList("101,65000", "102,72000", "103,58000");

        LongStream salaryStream = employees.stream().flatMapToLong(employee -> {
            String[] parts = employee.split(",");
            long salary = Long.parseLong(parts[1]);

            // Returns a single-element LongStream for each salary
            return LongStream.of(salary);
        });

        long totalSalaries = salaryStream.sum();
        System.out.println("flatMapToLong total salaries: " + totalSalaries);
        // Output: flatMapToLong total salaries: 190000
    }

    // flatMapToDouble(DoubleStream val) expects an DoubleStream of the nested data
    private static void flatMapToDouble() {
        List<String> salesReports = Arrays.asList("150.25,200.50", "120.00,300.75,180.10", "50.00");

        DoubleStream salesStream = salesReports.stream().flatMapToDouble(report -> {
            // Split the string by commas
            String[] salesStrings = report.split(",");

            // Map each string array element to a double value and put it in a DoubleStream
            return Arrays.stream(salesStrings)
                         .mapToDouble(Double::parseDouble);
        });

        double averageSale = salesStream.average().orElse(0.0);
        System.out.printf("flatMapToDouble average sale: %.2f%n", averageSale);
        // Output: flatMapToDouble average sale: 168.60
    }

    // 11. Hint that ordering is not required -> parallel operations may perform better.
    private static void unordered() {
        List<Integer> numbers = Arrays.asList(5, 2, 8, 2, 9, 5, 8, 1);

        // Standard ordered distinct operation (guaranteed order of insertion appearance: 5, 2, 8, 9, 1)
        List<Integer> orderedDistinct = numbers.stream()
                                               .distinct()
                                               .collect(Collectors.toList());
        System.out.println("Ordered distinct: " + orderedDistinct);

        // Unordered distinct operation (order might vary if run in parallel)
        List<Integer> unorderedDistinct = numbers.stream()
                                                 .unordered() // Optimization hint
                                                 .distinct()
                                                 .collect(Collectors.toList());
        System.out.println("Unordered distinct (sequential run): " + unorderedDistinct);

        // Create a large range of numbers
        List<Integer> firstFiveOrdered = IntStream.range(0, 10000)
                                                  .parallel() // Use parallel processing
                                                  .filter(n -> n % 2 == 0)
                                                  .limit(5) // limit() is an order-sensitive short-circuiting operation
                                                  .boxed()
                                                  .collect(Collectors.toList());

        System.out.println("Parallel (Ordered) first 5 even numbers: " + firstFiveOrdered);
        // Output is consistently: [0, 2, 4, 6, 8]

        List<Integer> firstFiveUnordered = IntStream.range(0, 10000)
                                                    .parallel()
                                                    .unordered() // We don't care which 5 even numbers we get first
                                                    .filter(n -> n % 2 == 0)
                                                    .limit(5) // This limit can now be implemented more aggressively in parallel
                                                    .boxed()
                                                    .collect(Collectors.toList());

        System.out.println("Parallel (Unordered) first 5 even numbers: " + firstFiveUnordered);
        // Output might be: [248, 250, 4, 6, 8] (The actual numbers you get might change with each run)

        /*
        By adding unordered(), the parallel stream stops wasting effort ensuring that the very first elements (0, 2, 4, etc.)
        found across all threads are collected first.Instead, it takes the first five elements found by any available
        thread, which can be faster.
         */
    }

    // Converts Primitive Stream to Another Primitive Stream
    private static void asLongStream() {
        IntStream intStream = IntStream.of(1, 2, 3, 4, 5);

        // Convert the IntStream to a LongStream
        LongStream longStream = intStream.asLongStream();

        System.out.print("IntStream elements: ");
        longStream.forEach(d -> System.out.print(d + " "));
    }

    private static void asDoubleStream() {
        LongStream longStream = LongStream.of(123456789012345L, 987654321098765L);

        // Convert the LongStream to a DoubleStream
        DoubleStream doubleStream = longStream.asDoubleStream();

        System.out.print("DoubleStream elements: ");
        doubleStream.forEach(d -> System.out.print(d + " "));
        // Output: DoubleStream elements: 1.23456789012345E14 9.87654321098765E14
    }

    // Primitive stream special ops & ranges - sum(), average(), min(), max(), summaryStatistics().
    // range(start, end) (end exclusive) and rangeClosed (inclusive).
    private static void summaryStatistics() {
        int sum = IntStream.rangeClosed(1, 10).sum(); // 55 (1+2+3+4+5+6+7+8+9+10)
        int sum2 = IntStream.range(1, 10).sum(); // 45 (1+2+3+4+5+6+7+8+9) (end exclusive)
        IntSummaryStatistics stats = IntStream.of(1,2,3,4).summaryStatistics();
        System.out.println("\nsum: " + sum);
        System.out.println("sum2: " + sum2);
        System.out.println("average : " + stats.getAverage());
        System.out.println("min: " + stats.getMin());
        System.out.println("max: " + stats.getMax());
        System.out.println("count: " + stats.getCount());
    }

    /*
    The parallel() operation tells the JVM to attempt to execute the stream pipeline using
    multiple threads from the common ForkJoinPool. This can significantly speed up operations on
    large datasets, but the results are not guaranteed to be ordered unless a terminal operation
    like forEachOrdered() or a collecting operation that enforces order is used
     */
    private static void parallel() {
        long start = System.currentTimeMillis();

        long sum = LongStream.range(0, 100_000_000)
                             .parallel() // Executes the subsequent operations in parallel threads
                             .map(n -> n * 2) // Operation run in parallel
                             .sum();         // Terminal operation

        long end = System.currentTimeMillis();
        System.out.println("Parallel Stream Sum: " + sum);
        System.out.println("Time taken (ms): " + (end - start));
    }

    /*
     The sequential() operation ensures that the stream pipeline runs on a single thread.
     This is useful for debugging, ensuring predictable output order, or when parallel execution
     would actually be slower due to overhead (e.g., on small datasets or operations that
     require constant synchronization).
     */
    private static void sequential() {
        List<String> fruits = Arrays.asList("Banana", "Apple", "Cherry", "Date");

        fruits.stream()
              .parallel() // If we started with parallel() (e.g. if 'fruits' was a ConcurrentLinkedQueue)
              .sequential() // Force subsequent operations back to a single thread
              .map(String::toUpperCase)
              .forEach(System.out::println); // forEach does not guarantee order in parallel streams

        // Output will reliably be:
        // BANANA
        // APPLE
        // CHERRY
        // DATE
    }

    private static void mixParallelSequential() {
        List<String> items = Arrays.asList("One", "Two", "Three", "Four");

        items.stream()
             .parallel()    // Step 1: Execute these operations in parallel
             .map(String::length)
             .sequential()  // Step 2: Switch back to a single thread for the final output step
             .forEachOrdered(System.out::println); // forEachOrdered guarantees output order

        // Output always prints lengths in the original list order:
        // 3
        // 3
        // 5
        // 4
    }

}
