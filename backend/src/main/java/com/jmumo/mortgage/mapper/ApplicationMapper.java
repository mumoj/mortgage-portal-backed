package com.jmumo.mortgage.mapper;


import com.jmumo.mortgage.model.dto.ApplicationDto;
import com.jmumo.mortgage.model.dto.CreateApplicationRequest;
import com.jmumo.mortgage.model.dto.DocumentDto;
import com.jmumo.mortgage.model.entity.Application;
import com.jmumo.mortgage.model.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    ApplicationDto toDto(Application application);
    Application toEntity(CreateApplicationRequest request);

    @Mapping(target = "presignedUrl", source = "presignedUrl")
    DocumentDto toDto(Document document);
}

