package com.example.demowithtests.service.document;

import com.example.demowithtests.domain.Document;

public interface DocumentService {

    Document create(Document document);

    Document getById(Integer id);

    Document handlePassport(Integer id);

    Document setPassportNotHandle(Integer id);

    Document addImage(Integer passportId, Integer imageId);
}
