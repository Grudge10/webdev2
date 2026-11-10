package com.webdev2.service;

import com.webdev2.model.Employee;
import com.webdev2.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public String getDepartmentNameWithoutTransaction(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        return employee.getDepartment().getName();
    }

    @Transactional(readOnly = true)
    public String getDepartmentNameTransactional(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        return employee.getDepartment().getName();
    }

    public String getDepartmentNameFetchJoin(Long id) {
        Employee employee = employeeRepository.findByIdWithDepartment(id).orElseThrow();
        return employee.getDepartment().getName();
    }
}
