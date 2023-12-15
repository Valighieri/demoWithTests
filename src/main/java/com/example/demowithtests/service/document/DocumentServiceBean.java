package com.example.demowithtests.service.document;

import com.example.demowithtests.domain.Document;
import com.example.demowithtests.repository.DocumentRepository;
import com.example.demowithtests.service.history.HistoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceBean implements DocumentService {

    private final DocumentRepository documentRepository;
    private final HistoryService historyService;

    /**
     * @param document
     * @return
     */
    @Override
    public Document create(Document document) {
        document.setExpireDate(LocalDateTime.now().plusYears(5));
        historyService.createHistory(null, "createDocument");
        return documentRepository.save(document);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Document getById(Integer id) {
        return documentRepository.findById(id).orElseThrow();
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Document handlePassport(Integer id) {
        Document document = getById(id);
        if (document.getIsHandled()) {
            throw new RuntimeException();
        } else document.setIsHandled(Boolean.TRUE);
        return documentRepository.save(document);
    }

    @Override
    public Document setPassportNotHandle(Integer id) {
        return Optional.ofNullable(getById(id))
                .filter(document -> document.getIsHandled().equals(Boolean.TRUE))
                .map(document -> {
                    document.setIsHandled(Boolean.FALSE);
                    return documentRepository.save(document);
                })
                .orElseThrow(() -> new EntityNotFoundException
                        ("Document not found"));
    }

    /**
     * @param passportId
     * @param imageId
     * @return
     */
    @Override
    public Document addImage(Integer passportId, Integer imageId) {
        return null;
    }
}
