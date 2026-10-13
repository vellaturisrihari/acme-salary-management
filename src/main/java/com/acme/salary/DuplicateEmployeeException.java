package com.acme.salary;

public class DuplicateEmployeeException extends RuntimeException {

    public DuplicateEmployeeException(String message) {
        super(message);
    }
}
