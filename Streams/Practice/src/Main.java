import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    record Person(String name, String city, int age) {}
    record Employee(String name, String email) {}

    /** @Projects:
    Log analyzer
        Read large log files, extract fields, compute counts per IP, top N slowest endpoints, grouped by date.
        Handle streaming I/O, memory constraints, parallelism.
    Product catalog aggregator
        From JSON files (or mocked objects), group products by category, compute price stats, top sellers.
    Mini search engine
        Tokenize documents, build inverted index using streams + collectors, support simple queries.
    API data pipeline
        Fetch paginated API results, flatten, transform, aggregate, and export CSV.
     */
    public static void main(String[] args) {
        ex42();
    }



    // Convert a list of string to uppercase and then concatenate
    private static void ex41() {
        char[] alphabets = {'a', 'b', 'c', 'd'};
        String collect = IntStream.range(0, alphabets.length)
                                  .mapToObj(i -> alphabets[i])
                                  .map(c -> c.toString().toUpperCase())
                                  .collect(Collectors.joining(" "));
        System.out.println(collect); // A B C D
    }

    // Using Supplier to supply streams. Allowing to use streams multiple times through supplying
    private static void ex40() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        Supplier<Stream<String>> supplier = () -> names.stream(); // or names::stream
        // Consumption #1
        supplier.get().forEach(System.out::println);
        // Consumption #2
        supplier.get().map(String::toUpperCase).forEach(System.out::println);
    }

    // Multiply array elements
    private static void ex39() {
        int[] arr = {1, 2, 3, 4, 5};

        Optional<Integer> reduce = Arrays.stream(arr).boxed().reduce((a, b) -> a * b);
        System.out.println(reduce.get()); // 120
    }

    // Convert a list to a map, group by city
    private static void ex38() {
        List<Person> people = Arrays.asList(
                new Person("Peter", "Pune",23),
                new Person("Pamela", "Kolkata",24),
                new Person("David", "Pune",28),
                new Person("Payel", "Bangalore", 20)
                                           );
        Map<String, List<Person>> collect = people.stream().collect(Collectors.groupingBy(Person::city));
        System.out.println(collect);
        // {Kolkata=[Person[name=Pamela, city=Kolkata, age=24]], Pune=[Person[name=Peter, city=Pune, age=23], Person[name=David, city=Pune, age=28]], Bangalore=[Person[name=Payel, city=Bangalore, age=20]]}
    }

    // Group list of strings by their first character and count the number of strings
    private static void ex37() {
        List<String> list = Arrays.asList("apple", "banana", "apricot", "cherry", "blueberry", "avocado");
        Map<Character, Long> collect = list.stream().collect(Collectors.groupingBy(c -> c.charAt(0), Collectors.counting()));
        System.out.println(collect); // {a=3, b=2, c=1}
    }

    // Transform Person object stream into a single string

    private static void ex36() {
        List<Person> people = Arrays.asList(
                new Person("Peter", "Pune",23),
                new Person("Pamela", "Kolkata",24),
                new Person("David", "Pune",28),
                new Person("Payel", "Bangalore", 20)
                                           );
        String collect = people.stream().map(person -> person.name.toUpperCase()).collect(Collectors.joining(" | "));
        System.out.println(collect); // PETER | PAMELA | DAVID | PAYEL
    }

    // Group each names by first name
    private static void ex35() {
        String[] names = {"Aaron Dave", "Aaron bananini", "Kyle wew", "kyle test", "Unique wow"};

        Map<String, List<String>> collect = Arrays.stream(names).collect(Collectors.groupingBy(
                n -> Arrays.stream(n.toLowerCase().split(" ")).findFirst().get()
                , Collectors.toList()
                                                                                                        ));
        System.out.println(collect); // {kyle=[Kyle wew, kyle test], aaron=[Aaron Dave, Aaron bananini], unique=[Unique wow]}
    }

    // Generate the first 10 numbers of the Fibonacci Sequence
    private static void ex34() {
        List<Integer> collect = Stream.iterate(new int[]{0, 1}, f -> new int[]{f[1], f[0] + f[1]})
                                      .limit(10)
                                      .map(arr -> arr[0])
                                      .collect(Collectors.toList());
        System.out.println(collect); // [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]
    }

    // Find the occurrence of domains using Java streams
    private static void ex33() {
        List<Employee> employees = Arrays.asList(
                new Employee("Sam", "sam@gmail.com"),
                new Employee("Adam", "adam@yahoo.com"),
                new Employee("Peter", "peter@gmail.com")
                                           );

        // Group by email domain
        Map<String, List<Employee>> collect = employees.stream()
                                                       .collect(Collectors.groupingBy(e -> e.email.substring(
                                                               e.email.indexOf("@") + 1, e.email.indexOf("."))));
        System.out.println(collect); // gmail=[Employee[name=Sam, email=sam@gmail.com], Employee[name=Peter, email=peter@gmail.com]], yahoo=[Employee[name=Adam, email=adam@yahoo.com]]}

        // Count occurences per domain
        Map<String, Long> collect1 = employees.stream()
                                              .collect(Collectors.groupingBy(e -> e.email.substring(e.email.indexOf("@")),
                                                      Collectors.counting()));
        System.out.println(collect1); // {@gmail.com=2, @yahoo.com=1}

    }

    // Find the intersection of two lists using Java streams
    private static void ex32() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> list1 = Arrays.asList(3, 5, 6, 7);

        List<Integer> collect = list.stream().filter(i -> list1.contains(i)).collect(Collectors.toList());
        System.out.println(collect); // [3, 5]
        List<Integer> collect1 = list.stream().filter(i -> list1.indexOf(i) != -1).collect(Collectors.toList());
        System.out.println(collect1);   // [3, 5]
    }

    // Calculate the average of all the numbers.
    private static void ex31() {
        List<Integer> list = Arrays.asList(1,2,3,4,5);

        double asDouble = list.stream().mapToDouble(i -> i).average().orElse(0);
        System.out.println(asDouble); // 3
    }

    // Convert a list of strings to uppercase.
    private static void ex30() {
        List<String> list = Arrays.asList("breaking bad", "gAmE oF tHrOnEs", "big BAng THEORy");
        List<String> ans = list.stream().map(String::toUpperCase).collect(Collectors.toList());
        System.out.println(ans); // [BREAKING BAD, GAME OF THRONES, BIG BANG THEORY]
    }

    // Find and print strings containing only digits
    private static void ex29() {
        List<String> list = Arrays.asList("123", "abc", "123abc", "45");

        List<String> collect = list.stream().filter(s -> s.matches("\\d+")).collect(Collectors.toList());
        System.out.println(collect); // [123, 45]
    }

    // Remove all non-numeric characters from a list.
    private static void ex28() {
        List<String> list = Arrays.asList("a1b2c3", "1a2b3c", "123abc");

        List<String> ans = list.stream().map(w -> w.chars()
                                                       .mapToObj(c -> (char) c)
                                                       .filter(c -> Character.isDigit(c))
                                                       .map(String::valueOf)
                                                       .collect(Collectors.joining()))
                                   .collect(Collectors.toList());
        System.out.println(ans); // [123, 123, 123]

        // Method 2 \D == [^0-9]
        Pattern pattern = Pattern.compile("[^0-9]");
        List<String> ans2 = list.stream().map(x -> pattern.matcher(x).replaceAll("")).collect(Collectors.toList());
        System.out.println(ans2); // [123, 123, 123]

        // Method 3
        List<String> result = list.stream().map(x -> x.replaceAll("\\D" , "")).toList();
        System.out.println(result);
    }

    // Find the kth smallest element in a list of integers.
    private static void ex27() {
        int k = 3;
        List<Integer> list = Arrays.asList(7,1,6,2,1,3,4,5);

        Integer i = list.stream().sorted().skip(k - 1).findFirst().get();
        System.out.println(i); // 2
    }

    // Find the union of two lists of integers
    private static void ex26() {
        int[] nums1 = {1,2,3,4,5};
        List<Integer> nums2 = Arrays.asList(6,7,8,9,10);
        List<Integer> ans = Stream.concat(Arrays.stream(nums1).boxed(), nums2.stream()).collect(Collectors.toList());
        System.out.println(ans); // [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    }

    // Find and print the distinct odd numbers
    private static void ex25() {
        List<Integer> nums = Arrays.asList(1,2,5,1,2,3,6,3,5,7,1);

        List<Integer> ans = nums.stream().filter(x -> x%2 != 0).distinct().collect(Collectors.toList());
        System.out.println(ans); // [1, 2, 5, 3, 6, 7]
    }

    // Convert a list of integers to a list of their squares
    private static void ex24() {
        List<Integer> nums = Arrays.asList(1,2,3,4,5);

        List<Integer> ans = nums.stream().map(i -> i * i).collect(Collectors.toList());
        System.out.println(ans); // [1, 4, 9, 16, 25]
    }


    // Sort a list of strings in alphabetical order
    private static void ex23() {
        List<String> str = Arrays.asList("Zudio", "Puma", "Addidas", "MAC", "H&M");

        List<String> ans = str.stream().sorted().collect(Collectors.toList());
        System.out.println(ans); // [Addidas, H&M, MAC, Puma, Zudio]
    }

    // Find the sum of all the elements in a list.
    private static void ex22() {
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        int sum = nums.stream().mapToInt(Integer::intValue).sum();
        int sum2 = nums.stream().reduce(Integer::sum).get();
        System.out.println(sum); // 55
    }

    // Given the string[] group the strings based on the middle character
    private static void ex21() {
        String[] str = {"ewe", "jji", "jhj", "kwk", "aha"};
        Map<Character, List<String>> ans = Arrays.stream(str)
                                                 .collect(Collectors.groupingBy((word) -> word.chars().mapToObj(c -> (char) c).skip(1).findFirst().get()));
        Map<Object, List<String>> map = Arrays.asList(str).stream()
                                              .collect(Collectors.groupingBy(x->x.charAt(x.length()/2))); // even substring(1,2) / charAt(index)
        System.out.println(ans);
        // {w=[ewe, kwk], h=[jhj, aha], j=[jji]}
    }


    // In a given array of integers, return true if it contains only distinct values
    private static void ex20() {
        int[] arr = {5,0,1,0,8,0};

        // method 1
        String collect = Arrays.stream(arr).mapToObj(String::valueOf).collect(Collectors.joining());
        Arrays.stream(arr).mapToObj(String::valueOf).map(x -> {
            return collect.indexOf(x) == collect.lastIndexOf(x);
        }).filter(x -> x == false).findFirst().ifPresent(System.out::println);

        // method 2
        boolean ans = Arrays.stream(arr)
                          .boxed()
                          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                          .values()
                          .stream()
                          .noneMatch(count -> count > 1);  //or allMatch(count -> count == 1);
        System.out.println(ans);
    }

    // Write a stream program to move all zero’s to beginning of array
    private static void ex19() {
        int[] arr = {5,0,1,0,8,0};

        List<Integer> collect = Arrays.stream(arr)
                                      .boxed()
                                      .collect(Collectors.partitioningBy(i -> i != 0))
                                      .values()
                                      .stream()
                                      .flatMap(Collection::stream)
                                      .collect(Collectors.toList());

        System.out.println(collect);
        // [0, 0, 0, 5, 1, 8]


    }

    // Write a program to multiply 1st and last element, 2nd and 2nd last element etc
    private static void ex18() {
        int[] arr = {4,5,1,7,2,9};

        // Use Instream.range as an iterator, then change individual item (0,1,2,3...) to the array operations (multiply)
        List<Integer> collect = IntStream.range(0, arr.length / 2)
                                         .map(i -> arr[i] * arr[arr.length - i - 1])
                                         .boxed()
                                         .collect(Collectors.toList());

        System.out.println(collect);
        // [36, 10, 7]
    }

    // Write a stream program to multiply alternative numbers in an array
    private static void ex17() {
        int[] arr = {4,5,1,7,2,9,2};

        Optional<Integer> reduce = Arrays.stream(arr).boxed().filter(e -> e % 2 == 0).reduce((a, b) -> a * b);
        System.out.println(reduce.get());   // 16
    }

    // Group /Pair anagrams from a list of Strings.
    private static void ex16() {
        String[] s = {"pat", "tap", "pan", "nap", "Team", "three", "meat"};

        Collection<List<String>> ans = Arrays.stream(s)
                                                .collect(Collectors.groupingBy(w -> Arrays.stream(w.toLowerCase().split(""))
                                                                                          .collect(Collectors.joining()))
                                                        )
                                                .values();
        System.out.println(ans);
        // [[tap], [pat], [three], [Team], [meat], [nap], [pan]]

    }

    // Find the products of the first two elements in an array.
    private static void ex15() {
        int[] arr = {12,5,6,9,2,4};

        OptionalInt reduce = Arrays.stream(arr).limit(2).reduce((x, y) -> x * y);
        System.out.println(reduce.getAsInt()); // 60

        Optional<Integer> reduce1 = Arrays.stream(arr).limit(2).boxed().reduce((x, y) -> x * y);
        System.out.println(reduce1.get()); // 60

    }

    // Given a list of strings, create a list that contains only integers
    private static void ex14() {
        String[] sArr = {"abc", "123", "456", "xyz"};

        List<Integer> list = Arrays.stream(sArr)
                                  .filter(x->x.matches("\\d+")) // only digits or [0-9]+
                                  .map(Integer::valueOf)
                                  .collect(Collectors.toUnmodifiableList());

        List<String> collect = Arrays.stream(sArr)
                                     .filter(s -> s.chars().mapToObj(c -> (char) c).allMatch(Character::isDigit))
                                     .collect(Collectors.toList());

        System.out.println(list);
        System.out.println(collect);
    }

    // Given an array of integers, group the numbers by the range (Range: by 10)
    private static void ex13() {
        int[] arr = {2, 3, 10, 14, 20, 24, 30, 34, 40, 44, 50, 54};

        System.out.println((41/10)* 10); // gives grouping classifier

        Map<Integer, List<Integer>> collect = Arrays.stream(arr) // become IntStream
                                                    .boxed() // or use mapToObj (to make Stream<Integer>)
                                                    .collect(Collectors.groupingBy(n -> (n / 10) * 10, LinkedHashMap::new, Collectors.toList()));
        System.out.println(collect);
    }

    // Given a string, find the first repeated character
    private static void ex12() {
        String s = "Hello World";

        String ans = Arrays.stream(s.split(""))
                           .filter(c -> s.indexOf(c) != s.lastIndexOf(c))
                           .findFirst()
                           .get();

        // Linked Hash Map, follows insertion order
        LinkedHashMap<Character, Long> collect = s.chars().mapToObj(c -> (char) c)  // Stream<Character>
                                                  .collect(Collectors.groupingBy(
                                                          Function.identity(),
                                                          LinkedHashMap::new,
                                                          Collectors.counting()
                                                                                ));

        LinkedHashMap<String, Long> collect2 = Arrays.stream(s.split(""))           // Stream<String>
                                                  .collect(Collectors.groupingBy(
                                                          Function.identity(),
                                                          LinkedHashMap::new,
                                                          Collectors.counting()
                                                                                ));

        String ans2 = collect2.entrySet().stream()
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
