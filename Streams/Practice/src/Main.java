import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    record Person(String name, String city, int age, String gender) {}
    record Person2(String firstName, String lastName, int age) {}
    record Employee(String name, String email, Double salary) {}
    record Employee2(String name, String department, Double salary) {}
    record EmployeeDTO(UUID id, String name, String department) {}
    record Product(int id, String name, int price, String category) {}
    record Transaction(String date, int amount) {}

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
        //ex84();
        
        ex5_hard();
    }

    // Categorize each employee based on their salary
    private static void ex5_hard() {
        List<Employee2> employees = Arrays.asList(
                new Employee2("Alice", "IT", 45000.0),
                new Employee2("Bob", "IT", 78000.0),
                new Employee2("Charlie", "IT", 120000.0),
                new Employee2("David", "IT", 99000.0),
                new Employee2("Frank", "IT", 30000.0)
                                                 );

        Map<String, List<Employee2>> collect = employees.stream().collect(Collectors.groupingBy(e -> { // create a function classifier
            if (e.salary() < 50000) return "LOW";
            if (e.salary() >= 50000 && e.salary() < 100000) return "MEDIUM";
            else return "HIGH";
        }));
        System.out.println(collect);
    }

    // Find Sliding Window Average
    private static void ex4_hard() {
        List<Integer> numbers = Arrays.asList(4, 8, 15, 16, 23, 42);
        int windowSize = 3;
        List<Double> collect = IntStream.range(0, numbers.size() - (windowSize - 1))
                                        .mapToObj(i -> numbers.subList(i, i + windowSize))// creates sublist from i to i + windowSize (excluding the last element), with + i since we are moving the pointer
                                        .map(window -> window.stream().mapToInt(i -> i).average().orElse(0))
                                        .collect(Collectors.toList());
        System.out.println(collect); // [9.0, 13.0, 18.0, 27.0]
    }

    // Find the frequency of each bigram (pair of words)
    private static void ex3_hard() {
        String sentence = "Java is great and Java is fun. Java is powerful!";
        String[] words = sentence.split(" "); // could also lower it first for normalization

        // Wrong answer - the problem is indexOF returns the very first index that met the condition (equals to w1)
        Map<String, Long> collect = Arrays.stream(words)
                                          .map(w1 -> Arrays.stream(words)
                                                  .skip(Arrays.asList(words).indexOf(w1))
                                                           .limit(2)
                                                           .collect(Collectors.joining(" ")))
                                          .collect(Collectors.groupingBy(
                                                  Function.identity(), LinkedHashMap::new, Collectors.counting()));
        System.out.println(collect); // {Java is=3, is great=3, great and=1, and Java=1, fun. Java=1, powerful!=1}

        // Correct approach
        Map<String, Long> collect1 = IntStream.range(0, words.length - 1)
                                              .mapToObj(i -> String.join(" ", words[i], words[i + 1])) // or just simply words[i] + " " + words[i + 1]
                                              .collect(Collectors.groupingBy(
                                                      Function.identity(),
                                                      LinkedHashMap::new, // just to maitain insertion order
                                                      Collectors.counting()
                                                                            ));
        System.out.println(collect1); // {Java is=3, is great=1, great and=1, and Java=1, is fun.=1, fun. Java=1, is powerful!=1}
    }

    // Sum and Sort Transactions by Date
    private static void ex83() {
        List<Transaction> transactions = Arrays.asList(
                new Transaction("2024-12-23", 100),
                new Transaction("2024-12-24", 200),
                new Transaction("2024-12-25", 300),
                new Transaction("2024-12-23", 400),
                new Transaction("2024-12-24", 500));

        // Treemap - auto sort elements
        TreeMap<String, Integer> collect = transactions.stream()
                                                       .collect(Collectors.groupingBy(
                                                               Transaction::date,
                                                               TreeMap::new,        // auto sort by keys (String date)
                                                               Collectors.summingInt(Transaction::amount)
                                                                                     ));
        System.out.println(collect); // {2024-12-23=500, 2024-12-24=700, 2024-12-25=300}

        // LinkedHashMap - maintain insertion order
        LinkedHashMap<String, Integer> collect1 = transactions.stream()
                                                              .sorted(Comparator.comparing(Transaction::date))
                                                              .collect(Collectors.groupingBy(
                                                                      Transaction::date,
                                                                      LinkedHashMap::new,
                                                                      Collectors.summingInt(Transaction::amount)
                                                                                            ));
        System.out.println(collect1); // {2024-12-23=500, 2024-12-24=700, 2024-12-25=300}
    }

    // Count the Number of Digits in a String
    private static void ex82() {
        String s = "Hello123";
        long count = s.chars().mapToObj(c -> (char) c).filter(Character::isDigit).count();
        System.out.println(count); // 3

    }

    // Find the Most Frequent Element in List
    private static void ex81() {
        List<Integer> nums = Arrays.asList(1,2,3,2,1,3);

        Map<Integer, Long> group = nums.stream()
                                         .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Long maxFreq = group.entrySet().stream().max(Map.Entry.comparingByValue()).get().getValue();
        List<Integer> collect = group.entrySet()
                                     .stream()
                                     .filter(e -> e.getValue() == maxFreq)
                                     .map(Map.Entry::getKey)
                                     .collect(Collectors.toList());
        System.out.println(collect);     // [1, 2, 3]

    }

    // Calculate Factorials of a List of Numbers
    private static void ex80() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<Long> collect1 = list.stream()
                                  .map(x -> IntStream.rangeClosed(1, x).mapToLong(i -> i).reduce(1, (a, b) -> a*b))
                                  .collect(Collectors.toList());
        System.out.println(collect1); // [1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800]

        List<Integer> collect = list.stream().map(x -> {
            for (int i = x - 1; i >= 1; i--) {
                x *= i;
            }
            return x;
        }).collect(Collectors.toList());
        System.out.println(collect);  // [1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800]

    }

    // Find Longest Palindrome in a List
    private static void ex79() {
        List<String> list = Arrays.asList("madam", "hello", "civic", "noon", "level", "kayak"); // racecar

        List<String> palindromeList = list.stream()
                                   .filter(s -> new StringBuilder(s).reverse().toString().equals(s))
                                   .collect(Collectors.toList());
        int maxLength = palindromeList.stream().max(Comparator.comparing(String::length)).orElse(null).length();
        List<String> collect = palindromeList.stream()
                                             .filter(p -> p.length() == maxLength)
                                             .collect(Collectors.toList());
        System.out.println(collect); // [racecar] or [madam, civic, level, kayak]

    }

    // Reverse the Characters of Each String in a List
    private static void ex78() {
        List<String> fruits = Arrays.asList("apple", "banana", "orange", "banana");

        // M
        List<String> collect = fruits.stream()
                                     .map(f -> new StringBuilder(f).reverse().toString())
                                     .collect(Collectors.toList());
        System.out.println(collect); // [elppa, ananab, egnaro, ananab]
    }

    // Find Difference Between Two Lists
    private static void ex77() {
        List<Integer> set1 = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> set2 = Arrays.asList(3, 4, 5);

        List<Integer> collect = set1.stream().filter(s1 -> !set2.contains(s1)).collect(Collectors.toList());
        System.out.println(collect);   // [1, 2]
    }

    // Sort Strings by Their Last Character
    private static void ex76() {
        List<String> fruits = Arrays.asList("apple", "banana", "orange", "kiwi", "manggo");
        List<String> collect = fruits.stream()
                                     .sorted(Comparator.comparing(f -> f.charAt(f.length() - 1)))
                                     .collect(Collectors.toList());
        System.out.println(collect);    // [banana, apple, orange, kiwi, manggo]
    }

    // Check If a List Contains a Sub-list
    private static void ex75() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> sub = Arrays.asList(7, 4, 1, 2, 10);

        boolean b = IntStream.rangeClosed(0, numbers.size() - sub.size())
                             .anyMatch(i -> {
                                 List<Integer> window = numbers.subList(i, i + sub.size());
                                 return window.equals(sub);
                             });
        System.out.println(b); // false, sub-list elements should be orderly syncrhonized with the main list

        boolean b1 = sub.stream().allMatch(i -> numbers.contains(i));
        System.out.println(b1); // true

        // List has a method containsAll, instead of using streams
        boolean b2 = numbers.containsAll(sub);
        System.out.println(b2); // true

        // List has a method indexOfSubList which returns starting index of the sub-list or -1 if not found (sub-list element should be ordered based on the main list)
        boolean b3 = Collections.indexOfSubList(numbers, sub) != -1;
        System.out.println(b3); // false, since the next element of 7 in main list is 8 while 4 in the sub-list
    }

    // Find Common Elements Between Two Lists
    private static void ex74() {
        List<String> fruits = Arrays.asList("apple", "banana", "orange", "pear", "grape");
        List<String> fruits2 = Arrays.asList("apple", "banana", "grape");

        List<String> collect = fruits2.stream().filter(fruits2::contains).collect(Collectors.toList());
        System.out.println(collect);
    }

    // Find Maximum Product of Two Integers in List
    private static void ex73() {
        List<Integer> numbers = Arrays.asList(-1, -4, 3, -5);
        Integer ans = numbers.stream()
                            .flatMap(i -> numbers.stream().filter(j -> !i.equals(j)).map(j -> i * j))
                            .max(Integer::compareTo).orElse(null);
        // flatmap - contains Stream<Integer> that is multiplied by each element(i) to another numbers.stream() then perform flattening it
        System.out.println(ans); // 20
    }

    // Count the number of employees per department that has salary greater than 2000
    // Group by department and count the employees having salary of greater than 2000
    private static void ex72() {
        List<Employee2> list = Arrays.asList(
                new Employee2("Alice", "HR", 2000.0),
                new Employee2("Bob", "IT", 3000.0),
                new Employee2("Charlie", "IT", 2000.0),
                new Employee2("David", "Finance", 1500.0),
                new Employee2("Eve", "IT", 4000.0),
                new Employee2("Frank", "Finance", 1800.0),
                new Employee2("Grace", "HR", 2300.0)
                                            );
        Map<String, Long> collect = list.stream()
                                        .collect(Collectors.groupingBy(
                                                Employee2::department,
                                                Collectors.filtering(e -> e.salary() > 2000, Collectors.counting()) // Filter every entry per group by the predicate and count it
                                                                      ));
        System.out.println(collect); // {Finance=0, HR=1, IT=2}

        // Method 2
        Map<String, Long> collect1 = list.stream()
                                         .filter(e -> e.salary() > 2000)
                                         .collect(Collectors.groupingBy(Employee2::department, Collectors.counting()));
        System.out.println(collect1); // {HR=1, IT=2}

    }

    // Find the First Non Repeated Character in String
    private static void ex71() {
        String s = "Ssaurabh";

        Character c1 = s.chars()
                        .mapToObj(c -> (char) Character.toLowerCase(c))
                        .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
                        .entrySet()
                        .stream()
                        .filter(e -> e.getValue() == 1)
                        .findFirst()
                        .map(Map.Entry::getKey)
                        .orElse(null);
        System.out.println(c1); // u
    }

    // Count Number of Characters in a List
    private static void ex70() {
        // Method 1
        List<String> strings = Arrays.asList("aa", "bbb", "cdaw", "d", "eda", "fwda", "gada", "hdde");
        Optional<Long> reduce = strings.stream()
                                       .map(w -> Arrays.stream(w.split("")).count())
                                       .reduce((x, y) -> x + y);
        System.out.println(reduce.get()); // 25

        // Method 2
        int sum = strings.stream().mapToInt(String::length).sum();
        System.out.println(sum); // 25
    }

    // Find the middle elements and its median
    private static void ex69() {
        List<Integer> nums = Arrays.asList(1,2,3,4,5,6);
        List<Integer> middle = (nums.size() % 2 == 0) ?
                nums.stream().skip(nums.size()/2 - 1).limit(2).collect(Collectors.toList()) :
                nums.stream().skip(nums.size() / 2).limit(1).collect(Collectors.toList()); // or just use nums.get(nums.size() / 2)
        System.out.println(middle); // [3, 4]

        double median = (nums.size() % 2 == 0) ?
                nums.stream().sorted().skip(nums.size()/2 - 1).limit(2).mapToDouble(Integer::doubleValue).average().orElse(-1) :
                nums.get(nums.size() / 2); // list must be sorted
        System.out.println(median);
    }

    // Find Palindromic Strings in a List
    private static void ex68() {
        List<String> list = Arrays.asList("madam", "apple", "racecar");

        // Method 1 - Stringbuilder - for performance, Stringbuffer - for thread safety
        List<String> collect1 = list.stream()
                                    .filter(w -> new StringBuffer(w).reverse().toString().equals(w)) // or Stringbuilder
                                    .collect(Collectors.toList());
        System.out.println(collect1); // [madam, racecar]

        // Method 2
        List<String> collect = list.stream().filter(w -> {
            String[] split = w.split("");
            int l = 0, r = w.length() - 1;
            while (l < r) {
                if (!split[l++].equals(split[r--])) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        System.out.println(collect); // [madam, racecar]
    }

    // Sort List of Strings by Length
    private static void ex67() {
        List<String> s = Arrays.asList("a", "bb", "cccc", "ddd", "eeeee");
        // Method 1
        List<String> collect = s.stream().sorted(Comparator.comparing(String::length)).collect(Collectors.toList());
        System.out.println(collect); // [a, bb, ddd, cccc, eeeee]

        // Method 2
        List<String> collect1 = s.stream()
                                 .sorted((s1, s2) -> Integer.compare(s1.length(), s2.length()))
                                 .collect(Collectors.toList());
        System.out.println(collect1); // [a, bb, ddd, cccc, eeeee]
    }

    // Collect Prime Numbers from a List
    private static void ex66() {
        List<Integer> arr = Arrays.asList(1, 2, 3, 4, 5,6,7,8,9,10);
        List<Integer> collect = arr.stream().filter(num -> {
            if (num <= 1) return false;
            for (int i = 2; i <= Math.sqrt(num); i++) {
                if (num % i == 0) return false;
            }
            return true;
        }).collect(Collectors.toList());

        System.out.println(collect); // [2, 3, 5, 7]
    }

    // Check if All Elements in a List are Positive/Negative
    private static void ex65() {
        // All are positive
        List<Integer> s = Arrays.asList(1, 2, 3, -4, 1, -10, 4, 5, -2, 0);
        boolean a = s.stream().allMatch(x -> x >= 0);
        System.out.println(a); // false

        // No negative elements
        boolean b = s.stream().noneMatch(x -> x <0);
        System.out.println(b); // false

        // Has atleast one 0 element
        boolean c = s.stream().anyMatch(x -> x == 0);
        System.out.println(c); // true
    }

    // Flatten the integers and group by odd and even numbers
    private static void ex64() {
        List<List<Integer>> list = Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6), Arrays.asList(7, 8, 9));
        List<Integer> list2= Arrays.asList(10,11,12);
        Map<Boolean, List<Integer>> collect = list.stream().flatMap(Collection::stream).collect(Collectors.partitioningBy(i -> i % 2 == 0));
        System.out.println(collect);

        List<Integer> collect1 = Stream.concat(list2.stream(), list.stream().flatMap(Collection::stream))
                                       .collect(Collectors.toList());
        System.out.println(collect1); // [10, 11, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9]
    }

    // Find Duplicate Values in a List
    private static void ex63() {
        int[] arr = {1,2,3,4,5,6,3,4,7,6,8};
        // Method 1 - if temp.add() returns true, meaning the set still doesn't have that value or no duplicate yet.
        // But if it return false(duplicate), then we negate it, it will be accumulated to our resulting list
        Set<Integer> temp = new HashSet<>();
        List<Integer> collect = Arrays.stream(arr).boxed().filter(x -> !temp.add(x)).collect(Collectors.toList());
        System.out.println(collect); // [3, 4, 6]

        // Method 2 - Group then filter
        List<Integer> collect1 = Arrays.stream(arr)
                                       .boxed()
                                       .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                                       .entrySet()
                                       .stream()
                                       .filter(entry -> entry.getValue() > 1)
                                       .map(Map.Entry::getKey)
                                       .collect(Collectors.toList());
        System.out.println(collect1); // [3, 4, 6]
    }

    // Return a that List Contains Words Starting with 'A' From a List of Strings
    private static void ex62() {
        String[] names = {"Amit", "Suresh", "Animesh", "Vikram", "apple", "banana", "kiwi"};

        List<String> collect = Arrays.stream(names)
                                     .filter(n -> n.startsWith("A") || n.startsWith("a"))
                                     .collect(Collectors.toList());
        System.out.println(collect);

        // Method 2
        List<String> ans = Arrays.stream(names)
                                             .collect(Collectors.partitioningBy(n -> n.toLowerCase().startsWith("a")))
                .get(true);
        System.out.println(ans);
    }

    // Group strings by length
    private static void ex61() {
        List<String> strings = Arrays.asList("One", "Two", "Three");
        Map<Integer, List<String>> collect = strings.stream()
                                                    .collect(Collectors.groupingBy(String::length));
        System.out.println(collect); // {3=[One, Two], 5=[Three]}

    }

    // Reverse a List of Strings
    private static void ex60() {
        List<String> fruits = Arrays.asList("apple", "banana", "orange", "pear", "grape");
        List<String> collect = fruits.stream().collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    Collections.reverse(list);
                    return list; // Finisher function needs to return the transformed list
                }));
        System.out.println(collect); // [grape, pear, orange, banana, apple]
    }

    // Find First Repeated Element in a List
    private static void ex59() {
        int[] arr = {1,2,4,3,4,2,5,3,6};
        Set<Integer> set = new HashSet<>();
        // if elemenet already exist in the hash set, the .add() method will return false
        Integer i = Arrays.stream(arr).boxed().filter(n -> !set.add(n)).findFirst().orElse(-1);

        System.out.println(i); // 4
    }

    // Transform one object into another . Transform Employee to EmployeeDTO
    private static void ex58() {
        List<Employee2> list = Arrays.asList(
                new Employee2("Alice", "HR", 2000.0),
                new Employee2("Bob", "IT", 3000.0),
                new Employee2("Charlie", "IT", 2000.0),
                new Employee2("David", "Finance", 1500.0),
                new Employee2("Eve", "IT", 4000.0),
                new Employee2("Frank", "Finance", 1800.0),
                new Employee2("Grace", "HR", 2300.0)
                                            );
        List<EmployeeDTO> collect = list.stream()
                                        .map(e -> new EmployeeDTO(UUID.randomUUID(), e.name(), e.department()))
                                        .collect(Collectors.toList());
        System.out.println(collect); // [EmployeeDTO[id=51385f34-53fe-4f8d-bfff-debf584e44d6, name=Alice, department=HR], EmployeeDTO[id=4cd94cea-59c2-4136-9eea-90a389a6f5a0, name=Bob, department=IT] ...
    }

    // Convert list of string into map of String and its equivalent length
    private static void ex57() {
        List<String> fruits = Arrays.asList("apple", "banana", "orange", "pear", "grape", "banana");

        Map<String, Integer> collect = fruits.stream()
                                          .collect(Collectors.toMap(Function.identity(), String::length, (existing, duplicate) -> existing));
        System.out.println(collect); // {banana=6, orange=6, apple=5, pear=4, grape=5}
    }

    // Return character with the maximum frequency
    private static void ex56() {
        String s = "javadeveloper";

        // Method 1 - Count then reverse sort by value and get first key
        Character max = s.chars()
                         .mapToObj(c -> (char) c)
                         .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                         .entrySet()
                         .stream()
                         .sorted(Map.Entry.<Character, Long>comparingByValue().reversed())
                         .findFirst()
                         .get()
                         .getKey();
        System.out.println(max); // e
        // Method 2
        Character c1 = s.chars()
                        .mapToObj(c -> (char) c)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                        .entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse(null);
        System.out.println(c1); // e
    }

    // Reorder message from format in chronological order HH:MM:ID:Message
    private static void ex55() {
        List<String> logs = Arrays.asList(
                "14:30:3:Server Started",
                "14:30:1:User Logged In",
                "14:29:2:Database connected",
                "18:32:4:User Logged out"
                                         );
        // BY ID
        List<String> collect = logs.stream()
                                   .map(s -> s.split(":"))
                                   .sorted(Comparator.comparing(x -> Integer.valueOf(x[2])))
                                   .map(x -> x[3])
                                   .collect(Collectors.toList());
        System.out.println(collect); // [User Logged In, Database connected, Server Started, User Logged out]
        // By Time
        List<String> collect2 = logs.stream()
                                   .map(s -> s.split(":"))
                                   .sorted(Comparator.comparing(x -> Integer.valueOf(x[0] + Integer.valueOf(x[1] + Integer.valueOf(x[2])))))
                                   .map(x -> x[3])
                                   .collect(Collectors.toList());
        System.out.println(collect2); // [Database connected, User Logged In, Server Started, User Logged out]
        // By Time 2.0
        List<String> collect1 = logs.stream()
                                    .sorted(Comparator.comparing(s -> Integer.valueOf(s.substring(0, s.lastIndexOf(":"))
                                                                                       .replaceAll(":", ""))))
                                    .map(s -> s.substring(s.lastIndexOf(":") + 1)) // get the event only
                                    .collect(Collectors.toList());
        System.out.println(collect1); // [Database connected, User Logged In, Server Started, User Logged out]
        List<String> collect3 = logs.stream().sorted(Comparator.comparing((String log) -> log.split(":")[0])
                                                               .thenComparing((String log) -> log.split(":")[1])
                                                               .thenComparing((String log) -> log.split(":")[2]))
                                    .map(s -> s.split(":")[3]) // or s.substring(s.lastIndexOf(":") + 1)
                                    .collect(Collectors.toList());
        System.out.println(collect3);
    }

    // Find the average salary from each department
    private static void ex54() {
        List<Employee2> list = Arrays.asList(
                new Employee2("Alice", "HR", 2000.0),
                new Employee2("Bob", "IT", 3000.0),
                new Employee2("Charlie", "IT", 2000.0),
                new Employee2("David", "Finance", 1500.0),
                new Employee2("Eve", "IT", 4000.0),
                new Employee2("Frank", "Finance", 1800.0),
                new Employee2("Grace", "HR", 2300.0)
                                            );
        Map<String, Double> collect = list.stream()
                                          .collect(Collectors.groupingBy(
                                                  Employee2::department,
                                                  Collectors.averagingDouble(Employee2::salary)
                                                                        ));
        System.out.println(collect);

    }

    // Find the department with maximum people
    private static void ex53() {
        List<Employee2> list = Arrays.asList(
                new Employee2("Alice", "HR", 2000.0),
                new Employee2("Bob", "IT", 3000.0),
                new Employee2("Charlie", "IT", 2500.0),
                new Employee2("David", "Finance", 1500.0),
                new Employee2("Eve", "IT", 4000.0),
                new Employee2("Frank", "Finance", 1800.0),
                new Employee2("Grace", "HR", 2300.0)
                                            );
        String department = list.stream()
                         .collect(Collectors.groupingBy(
                                 Employee2::department,
                                 Collectors.counting()
                                                       ))
                         .entrySet()
                         .stream().max(Map.Entry.comparingByValue()).get().getKey();
        System.out.println(department); // IT
    }

    // Print the count of a particular substring
    private static void ex52() {
        String s = "byebyeBirdiebye!";
        String target = "bye";

        long count = IntStream.range(0, s.length() - (target.length() - 1))
                              .filter(i -> s.substring(i, i + target.length()).equals(target))
                              .count();
        System.out.println(count); // 3
    }

    // Return comparison of a Person object based on last name and then age
    private static void ex51() {
        List<Person2> list = Arrays.asList(
                new Person2("Bobby", "Smith", 23),
                new Person2("Bobby", "Adams", 25),
                new Person2("John", "Smith", 19),
                new Person2("Alice", "Johnson", 10)
                                          );

        List<Person2> collect = list.stream().sorted(Comparator.comparing(Person2::lastName).thenComparing(Person2::age)).collect(Collectors.toList());
        System.out.println(collect);
        // sorted by lastname only          [Person2[firstName=Bobby, lastName=Adams, age=25], Person2[firstName=Alice, lastName=Johnson, age=10], Person2[firstName=Bobby, lastName=Smith, age=23], Person2[firstName=John, lastName=Smith, age=19]]
        // sorted by both lastname and age [Person2[firstName=Bobby, lastName=Adams, age=25], Person2[firstName=Alice, lastName=Johnson, age=10], Person2[firstName=John, lastName=Smith, age=19], Person2[firstName=Bobby, lastName=Smith, age=23]]
    }

    // Print the Employees' name and salary whose salary >= 2000 and increase it by 1000
    private static void ex50() {
        List<Employee> employees = Arrays.asList(
                new Employee("Sam", "sam@gmail.com", 2000.0),
                new Employee("Adam", "adam@yahoo.com", 3000.0),
                new Employee("Peter", "peter@gmail.com", 1500.0)
                                                );
        Map<String, Double> collect = employees.stream()
                                               .filter(e -> e.salary() >= 2000) // then use peak if you have setter instead of collecting as map
                                               .collect(Collectors.toMap(Employee::name, e -> e.salary() + 1000));
        System.out.println(collect); // {Adam=4000.0, Sam=3000.0}
    }

    // Print distinct numbers which starts with "1" in descending order
    private static void ex49_B() {
        int[] arr = {12,34,11,34,67,121,121,52,78,114,565,1643,11};
        List<Integer> collect = Arrays.stream(arr)
                                      .mapToObj(String::valueOf)
                                      .filter(i -> i.startsWith("1"))
                                      .distinct()
                                      .map(Integer::valueOf)
                                      .sorted(Comparator.reverseOrder())
                                      .collect(Collectors.toList());
        System.out.println(collect); // [1643, 121, 12, 114, 11]
    }

    // Print the middle character of a given String
    private static void ex49() {
        String s = "travel"; // education
        // Method 1 - if string.length() is even
        Optional<Character> first = s.chars().mapToObj(c -> (char) c).skip(s.length() / 2).findFirst();
        System.out.println(first.get()); // Education - a

        // Method 2 - Handles both even and odd length
        String collect = Arrays.stream(s.split(""))
                               .skip((s.length() % 2 == 0) ? s.length() / 2 - 1 : s.length() / 2)
                               .limit((s.length() % 2 == 0) ? 2 : 1)
                               .collect(Collectors.joining());
        System.out.println(collect); // Travel - av
    }

    // Calculate average of age of male and female of employees
    private static void ex48() {
        List<Person> people = Arrays.asList(
                new Person("Peter", "Pune",23, "M"),
                new Person("Pamela", "Kolkata",24, "F"),
                new Person("David", "Pune",28, "M"),
                new Person("Payel", "Bangalore", 20, "F")
                                           );
        Map<String, Double> collect = people.stream()
                                            .collect(Collectors.groupingBy(
                                                    Person::gender,
                                                    Collectors.averagingDouble(Person::age)
                                                                          ));
        System.out.println(collect); // {F=22.0, M=25.5}
    }

    // Iterate vs Generate - Infinite Stream
    private static void ex47() {
        // Use generate if you just want it to supply a value
        Stream<Double> generate = Stream.generate(Math::random);
        // Use iterate if it demonstrate a pattern e.g i + 1
        Stream<Integer> iterate = Stream.iterate(0, i -> i + 1);
    }

    // Given a list of names, find all the names that start with letter 'A', and print its count
    private static void ex46() {
        List<String> names = Arrays.asList("Ema", "Bob", "Alice", "Anna");
        long a = names.stream().filter(n -> n.startsWith("A")).count();
        System.out.println(a); // 2

    }

    // Windowing Without Loops
    private static void ex2_hard() {
        List<Integer> list = List.of(1,2,3,4,5);
        List<int[]> list1 = IntStream.range(0, list.size() - 1)
                                     .mapToObj(i -> new int[]{list.get(i), list.get(i + 1)})
                                     .toList();
        list1.forEach(e -> System.out.print(Arrays.toString(e)));
        System.out.println(); // [1, 2][2, 3][3, 4][4, 5]

        List<List<Integer>> collect = list1.stream().map(x -> List.of(x[0], x[1])).collect(Collectors.toList());
        System.out.println(collect); // [[1, 2], [2, 3], [3, 4], [4, 5]]

    }

    // Given large text input, find the Top 3 Most Frequent Words
    private static void ex1_hard() {
        String frequentWords = """
                Java stream processing is great. Java streams can handle data efficiently.
                Stream operations make Java code clean. Data is the key here. More data, more fun.
                Parallel streams are powerful in Java. Stream data manipulation is an art.
                Java developers love streams for data tasks.
                """;
        Map<String, Long> group = Arrays.stream(frequentWords.split(" ")) // can do .map(String::toLowerCase)
                                          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Map<String, Long> collect = group.entrySet()
                                    .stream()
                                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
//                                    .map(Map.Entry::getKey)  // for single String only
                                    .limit(3)
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        System.out.println(collect);  // {Java=3, data=3, streams=3}
    }


    // Implement filter, reduction, average, min in a program
    private static void ex45() {
        List<Product> products = Arrays.asList(
                new Product(1, "Lifeboy", 20, "Soap"),
                new Product(2, "Portronics", 200, "Adapter"),
                new Product(3, "SurExcel", 90, "Washing Powder"),
                new Product(4, "Yamaha", 8000, "Guitar"),
                new Product(5, "Ikigai", 500, "Book")
                                              );
        // Print the name of the products whose price is below a certain threshold (e.g 100)
        List<String> collect = products.stream().filter(p -> p.price() < 100).map(Product::name).collect(Collectors.toList());
        System.out.println(collect); // [Lifeboy, SurExcel]

        // Create a new list containing products' price with reduction of 20%
        List<Double> collect1 = products.stream().map(p -> p.price() * 0.80).collect(Collectors.toList());
        System.out.println(collect1); // [16.0, 160.0, 72.0, 6400.0, 400.0]

        // Calculate the average price of all products
        OptionalDouble average = products.stream().mapToDouble(Product::price).average();
        System.out.println(average.getAsDouble());  // 1762.0

        // Find the product with the highest and lowest price
        Product highestPrice = products.stream().sorted(Comparator.comparing(Product::price).reversed()).findFirst().get();
        Product highestPrice2 = products.stream().max(Comparator.comparing(Product::price)).get();
        Product lowestPrice = products.stream().min(Comparator.comparing(Product::price)).get();
        System.out.println(highestPrice); // Product[id=4, name=Yamaha, price=8000, category=Guitar]
        System.out.println(highestPrice2); // Product[id=4, name=Yamaha, price=8000, category=Guitar]
        System.out.println(lowestPrice); // Product[id=1, name=Lifeboy, price=20, category=Soap]
    }

    // List to Set and to Map
    private static void ex44() {
        List<String> fruits = Arrays.asList("apple", "banana", "orange", "pineapple", "banana");
        Set<String> collect = fruits.stream().collect(Collectors.toSet());
        System.out.println(collect); // [banana, orange, apple, pineapple]
        Map<String, String> collect1 = fruits.stream()
                                           .collect(Collectors.toMap(fruit -> UUID.randomUUID().toString().substring(0, 8),
                                                   Function.identity())); // {3d2225f9=banana, 2b23002a=orange, a3775bbb=pineapple, 3add86e9=apple, bc8a19c8=banana}

        System.out.println(collect1);
    }

    // Best Practices: Divide complex stream pipelines
    private static void ex43() {
        List<Person> people = Arrays.asList(
                new Person("Peter", "Pune",23, "M"),
                new Person("Pamela", "Kolkata",24, "F"),
                new Person("David", "Pune",28, "M"),
                new Person("Payel", "Bangalore", 20, "F")
                                           );
        // Getting all unique Person's name whose age is >= 20 and in sorted manner
        // AVOID!!
        List<String> ans = people.stream().filter(p -> p.age() >= 20).map(Person::name).distinct().sorted().collect(Collectors.toList());
        System.out.println(ans); // [David, Pamela, Payel, Peter]
        // DO: Divide it
        List<String> filtered = people.stream().filter(e -> e.age() >= 20).map(Person::name).collect(Collectors.toList());
        List<String> ans2 = filtered.stream().distinct().sorted().collect(Collectors.toList());
        System.out.println(ans2); // [David, Pamela, Payel, Peter]
    }

    // Map vs Flatmap
    private static void ex42() {
        List<List<String>> colors = Arrays.asList(
                List.of("Red", "Blue"),
                List.of("Green"),
                List.of("Yellow", "Indigo", "Black")
                                                 );
        List<List<String>> collect = colors.stream().map(Function.identity()).collect(Collectors.toList());
        System.out.println(collect); // [[Red, Blue], [Green], [Yellow, Indigo, Black]]
        List<String> collect2 = colors.stream()
                                      .flatMap(x -> x.stream().map(String::toUpperCase)) // flatmap accepts stream as args
                                      .collect(Collectors.toList());
        System.out.println(collect2); // [Red, Blue, Green, Yellow, Indigo, Black]
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
                new Person("Peter", "Pune",23, "M"),
                new Person("Pamela", "Kolkata",24, "F"),
                new Person("David", "Pune",28, "M"),
                new Person("Payel", "Bangalore", 20, "F")
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
                new Person("Peter", "Pune",23, "M"),
                new Person("Pamela", "Kolkata",24, "F"),
                new Person("David", "Pune",28, "M"),
                new Person("Payel", "Bangalore", 20, "F")
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
                new Employee("Sam", "sam@gmail.com", 2000.0),
                new Employee("Adam", "adam@yahoo.com", 3000.0),
                new Employee("Peter", "peter@gmail.com", 1500.0)
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
