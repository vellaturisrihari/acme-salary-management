package com.acme.salary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EmployeeSeedData implements CommandLineRunner {

    private static final int EMPLOYEE_COUNT = 10_000;
    private static final String[][] LOCATIONS = {
        {"United States", "USD"}, {"United Kingdom", "GBP"}, {"Canada", "CAD"},
        {"Australia", "AUD"}, {"Germany", "EUR"}, {"France", "EUR"},
        {"India", "INR"}, {"Japan", "JPY"}, {"Singapore", "SGD"}, {"Brazil", "BRL"}
    };
    private static final String[] DEPARTMENTS = {
        "Engineering", "Finance", "Human Resources", "Legal", "Marketing",
        "Operations", "Product", "Sales", "Customer Success", "Data"
    };
    private static final String[] TITLES = {
        "Analyst", "Associate", "Specialist", "Manager", "Senior Manager",
        "Director", "Senior Engineer", "Product Manager", "Consultant", "Team Lead"
    };
    private static final String[] FIRST_NAMES = {
        "Alex", "Avery", "Casey", "Jordan", "Morgan", "Riley", "Sam", "Taylor",
        "Cameron", "Jamie", "Quinn", "Robin", "Skyler", "Drew", "Parker", "Reese"
    };
    private static final String[] LAST_NAMES = {
        "Adams", "Bennett", "Chen", "Diaz", "Evans", "Foster", "Gupta", "Hall",
        "Ito", "Jones", "Khan", "Lee", "Martin", "Patel", "Reed", "Singh"
    };

    private final EmployeeRepository employeeRepository;

    public EmployeeSeedData(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(String... args) {
        if (employeeRepository.count() != 0) {
            return;
        }
        List<Employee> employees = new ArrayList<Employee>(EMPLOYEE_COUNT);
        for (int i = 0; i < EMPLOYEE_COUNT; i++) {
            int number = i + 1;
            String employeeNumber = String.format("AC%05d", number);
            String[] location = LOCATIONS[i % LOCATIONS.length];
            employees.add(new Employee(employeeNumber, FIRST_NAMES[i % FIRST_NAMES.length],
                    LAST_NAMES[(i / FIRST_NAMES.length) % LAST_NAMES.length],
                    String.format("employee%05d@acme.example", number), location[0],
                    DEPARTMENTS[i % DEPARTMENTS.length], TITLES[(i / DEPARTMENTS.length) % TITLES.length],
                    location[1], BigDecimal.valueOf(45000L + (number * 137L % 155000L), 0)
                            .setScale(2), LocalDate.of(2010 + number % 15, 1 + number % 12,
                            1 + number % 27)));
        }
        for (int start = 0; start < employees.size(); start += 500) {
            employeeRepository.saveAll(employees.subList(start, Math.min(start + 500, employees.size())));
        }
    }
}
