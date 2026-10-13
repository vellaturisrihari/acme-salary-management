package com.acme.salary;

import java.util.Arrays;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Validated
public class EmployeeController {

    private static final List<String> SORT_FIELDS = Arrays.asList(
            "employeeNumber", "firstName", "lastName", "country",
            "department", "annualSalary", "hireDate");
    private static final List<String> CURRENCIES = Arrays.asList(
            "AUD", "BRL", "CAD", "EUR", "GBP", "INR", "JPY", "SGD", "USD");

    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeService employeeService, EmployeeRepository employeeRepository) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/employees")
    public Page<Employee> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "25") @Min(1) int size,
            @RequestParam(defaultValue = "lastName") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        if (!SORT_FIELDS.contains(sort)) {
            throw new IllegalArgumentException("Unsupported sort field");
        }
        if (!"asc".equalsIgnoreCase(direction) && !"desc".equalsIgnoreCase(direction)) {
            throw new IllegalArgumentException("Sort direction must be asc or desc");
        }
        PageRequest pageable = PageRequest.of(page, Math.min(size, 100),
                Sort.by("desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, sort));
        return employeeService.search(search, country, department, pageable);
    }

    @GetMapping("/employees/{id}")
    public Employee get(@PathVariable long id) {
        return employeeService.get(id);
    }

    @PostMapping("/employees")
    @ResponseStatus(HttpStatus.CREATED)
    public Employee create(@Valid @RequestBody Employee employee) {
        return employeeService.create(employee);
    }

    @PutMapping("/employees/{id}")
    public Employee update(@PathVariable long id, @Valid @RequestBody Employee employee) {
        return employeeService.update(id, employee);
    }

    @DeleteMapping("/employees/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        employeeService.delete(id);
    }

    @GetMapping("/dashboard")
    public DashboardSummary dashboard() {
        return employeeService.dashboard();
    }

    @GetMapping("/metadata")
    public EmployeeMetadata metadata() {
        return new EmployeeMetadata(employeeRepository.findCountries(),
                employeeRepository.findDepartments(), CURRENCIES);
    }
}
