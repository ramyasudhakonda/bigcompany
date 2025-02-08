package com.bigcompany.service;



import com.bigcompany.model.Employee;
import com.opencsv.exceptions.CsvValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Executable;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeServiceTest {
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        employeeService = new EmployeeService();
    }

    @Test
    public void testReadEmployeeData() throws IOException, CsvValidationException {
        employeeService.readEmployeeData("test_file.csv");
        Map<Integer, Employee> employees = employeeService.getEmployees();
        assertNotNull(employees);
        assertEquals(5, employees.size()); // Assume test file has 5 employees
    }

    @Test
    public void testCsvValidationException() {
        EmployeeService employeeService = new EmployeeService();

        // Define the filename that does not exist
        String nonExistentFileName = "test_file1.csv";

        // Assert that a FileNotFoundException is thrown
        assertThrows(FileNotFoundException.class, () -> {
            employeeService.readEmployeeData(nonExistentFileName);
        });
        }




        @Test
    public void testCheckReportingLines() throws IOException, CsvValidationException {
        employeeService.readEmployeeData("test_file.csv");
        employeeService.checkReportingLines(); // Output should be manually verified
    }

    @Test
    public void testManagerSalaryBelowRange() {
        Employee manager = new Employee(1, "Manager1", "LastName1", 1050, null);
        Employee sub1 = new Employee(2, "Sub1", "LastName2", 1000, "1");
        Employee sub2 = new Employee(3, "Sub2", "LastName3", 800, "1");

        Map<Integer, Employee> employees = new HashMap<>();
        employees.put(1, manager);
        employees.put(2, sub1);
        employees.put(3, sub2);
        employeeService.setEmployees(employees);

        // Set subordinates
        Map<Integer, List<Employee>> subordinates = new HashMap<>();
        List<Employee> managerSubordinates = new ArrayList<>();
        managerSubordinates.add(sub1);
        managerSubordinates.add(sub2);
        subordinates.put(1, managerSubordinates);
        employeeService.setSubordinates(subordinates);

        Map<String, List<String>> result = employeeService.checkManagerSalaries(); // Manager1 earns less than needed
        assertEquals(result.get("lessEarningManagers").get(0), "Manager1");
    }


    @Test
    public void testManagerSalaryAboveRange() {
        Employee manager = new Employee(1, "Manager1", "LastName1", 3000, null);
        Employee sub1 = new Employee(2, "Sub1", "LastName2", 1000, "1");
        Employee sub2 = new Employee(3, "Sub2", "LastName3", 800, "1");

        Map<Integer, Employee> employees = new HashMap<>();
        employees.put(1, manager);
        employees.put(2, sub1);
        employees.put(3, sub2);
        employeeService.setEmployees(employees);

        // Set subordinates
        Map<Integer, List<Employee>> subordinates = new HashMap<>();
        List<Employee> managerSubordinates = new ArrayList<>();
        managerSubordinates.add(sub1);
        managerSubordinates.add(sub2);
        subordinates.put(1, managerSubordinates);
        employeeService.setSubordinates(subordinates);

        employeeService.checkManagerSalaries(); // Manager1 earns more than needed
    }

    @Test
    public void testLongReportingLine() {
        Employee ceo = new Employee(1, "CEO", "LastName1", 5000, null);
        Employee manager1 = new Employee(2, "Manager1", "LastName2", 3000, "1");
        Employee manager2 = new Employee(3, "Manager2", "LastName3", 2000, "2");
        Employee manager3 = new Employee(4, "Manager3", "LastName4", 1500, "3");
        Employee manager4 = new Employee(5, "Manager4", "LastName5", 1000, "4");
        Employee employee = new Employee(6, "Employee", "LastName6", 800, "5");

        Map<Integer, Employee> employees = new HashMap<>();
        employees.put(1, ceo);
        employees.put(2, manager1);
        employees.put(3, manager2);
        employees.put(4, manager3);
        employees.put(5, manager4);
        employees.put(6, employee);
        employeeService.setEmployees(employees);

        // Set subordinates
        Map<Integer, List<Employee>> subordinates = new HashMap<>();
        subordinates.put(1, Arrays.asList(manager1));
        subordinates.put(2, Arrays.asList(manager2));
        subordinates.put(3, Arrays.asList(manager3));
        subordinates.put(4, Arrays.asList(manager4));
        subordinates.put(5, Arrays.asList(employee));
        employeeService.setSubordinates(subordinates);

        List<String> result = employeeService.checkReportingLines();
        assertEquals(result.get(0), "Employee");    // Employee has too long reporting line
    }

    @Test
    public void testShortReportingLine() {
        Employee ceo = new Employee(1, "CEO", "LastName1", 5000, null);
        Employee manager1 = new Employee(2, "Manager1", "LastName2", 3000, "1");
        Employee employee = new Employee(3, "Employee", "LastName3", 2000, "2");

        Map<Integer, Employee> employees = new HashMap<>();
        employees.put(1, ceo);
        employees.put(2, manager1);
        employees.put(3, employee);
        employeeService.setEmployees(employees);

        // Set subordinates
        Map<Integer, List<Employee>> subordinates = new HashMap<>();
        subordinates.put(1, Arrays.asList(manager1));
        subordinates.put(2, Arrays.asList(employee));
        employeeService.setSubordinates(subordinates);

        List<String> result =  employeeService.checkReportingLines();
        assertEquals(result.size(),0); // Employee does not have too long reporting line
    }

}

