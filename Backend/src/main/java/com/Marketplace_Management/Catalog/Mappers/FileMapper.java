package com.Marketplace_Management.Catalog.Mappers;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Shared.Contracts.EntityDomainMapper;
import com.Marketplace_Management.Catalog.Entities.FileEntity;
import com.Marketplace_Management.Catalog.Models.File;

@Component
public class FileMapper implements EntityDomainMapper<File, FileEntity>{
    @Override
    public File toDomain(FileEntity entity) {
        return new File(entity.getId(), entity.getUrl(), entity.getEntity());
    }
    
    @Override
    public FileEntity toEntity(File domain) {
        return new FileEntity(domain.getId(), domain.getUrl(), domain.getEntity());
    }
}
