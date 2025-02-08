package com.bigcompany.service;

import com.bigcompany.model.Employee;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;


import java.io.*;
import java.util.*;

public class EmployeeService {


    private Map<Integer, Employee> employees = new HashMap<>();

    public Map<Integer, List<Employee>> getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(Map<Integer, List<Employee>> subordinates) {
        this.subordinates = subordinates;
    }

    private Map<Integer, List<Employee>> subordinates = new HashMap<>();

    public Map<Integer, Employee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Map<Integer, Employee> employees) {
        this.employees = employees;
    }

    public void readEmployeeData(String filename) throws IOException, CsvValidationException {
        ClassLoader classLoader = getClass().getClassLoader();


        try (InputStream inputStream = classLoader.getResourceAsStream(filename)) {
            if (inputStream == null) {
                System.out.println("File not found");
                throw new FileNotFoundException("File not found");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 CSVReader csvReader = new CSVReader(reader)) {

                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    if (line[0].equalsIgnoreCase("Id")) continue;
                    line[0] = removeBOM(line[0]);
                    int id = Integer.parseInt(line[0]);
                    String firstName = line[1];
                    String lastName = line[2];
                    double salary = Double.parseDouble(line[3]);
                    String managerId = line[4].isEmpty() ? null : (line[4]);

                    Employee employee = new Employee(id, firstName, lastName, salary, managerId);
                    employees.put(id, employee);

                    if (managerId != null) {
                        List<Employee> subordinateList = subordinates.get(managerId);
                        if (subordinateList == null) {
                            subordinateList = new ArrayList<>();
                            subordinates.put(Integer.valueOf(managerId), subordinateList);
                        }
                        subordinateList.add(employee);
                    }
                }
            }catch (IOException | CsvValidationException e) {
                throw new CsvValidationException(e.getMessage());
            }
        }catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    public Map<String, List<String>> checkManagerSalaries() {
        List<String> lessEarningManagers = new ArrayList<>();
        List<String> moreEarningManagers = new ArrayList<>();
        Map<String, List<String>> result = new HashMap<>();

        for (Map.Entry<Integer, List<Employee>> entry : subordinates.entrySet()) {
            Integer managerId = entry.getKey();
            Employee manager = employees.get(managerId);
            double managerSalary = manager.getSalary();
            List<Employee> directSubordinates = entry.getValue();
            double averageSalary = directSubordinates.stream().mapToDouble(Employee::getSalary).average().orElse(0.0);
            double minSalary = averageSalary * 1.2;
            double maxSalary = averageSalary * 1.5;

            if (managerSalary < minSalary) {
                lessEarningManagers.add(manager.getFirstName());
                System.out.println("Manager " + manager.getFirstName() + " earns " + (minSalary - managerSalary) + " less than needed.");
            }
            if (managerSalary > maxSalary) {
                moreEarningManagers.add(manager.getFirstName());
                System.out.println("Manager " + manager.getFirstName() + " earns " + (managerSalary - maxSalary) + " more than needed.");
            }
        }

        result.put("lessEarningManagers", lessEarningManagers);
        result.put("moreEarningManagers", moreEarningManagers);

        return result;
    }

    public List<String> checkReportingLines() {
        List<String> employeesWithLongRL = new ArrayList<>();
        for (Employee employee : employees.values()) {
            int reportingCounter = getNoOfStepsToCEO(employee);
            if (reportingCounter > 4) {
                employeesWithLongRL.add(employee.getFirstName());
                System.out.println("Employee " + employee.getFirstName() + " has too long reporting line: " + reportingCounter + " steps.");
            }
        }
        return employeesWithLongRL;
    }

    private int getNoOfStepsToCEO(Employee employee) {
        if (employee == null || employee.getManagerId() == null) {
            return 0;
        }
        Employee manager = employees.get(Integer.parseInt(employee.getManagerId()));
        return 1 + getNoOfStepsToCEO(manager);
    }


    private String removeBOM(String str) {
        if (str.startsWith("\uFEFF")) {
            return str.substring(1);
        }
        return str;
    }
}
