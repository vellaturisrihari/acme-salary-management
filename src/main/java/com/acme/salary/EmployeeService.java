package com.acme.salary;

import java.util.List;
import java.util.Locale;
import javax.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public Page<Employee> search(String search, String country, String department, Pageable pageable) {
        return employeeRepository.search(normalize(search), normalize(country),
                normalize(department), pageable);
    }

    @Transactional(readOnly = true)
    public Employee get(long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee " + id + " was not found"));
    }

    @Transactional
    public Employee create(Employee employee) {
        employee.setEmployeeNumber(employee.getEmployeeNumber().trim().toUpperCase(Locale.ROOT));
        employee.setEmail(employee.getEmail().trim().toLowerCase(Locale.ROOT));
        employee.setCurrency(employee.getCurrency().toUpperCase(Locale.ROOT));
        if (employeeRepository.existsByEmployeeNumber(employee.getEmployeeNumber())) {
            throw new DuplicateEmployeeException("Employee number is already in use");
        }
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new DuplicateEmployeeException("Email is already in use");
        }
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee update(long id, Employee changes) {
        Employee employee = get(id);
        String employeeNumber = changes.getEmployeeNumber().trim().toUpperCase(Locale.ROOT);
        String email = changes.getEmail().trim().toLowerCase(Locale.ROOT);
        if (!employee.getEmployeeNumber().equals(employeeNumber)
                && employeeRepository.existsByEmployeeNumber(employeeNumber)) {
            throw new DuplicateEmployeeException("Employee number is already in use");
        }
        if (!employee.getEmail().equals(email) && employeeRepository.existsByEmail(email)) {
            throw new DuplicateEmployeeException("Email is already in use");
        }
        employee.setEmployeeNumber(employeeNumber);
        employee.setFirstName(changes.getFirstName());
        employee.setLastName(changes.getLastName());
        employee.setEmail(email);
        employee.setCountry(changes.getCountry());
        employee.setDepartment(changes.getDepartment());
        employee.setJobTitle(changes.getJobTitle());
        employee.setCurrency(changes.getCurrency().toUpperCase(Locale.ROOT));
        employee.setAnnualSalary(changes.getAnnualSalary());
        employee.setHireDate(changes.getHireDate());
        return employeeRepository.save(employee);
    }

    @Transactional
    public void delete(long id) {
        employeeRepository.delete(get(id));
    }

    @Transactional(readOnly = true)
    public DashboardSummary dashboard() {
        List<SalaryGroupSummary> byCurrency = employeeRepository.summarizeByCurrency();
        List<SalaryGroupSummary> byCountry = employeeRepository.summarizeByCountry();
        return new DashboardSummary(employeeRepository.count(), byCurrency, byCountry);
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
