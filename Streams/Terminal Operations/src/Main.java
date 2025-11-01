import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    record Employee(String Department, String name, Long salary) {}
    private static final List<Employee> employees = Arrays.asList(
            new Employee("IT", "Pikachu", 30000L),
            new Employee("HR", "Charizard", 50000L),
            new Employee("IT", "Snorlax", 70000L),
            new Employee("Finance", "Bulbasaur", 40000L));


    public static void main(String[] args) {
        partitioningBy();
        groupingBy();
    }

    // Group by whatever the parameter you passed (usually a property of an object)
    private static void groupingBy() {
        Map<String, List<Employee>> groupedEmployees = employees.stream()
                                                       .collect(Collectors.groupingBy(Employee::Department));
        groupedEmployees.forEach((department, employees) -> {
            System.out.println(department);
            employees.forEach(System.out::println);
            //System.out.println(employees);
        });
        System.out.println(groupedEmployees);
        /*
        {
            Finance=[Employee[Department=Finance, name=Bulbasaur, salary=40000]],
            HR=[Employee[Department=HR, name=Charizard, salary=50000]],
            IT=[Employee[Department=IT, name=Pikachu, salary=30000], Employee[Department=IT, name=Snorlax, salary=70000]]
        }
          */
    }

    // Divide the list based on the predicate(or condition) you passed
    private static void partitioningBy() {
        Map<Boolean, List<Employee>> partitionedEmployees = employees.stream()
                                                        .collect(Collectors.partitioningBy(e -> e.salary() >= 50000));

        partitionedEmployees.forEach((isGreaterThanSalary, employees) -> {
            System.out.println(isGreaterThanSalary);
            employees.forEach(System.out::println);
        });

        System.out.println(partitionedEmployees);

        /*
        {
            false=[Employee[Department=IT, name=Pikachu, salary=30000], Employee[Department=Finance, name=Bulbasaur, salary=40000]],
            true=[Employee[Department=HR, name=Charizard, salary=50000], Employee[Department=IT, name=Snorlax, salary=70000]]
         }
         */
    }
}
