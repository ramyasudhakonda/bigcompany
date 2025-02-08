

import com.bigcompany.service.EmployeeService;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;

public class EmployeeAnalyzer {
    public static void main(String[] args) {
        try {
            EmployeeService employeeService = new EmployeeService();
            employeeService.readEmployeeData("Book1.csv");
            employeeService.checkManagerSalaries();
            employeeService.checkReportingLines();
        } catch (IOException | CsvValidationException e) {
            System.out.println(" Exception Occured : " + e.getMessage());
        }
    }
}
