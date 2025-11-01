import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    record Employee(String department, String name, Long salary) {}
    private static final List<Employee> employees = Arrays.asList(
            new Employee("IT", "Pikachu", 30000L),
            new Employee("HR", "Charizard", 50000L),
            new Employee("IT", "Snorlax", 70000L),
            new Employee("Finance", "Bulbasaur", 40000L));
            //new Employee("Finance", "Bulbasaur", 40000L));   Test it for Set collections


    public static void main(String[] args) {
        /*
        Collector<T, A, R> is a strategy object that tells the Stream API:
            - how to accumulate elements (A accumulator type),
            - how to combine partial results (for parallel),
            - and how to finish to produce the result R.
         */


        //partitioningBy();
        //groupingBy();
        groupingByWithDownstreamCollectors();
        //reduce();
        //basicCollectors();
        //joining();
        //counting();
        //summarizingType();


    }

    private static void groupingByWithDownstreamCollectors() {

        // Grouping + Mapping (w/ Collections)
        // Can also do .toSet() to return unique items depending on the mapper(first args of .mapping())
        // or Collectors.toCollection(Supplier<C> collectionFactory) such as (Collectors.toCollection(ArrayDeque::new)
        // Collects the elements within each group into a List, Set, or a specific type of Collection.
        Map<String, List<String>> listOfEmployeeNamesPerDepartment = employees.stream()
                                                                              .collect(Collectors.groupingBy(Employee::department, Collectors.mapping(Employee::name, Collectors.toList())));
        System.out.println("Grouping + Mapping -> List of Employees' Names Per Department: " + listOfEmployeeNamesPerDepartment);

        // Grouping + Mapping (w/ Joining)
        // Concatenates CharSequence elements within each group into a single String, separated by the specified delimiter.
        Map<String, String> joinedEmployeeNamePerDepartment = employees.stream()
                                                                       .collect(Collectors.groupingBy(Employee::department, Collectors.mapping(Employee::name, Collectors.joining(", "))));
        System.out.println("Grouping + Joining -> List of Employees' Names Per Department: " + joinedEmployeeNamePerDepartment);

        // Grouping + Mapping (w/ reduction)
        // Performs a general reduction operation on the elements within each group using a BinaryOperator.
        Map<String, Optional<String>> longestNameByDepartment = employees.stream()
                                                                         .collect(Collectors.groupingBy(Employee::department,
                                                                                 Collectors.mapping(Employee::name, Collectors.reducing((name1, name2) -> name1.length() > name2.length() ? name1 : name2))));
        System.out.println("Grouping + Mapping (w/ reduction) -> Longest Employees' Name Per Department: " + longestNameByDepartment);

        // Grouping + Counting
        // Counts the number of elements within each group created by groupingBy()
        Map<String, Long> numberOfEmployeesPerDepartment = employees.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.counting()));
        System.out.println("Grouping + Counting -> Number of Employees Per Department: " + numberOfEmployeesPerDepartment);

        // Grouping + MaxBy/MinBy
        // Finds the maximum or minimum element within each group based on a provided Comparator.
        Map<String, Optional<Employee>> highestSalaryPerDepartment = employees.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.maxBy(Comparator.comparing(Employee::salary))));
        System.out.println("Grouping + MaxBy -> Employee Who Got Highest Salary Per Department: " + highestSalaryPerDepartment);

        // Grouping + Summing (sum) (summingInt(), summingLong(), summingDouble())
        // Computes the sum of a numeric-valued function applied to the elements within each group.
        Map<String, Long> totalSalaryPerDepartment = employees.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.summingLong(Employee::salary)));
        System.out.println("Grouping + Summing -> Total Salary Per Department: " + totalSalaryPerDepartment);

        // Grouping + Averaging (average) (averagingInt(), averagingLong(), averagingDouble())
        // Computes the average of a numeric-valued function applied to the elements within each group.
        Map<String, Double> averageSalaryPerDepartment = employees.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.averagingDouble(Employee::salary)));
        System.out.println("Grouping + Summing -> Average Salary Per Department: " + averageSalaryPerDepartment);

        // Grouping + CollectingAndThen
        // Applies a finishing transformation to the result of another downstream collector.
        Map<String, List<String>> listOfNamesInUpperCasePerDepartment = employees.stream()
                .collect(Collectors.groupingBy(Employee::department,
                        Collectors.collectingAndThen(Collectors.mapping(Employee::name,
                                Collectors.toList()),
                                list -> list.stream().map(String::toUpperCase).collect(Collectors.toList()))));
        System.out.println("Grouping + CollectingAndThen -> Uppercase Names Per Department: " + listOfNamesInUpperCasePerDepartment);

    }

    // Produces IntSummaryStatistics/LongSummaryStatistics/DoubleSummaryStatistics with count, sum, min, max, average.
    private static void summarizingType() {
        LongSummaryStatistics stats = employees.stream()
                                               .collect(Collectors.summarizingLong(Employee::salary));
        stats.getAverage();
        stats.getMax();
        stats.getMin();
        stats.getSum();
        System.out.println(stats);

        DoubleSummaryStatistics stats2 = employees.stream()
                                                 .collect(Collectors.summarizingDouble(Employee::salary));
        IntSummaryStatistics stats3 = employees.stream()
                                               .collect(Collectors.summarizingInt(e -> Integer.valueOf(e.salary.toString())));
        System.out.println(stats2);
        System.out.println(stats3);
    }

    // Simple terminal collector giving a Long. It’s equivalent to stream.count(), but useful as a downstream collector.
    private static void counting() {
        long count = Stream.of("a", "b", "c").collect(Collectors.counting());
        System.out.println(count); // 3

        long count2 = Stream.of("a", "b", "c").count();
        System.out.println(count2); // 3

    }

    // Concatenates stream of CharSequences (combine strings)
    private static void joining() {
        String csv = Stream.of("a","b","c").collect(Collectors.joining(","));
        System.out.println(csv);
        // "a,b,c"

        // You can specify a delimiter(separator between stream values), prefix and a suffix .joining(delimiter, prefix, suffix)
        String htmlList = Stream.of("one","two")
                                .collect(Collectors.joining("--", "<ul><li>", "</li></ul>"));
        System.out.println(htmlList);
        // <ul><li>one--two</li></ul>
    }

    private static void basicCollectors() {
        List<String> list = Stream.of("a","b","c").collect(Collectors.toList());
        Set<String> set = Stream.of("a","b","b").collect(Collectors.toSet());
        Deque<String> deque = Stream.of("x","y").collect(Collectors.toCollection(ArrayDeque::new));

        // Immutable List
        List<String> immutableList = Stream.of("a","b","c").collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

        // Map - error if there is duplicate key. You have to handle it with merge function or just use groupingBy instead
        Map<String, Employee> employeeMap = employees.stream()
                .collect(Collectors.toMap(Employee::department, Function.identity(), (existing, replacement) -> existing));
        employeeMap.forEach((k, v) -> System.out.println(v));
    }

    // Reduce - to aggregate or combine elements into a single result, such as computing the maximum, minimum, sum, or product.
    // Param: identity = An initial value of type T. accumulator = A function that combines two values of type T.




    private static void reduce() {
        // Example 1: Getting Sum
        Long reduce = employees.stream().map(Employee::salary)
                               .reduce(0L, (carry, e) -> carry + e);
        System.out.println(reduce);


        // Example 2: Finding the longest word
        List<String> words = Arrays.asList("GFG", "Geeks", "for", "GeeksQuiz", "GeeksforGeeks");
        // Using reduce to find the longest string in the list
        Optional<String> longestString = words.stream()
                                              .reduce((word1, word2) -> word1.length() > word2.length() ? word1 : word2);
        longestString.ifPresent(System.out::println);

        // Example 3: Combining strings
        String[] array = { "Geeks", "for", "Geeks" };
        // Using reduce to concatenate strings with a hyphen
        Optional<String> combinedString = Arrays.stream(array)
                                                .reduce((str1, str2) -> str1 + "-" + str2);
        combinedString.ifPresent(System.out::println);

    }

    // Group by whatever the parameter you passed (usually a property of an object) (a function that returns a value)
    // returns Map of <FunctionType, List<Type>>/Map/Set        FunctionType - the args you passed in the groupingBy()
    private static void groupingBy() {
        Map<String, List<Employee>> groupedEmployees = employees.stream()
                                                       .collect(Collectors.groupingBy(Employee::department));
        System.out.println("String, List<Employee> " + groupedEmployees);

        Map<String, Set<Employee>> groupedEmployees2 = employees.stream()
                                                               .collect(Collectors.groupingBy(Employee::department, Collectors.mapping(Function.identity(), Collectors.toSet())));
        System.out.println("String, Set<Employee> " + groupedEmployees2);

        Map<String, Map<String, Long>> groupedEmployees3 = employees.stream()
                                                          .collect(Collectors.groupingBy(Employee::department, Collectors.toMap(Employee::name, Employee::salary)));
        System.out.println("String, Map<String, Long> " + groupedEmployees3);

        groupedEmployees.forEach((department, employees) -> {
            System.out.println(department);
            employees.forEach(System.out::println);
            //System.out.println(employees);
        });

        System.out.println("Employees' department = HR");
        groupedEmployees.get("HR").forEach(System.out::println);
        System.out.println("Employees' department = IT");
        groupedEmployees.get("IT").forEach(System.out::println);
        System.out.println("Employees' department = Finance");
        groupedEmployees.get("Finance").forEach(System.out::println);

        System.out.println(groupedEmployees);
        /*
        {
            Finance=[Employee[Department=Finance, name=Bulbasaur, salary=40000]],
            HR=[Employee[Department=HR, name=Charizard, salary=50000]],
            IT=[Employee[Department=IT, name=Pikachu, salary=30000], Employee[Department=IT, name=Snorlax, salary=70000]]
        }
          */
    }

    // Divide (and group) the list based on the predicate(or condition) you passed
    // returns Map<Boolean, List<Type>>,  Boolean value are true or false and assign employees based on the predicate
    private static void partitioningBy() {
        Map<Boolean, List<Employee>> partitionedEmployees = employees.stream()
                                                        .collect(Collectors.partitioningBy(e -> e.salary() >= 50000));

        partitionedEmployees.forEach((isGreaterThanSalary, employees) -> {
            System.out.println(isGreaterThanSalary);
            employees.forEach(System.out::println);
        });
        System.out.println("Employees' salaries > 50000");
        partitionedEmployees.get(true).forEach(System.out::println);
        System.out.println("Employees' salaries < 50000");
        partitionedEmployees.get(false).forEach(System.out::println);

        System.out.println(partitionedEmployees);

        /*
        {
            false=[Employee[Department=IT, name=Pikachu, salary=30000], Employee[Department=Finance, name=Bulbasaur, salary=40000]],
            true=[Employee[Department=HR, name=Charizard, salary=50000], Employee[Department=IT, name=Snorlax, salary=70000]]
         }
         */
    }
}
