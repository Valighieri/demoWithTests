package com.example.demowithtests.service.history;

import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.domain.History;

public interface HistoryService {

    History createHistory(Employee employee, String operationType);

}
