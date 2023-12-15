package com.example.demowithtests.service.history;

import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.domain.History;
import com.example.demowithtests.repository.HistoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
@Service
public class HistoryServiceBean implements HistoryService {

    private final HistoryRepository historyRepository;

    @Override
    public History createHistory(Employee employee, String operationType) {
        History history = new History();
        history.setEmployee(employee);
        history.setOperationDate(LocalDateTime.now());
        history.setOperationType(operationType);
        return historyRepository.save(history);
    }
}
