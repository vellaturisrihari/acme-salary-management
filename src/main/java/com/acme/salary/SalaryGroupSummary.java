package com.acme.salary;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SalaryGroupSummary {

    private final String country;
    private final String currency;
    private final long employeeCount;
    private final BigDecimal totalPayroll;
    private final BigDecimal averageSalary;

    public SalaryGroupSummary(String currency, long employeeCount,
                              BigDecimal totalPayroll, double averageSalary) {
        this(null, currency, employeeCount, totalPayroll, averageSalary);
    }

    public SalaryGroupSummary(String country, String currency, long employeeCount,
                              BigDecimal totalPayroll, double averageSalary) {
        this.country = country;
        this.currency = currency;
        this.employeeCount = employeeCount;
        this.totalPayroll = totalPayroll;
        this.averageSalary = BigDecimal.valueOf(averageSalary).setScale(2, RoundingMode.HALF_UP);
    }

    public String getCountry() {
        return country;
    }

    public String getCurrency() {
        return currency;
    }

    public long getEmployeeCount() {
        return employeeCount;
    }

    public BigDecimal getTotalPayroll() {
        return totalPayroll;
    }

    public BigDecimal getAverageSalary() {
        return averageSalary;
    }
}
