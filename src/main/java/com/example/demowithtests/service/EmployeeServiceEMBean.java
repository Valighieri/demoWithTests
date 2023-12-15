package com.example.demowithtests.service;

import com.example.demowithtests.domain.Document;
import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.util.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmployeeServiceEMBean implements EmployeeServiceEM {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @param employee
     * @return
     */
    @Override
    @Transactional //jakarta
    public Employee createWithJpa(Employee employee) {
        if (employee.getDocument() != null) {
            employee.getDocument().setIsHandled(Boolean.TRUE);
        }
        return entityManager.merge(employee);
        /*entityManager.persist(employee);
        entityManager.flush();
        return entityManager.find(Employee.class, employee);*/
    }

    /**
     * @return
     */
    @Override
    @Transactional //jakarta
    public Set<String> findAllCountriesWithJpa() {
        return entityManager.createQuery("select distinct country from Employee", String.class).getResultStream().collect(Collectors.toSet());
    }

    /**
     * @param id
     * @param employee
     * @return
     */
    @Override
    @Transactional //jakarta
    public Employee updateByIdWithJpa(Integer id, Employee employee) {
        Employee refreshEmployee = Optional.ofNullable(entityManager.find(Employee.class, id))
                .orElseThrow(() -> new RuntimeException("id = " + employee.getId()));
        return entityManager.merge(refreshEmployee);
    }

    /**
     * @param id
     */
    @Override
    @Transactional //jakarta
    public void deleteByIdWithJpa(Integer id) {
        Optional<Employee> employee = Optional.ofNullable(entityManager.find(Employee.class, id));
        entityManager.remove(employee);
    }

    @Override
    @Transactional
    public List<Employee> getAllEM() {
        return entityManager.createNativeQuery("SELECT * FROM users", Employee.class).getResultList();
    }

    @Override
    @Transactional
    public Employee assignDocumentForEmployeeWithJpa(Integer employeeId, Integer documentId) {
        Employee employee = Optional.ofNullable(entityManager.find(Employee.class, employeeId))
                .orElseThrow(() -> new EntityNotFoundException
                        ("Employee not found with id = " + employeeId));

        Document document = Optional.ofNullable(entityManager.find(Document.class, documentId))
                .orElseThrow(() -> new EntityNotFoundException
                        ("Document not found with id = " + documentId));

        employee.setDocument(document);
        return entityManager.merge(employee);
    }

}
