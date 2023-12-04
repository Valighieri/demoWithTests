package com.example.demowithtests;

import com.example.demowithtests.domain.Address;
import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.domain.Gender;
import com.example.demowithtests.repository.EmployeeRepository;
import com.example.demowithtests.util.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Employee Repository Tests")
public class RepositoryTests {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @Order(1)
    @Rollback(value = false)
    @DisplayName("Save employee test")
    public void saveEmployeeTest() {

        var employee = Employee.builder()
                .name("Mark")
                .country("England")
                .addresses(new HashSet<>(Set.of(
                        Address
                                .builder()
                                .country("UK")
                                .build())))
                .gender(Gender.M)
                .build();

        employeeRepository.save(employee);

        Assertions.assertThat(employee.getId()).isEqualTo(1);
        Assertions.assertThat(employee.getName()).isEqualTo("Mark");
        Assertions.assertThat(employee.getCountry()).isEqualTo("England");
        Assertions.assertThat(employee.getIsDeleted()).isEqualTo(null);
        Assertions.assertThat(employee.getGender()).isEqualTo(Gender.M);

    }

    @Test
    @Order(2)
    @DisplayName("Get employee by id test")
    public void getEmployeeTest() {

        var employee = employeeRepository.findById(1).orElseThrow();

        Assertions.assertThat(employee.getId()).isEqualTo(1);
        Assertions.assertThat(employee.getName()).isEqualTo("Mark");
        Assertions.assertThat(employee.getCountry()).isEqualTo("England");
        Assertions.assertThat(employee.getIsDeleted()).isEqualTo(null);
        Assertions.assertThat(employee.getGender()).isEqualTo(Gender.M);

    }

    @Test
    @Order(3)
    @DisplayName("Get employees test")
    public void getListOfEmployeeTest() {

        var employeesList = employeeRepository.findAll();

        Assertions.assertThat(employeesList.size()).isEqualTo(1);
        Assertions.assertThat(employeesList.get(0).getName()).isEqualTo("Mark");
    }

    @Test
    @Order(4)
    @Rollback(value = false)
    @DisplayName("Update employee test")
    public void updateEmployeeTest() {

        var employee = employeeRepository.findById(1).orElseThrow();
        employee.setName("Martin");
        employee.setCountry("Ukraine");
        employeeRepository.save(employee);

        var employeeUpdated = employeeRepository.findById(1).orElseThrow();

        Assertions.assertThat(employeeUpdated.getName()).isEqualTo("Martin");
        Assertions.assertThat(employeeUpdated.getCountry()).isEqualTo("Ukraine");
        Assertions.assertThat(employee.getGender()).isEqualTo(Gender.M);

    }

    @Test
    @Order(5)
    @DisplayName("Find employee by gender test")
    public void findByGenderTest() {

        var employees = employeeRepository.findByGender(Gender.M.toString(), "UK");

        assertThat(employees.get(0).getGender()).isEqualTo(Gender.M);
    }

    @Test
    @Order(6)
    @Rollback(value = false)
    @DisplayName("Find employee by name success test")
    public void findEmployeesByCountrySuccessTest() {

        var employeesList = employeeRepository.findEmployeesByCountry("Ukraine");

        System.out.println(employeesList);

        Assertions.assertThat(employeesList.size()).isEqualTo(1);
        Assertions.assertThat(employeesList.get(0).getName()).isEqualTo("Martin");
        Assertions.assertThat(employeesList.get(0).getCountry()).isEqualTo("Ukraine");

    }

    @Test
    @Order(7)
    @Rollback(value = false)
    @DisplayName("Find employee by name failure test")
    public void findEmployeesByCountryFailureTest() {

        var employeesList = employeeRepository.findEmployeesByCountry("England");

        Assertions.assertThat(employeesList.size()).isEqualTo(0);
    }

    @Test
    @Order(8)
    @Rollback(value = false)
    @DisplayName("Delete employee test")
    public void deleteEmployeeTest() {

        var employee = employeeRepository.findById(1).orElseThrow();

        employeeRepository.delete(employee);

        Employee employeeNull = null;

        var optionalEmployee = Optional.ofNullable(employeeRepository.findByName("Martin"));

        if (optionalEmployee.isPresent()) {
            employeeNull = optionalEmployee.orElseThrow();
        }

        Assertions.assertThat(employeeNull).isNull();
    }

}
