package com.example.demowithtests.util.mappers;

import com.example.demowithtests.domain.Document;
import com.example.demowithtests.dto.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    DocumentDto toDocumentDto(Document document);

    Document toDocument(DocumentDto documentDto);

}