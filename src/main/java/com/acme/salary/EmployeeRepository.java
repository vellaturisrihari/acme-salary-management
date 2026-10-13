package com.acme.salary;

import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE "
            + "(:search IS NULL OR "
            + "LOWER(e.employeeNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(e.country) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(e.department) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(e.jobTitle) LIKE LOWER(CONCAT('%', :search, '%'))) "
            + "AND (:country IS NULL OR e.country = :country) "
            + "AND (:department IS NULL OR e.department = :department)")
    Page<Employee> search(@Param("search") String search,
                          @Param("country") String country,
                          @Param("department") String department,
                          Pageable pageable);

    Optional<Employee> findByEmployeeNumber(String employeeNumber);

    boolean existsByEmail(String email);

    boolean existsByEmployeeNumber(String employeeNumber);

    @Query("SELECT DISTINCT e.country FROM Employee e ORDER BY e.country")
    List<String> findCountries();

    @Query("SELECT DISTINCT e.department FROM Employee e ORDER BY e.department")
    List<String> findDepartments();

    @Query("SELECT new com.acme.salary.SalaryGroupSummary(e.currency, COUNT(e), "
            + "SUM(e.annualSalary), AVG(e.annualSalary)) "
            + "FROM Employee e GROUP BY e.currency ORDER BY e.currency")
    List<SalaryGroupSummary> summarizeByCurrency();

    @Query("SELECT new com.acme.salary.SalaryGroupSummary(e.country, e.currency, COUNT(e), "
            + "SUM(e.annualSalary), AVG(e.annualSalary)) "
            + "FROM Employee e GROUP BY e.country, e.currency ORDER BY e.country")
    List<SalaryGroupSummary> summarizeByCountry();
}
