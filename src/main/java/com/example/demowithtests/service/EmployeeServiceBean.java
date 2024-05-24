package com.example.demowithtests.service;

import com.example.demowithtests.domain.Document;
import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.repository.EmployeeRepository;
import com.example.demowithtests.service.document.DocumentService;
import com.example.demowithtests.service.emailSevice.EmailSenderService;
import com.example.demowithtests.service.history.HistoryService;
import com.example.demowithtests.util.annotations.entity.ActivateCustomAnnotations;
import com.example.demowithtests.util.annotations.entity.Name;
import com.example.demowithtests.util.annotations.entity.ToLowerCase;
import com.example.demowithtests.util.exception.ResourceNotFoundException;
import com.example.demowithtests.util.exception.ResourceNotUpdateException;
import com.example.demowithtests.util.exception.ResourceWasDeletedException;
import com.example.demowithtests.util.exception.ResourcesNotExistException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class EmployeeServiceBean implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmailSenderService emailSenderService;
    private final DocumentService documentService;
    private final HistoryService historyService;

    @Override
    public Document takeAwayDocumentFromEmployee(Integer employeeId) {
        return employeeRepository.findById(employeeId)
                .filter(this::IsEmployeePresent)
                .map(entity -> {
                    Document doc = documentService.setPassportNotHandle(entity.getDocument().getId());
                    historyService.createHistory(entity, "takeAwayDocument");
                    return doc;
                })
                .orElseThrow(() -> new EntityNotFoundException
                        ("Employee not found with id = " + employeeId));
    }

    @Override
    public Employee assignDocumentForEmployee(Integer employeeId, Integer documentId) {
        return employeeRepository.findById(employeeId)
                .filter(this::IsEmployeePresent)
                .map(entity -> {
                    entity.setDocument(documentService.getById(documentId));
                    documentService.handlePassport(documentId);
                    historyService.createHistory(entity, "assignDocument");
                    return employeeRepository.save(entity);
                })
                .orElseThrow(() -> new EntityNotFoundException
                        ("Employee not found with id = " + employeeId));
    }

    @Override
    @ActivateCustomAnnotations({Name.class, ToLowerCase.class})
    // @Transactional(propagation = Propagation.MANDATORY)
    public Employee create(Employee employee) {
//        if (employee.getDocument() != null) {
//            employee.getDocument().setIsHandled(Boolean.TRUE);
//            historyService.createHistory(employee, "createDocumentWithEmployee");
//        }
        return employeeRepository.save(employee);
        //return employeeRepository.saveAndFlush(employee);
    }

    @Override
    public void createAndSave(Employee employee) {
        employeeRepository.saveEmployee(employee.getName(), employee.getEmail(), employee.getCountry(), String.valueOf(employee.getGender()));
    }


    @Override
    public List<Employee> getAll() {
        return employeeRepository.findAll().stream()
                .filter(this::IsEmployeePresent)
                .toList();
    }

    @Override
    public List<Employee> removeAll() {
        List<Employee> list =
                employeeRepository.findAll().stream()
                        .filter(this::IsEmployeePresent)
                        .peek(emp -> emp.setIsDeleted(Boolean.TRUE))
                        .toList();

        if (list.isEmpty()) throw new ResourcesNotExistException();

        employeeRepository.saveAll(list);
        return list;
    }

    @Override
    public Employee getById(Integer id) {
        return employeeRepository.findById(id)
                .filter(this::IsEmployeePresent)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Employee removeById(Integer id) {
        return employeeRepository.findById(id)
                .filter(this::IsEmployeePresent)
                .map(employee -> {
                    employee.setIsDeleted(Boolean.TRUE);
                    return employeeRepository.save(employee);
                })
                .orElseThrow(ResourceWasDeletedException::new);
    }

    @Override
    public Employee updateById(Integer id, Employee employee) {
        return employeeRepository.findById(id)
                .filter(this::IsEmployeePresent)
                .map(entity -> {
                    entity.setName(employee.getName());
                    entity.setEmail(employee.getEmail());
                    entity.setCountry(employee.getCountry());
                    return employeeRepository.save(entity);
                })
                .orElseThrow(() -> new EntityNotFoundException
                        ("Employee not found with id = " + id));
    }

    @Override
    public Employee updateEmployeeByName(String name, Integer id) {
        return employeeRepository.findById(id)
                .filter(this::IsEmployeePresent)
                .map(entity -> {
                    entity.setName(name);
                    return employeeRepository.save(entity);
                })
                .orElseThrow(ResourceNotUpdateException::new);
    }


    @Override
    public List<Employee> findByNameContaining(String name) {
        return employeeRepository.findByNameContaining(name)
                .stream()
                .filter(this::IsEmployeePresent)
                .toList();
    }

    @Override
    public Page<Employee> getAllWithPagination(Pageable pageable) {
        log.debug("getAllWithPagination() - start: pageable = {}", pageable);
        Page<Employee> list = employeeRepository.findAll(pageable);
        log.debug("getAllWithPagination() - end: list = {}", list);
        return list;
    }


    /*@Override
    public Page<Employee> findByCountryContaining(String country, Pageable pageable) {
        return employeeRepository.findByCountryContaining(country, pageable);
    }*/

    @Override
    public Page<Employee> findByCountryContaining(String country, int page, int size, List<String> sortList, String sortOrder) {
        // create Pageable object using the page, size and sort details
        Pageable pageable = PageRequest.of(page, size, Sort.by(createSortOrder(sortList, sortOrder)));
        // fetch the page object by additionally passing pageable with the filters
        return employeeRepository.findByCountryContaining(country, pageable);
    }

    private List<Sort.Order> createSortOrder(List<String> sortList, String sortDirection) {
        List<Sort.Order> sorts = new ArrayList<>();
        Sort.Direction direction;
        for (String sort : sortList) {
            if (sortDirection != null) {
                direction = Sort.Direction.fromString(sortDirection);
            } else {
                direction = Sort.Direction.DESC;
            }
            sorts.add(new Sort.Order(direction, sort));
        }
        return sorts;
    }

    @Override
    public List<String> getAllEmployeeCountry() {
        log.info("getAllEmployeeCountry() - start:");
        List<Employee> employeeList = employeeRepository.findAll()
                .stream().filter(this::IsEmployeePresent).toList();

        List<String> countries = employeeList.stream()
                .map(Employee::getCountry)
                //.sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        log.info("getAllEmployeeCountry() - end: countries = {}", countries);
        return countries;
    }

    @Override
    public List<String> getSortCountry() {
        List<Employee> employeeList = employeeRepository.findAll()
                .stream().filter(this::IsEmployeePresent)
                .toList();

        return employeeList.stream()
                .map(Employee::getCountry)
                .filter(c -> c.startsWith("U"))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<String> findEmails() {
        var employeeList = employeeRepository.findAll()
                .stream().filter(this::IsEmployeePresent)
                .toList();

        var emails = employeeList.stream()
                .map(Employee::getEmail)
                .toList();

        var opt = emails.stream()
                .filter(s -> s.endsWith(".com"))
                .findFirst()
                .orElse("error?");

        return Optional.ofNullable(opt);
    }

    @Override
    public List<Employee> filterByCountry(String country) {
        return employeeRepository.findEmployeesByCountry(country)
                .stream().filter(this::IsEmployeePresent)
                .toList();
    }

    private boolean IsEmployeePresent(Employee employee) {
        Boolean isDeleted = employee.getIsDeleted();
        if (isDeleted != null && isDeleted.equals(Boolean.FALSE)) return true;
        else return false;
    }

    @Override
    public Set<String> sendEmailsAllUkrainian() {
        var ukrainians = employeeRepository.findAllUkrainian()
                .orElseThrow(() -> new EntityNotFoundException("Employees from Ukraine not found!"));

        var emails = new HashSet<String>();
        ukrainians.forEach(employee -> {
            emailSenderService.sendEmail(
                    /*employee.getEmail(),*/
                    "kaluzny.oleg@gmail.com", //для тесту
                    "Need to update your information",
                    String.format(
                            "Dear " + employee.getName() + "!\n" +
                                    "\n" +
                                    "The expiration date of your information is coming up soon. \n" +
                                    "Please. Don't delay in updating it. \n" +
                                    "\n" +
                                    "Best regards,\n" +
                                    "Ukrainian Info Service.")
            );
            emails.add(employee.getEmail());
        });

        return emails;
    }

    @Override
    public String renameAllFrenchCitizens(String name) {
        return "All french " +
                employeeRepository.updateAllFrenchNames(name)
                        .orElseThrow(ResourceNotUpdateException::new)
                + " users were updated. ";
    }

    @Override
    public String updateEmployeeNamesByCountry(String name, String country) {
        return "All french " +
                employeeRepository.updateEmployeeNamesByCountry(name, country)
                + " users were updated. ";
    }

    /**
     * @param name
     * @param id
     * @return
     */


}
