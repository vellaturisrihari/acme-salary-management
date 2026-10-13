package com.acme.salary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void trimsSearchFiltersAndPassesPaginationToRepository() {
        PageRequest pageable = PageRequest.of(2, 25, Sort.by("lastName"));
        when(employeeRepository.search("alex", "India", null, pageable))
                .thenReturn(new PageImpl<Employee>(java.util.Collections.<Employee>emptyList()));

        employeeService.search(" alex ", " India ", "  ", pageable);

        verify(employeeRepository).search("alex", "India", null, pageable);
    }

    @Test
    void normalizesNewEmployeeIdentifiersAndCurrencyBeforeSaving() {
        Employee employee = employee(" ac-test-1 ", " HR@acme.example ", "usd");
        when(employeeRepository.existsByEmployeeNumber("AC-TEST-1")).thenReturn(false);
        when(employeeRepository.existsByEmail("hr@acme.example")).thenReturn(false);
        when(employeeRepository.save(employee)).thenReturn(employee);

        assertSame(employee, employeeService.create(employee));

        assertEquals("AC-TEST-1", employee.getEmployeeNumber());
        assertEquals("hr@acme.example", employee.getEmail());
        assertEquals("USD", employee.getCurrency());
        verify(employeeRepository).save(employee);
    }

    @Test
    void rejectsDuplicateEmailBeforePersisting() {
        Employee employee = employee("AC-TEST-2", "hr@acme.example", "USD");
        when(employeeRepository.existsByEmployeeNumber("AC-TEST-2")).thenReturn(false);
        when(employeeRepository.existsByEmail("hr@acme.example")).thenReturn(true);

        assertThrows(DuplicateEmployeeException.class, () -> employeeService.create(employee));

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updatesExistingEmployeeWithoutChangingItsIdentity() {
        Employee existing = employee("AC-TEST-3", "before@acme.example", "USD");
        Employee changes = employee("ac-test-3", "AFTER@acme.example", "gbp");
        when(employeeRepository.findById(42L)).thenReturn(Optional.of(existing));
        when(employeeRepository.save(existing)).thenReturn(existing);

        Employee updated = employeeService.update(42L, changes);

        assertSame(existing, updated);
        assertEquals("AC-TEST-3", updated.getEmployeeNumber());
        assertEquals("after@acme.example", updated.getEmail());
        assertEquals("GBP", updated.getCurrency());
        verify(employeeRepository, never()).existsByEmployeeNumber("AC-TEST-3");
        verify(employeeRepository).existsByEmail("after@acme.example");
    }

    @Test
    void rejectsUpdatesWhenEmployeeDoesNotExist() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(javax.persistence.EntityNotFoundException.class,
                () -> employeeService.update(99L, employee("AC-TEST-4", "nobody@acme.example", "USD")));

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    private Employee employee(String number, String email, String currency) {
        return new Employee(number, "Alex", "Example", email, "United States",
                "Engineering", "Engineer", currency, new BigDecimal("100000.00"),
                LocalDate.of(2020, 1, 15));
    }
}
