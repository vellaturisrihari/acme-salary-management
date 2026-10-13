package com.acme.salary;

import java.util.List;

public class DashboardSummary {

    private final long employeeCount;
    private final List<SalaryGroupSummary> payrollByCurrency;
    private final List<SalaryGroupSummary> payrollByCountry;

    public DashboardSummary(long employeeCount, List<SalaryGroupSummary> payrollByCurrency,
                           List<SalaryGroupSummary> payrollByCountry) {
        this.employeeCount = employeeCount;
        this.payrollByCurrency = payrollByCurrency;
        this.payrollByCountry = payrollByCountry;
    }

    public long getEmployeeCount() {
        return employeeCount;
    }

    public List<SalaryGroupSummary> getPayrollByCurrency() {
        return payrollByCurrency;
    }

    public List<SalaryGroupSummary> getPayrollByCountry() {
        return payrollByCountry;
    }
}
