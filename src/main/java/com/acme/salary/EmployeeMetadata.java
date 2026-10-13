package com.acme.salary;

import java.util.List;

public class EmployeeMetadata {

    private final List<String> countries;
    private final List<String> departments;
    private final List<String> currencies;

    public EmployeeMetadata(List<String> countries, List<String> departments, List<String> currencies) {
        this.countries = countries;
        this.departments = departments;
        this.currencies = currencies;
    }

    public List<String> getCountries() {
        return countries;
    }

    public List<String> getDepartments() {
        return departments;
    }

    public List<String> getCurrencies() {
        return currencies;
    }
}
