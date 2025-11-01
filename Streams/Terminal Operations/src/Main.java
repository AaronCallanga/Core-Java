import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
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


    public static void main(String[] args) {
        /*
        Collector<T, A, R> is a strategy object that tells the Stream API:
            - how to accumulate elements (A accumulator type),
            - how to combine partial results (for parallel),
            - and how to finish to produce the result R.
         */


        //partitioningBy();
        //groupingBy();
        //reduce();
        //basicCollectors();
        joining();
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
    // returns Map of <FunctionType, List<Type>>/Map/Set or,   FunctionType - the args you passed in the groupingBy()
    private static void groupingBy() {
        Map<String, List<Employee>> groupedEmployees = employees.stream()
                                                       .collect(Collectors.groupingBy(Employee::department));

        Map<String, Set<Employee>> groupedEmployee2 = employees.stream()
                                                               .collect(Collectors.groupingBy(Employee::department, Collectors.mapping(Function.identity(), Collectors.toSet())));

        Map<String, Map<String, Long>> groupedEmployees3 = employees.stream()
                                                          .collect(Collectors.groupingBy(Employee::department, Collectors.toMap(Employee::name, Employee::salary)));

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
