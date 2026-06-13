package com.Marketplace_Management.Shared.Mappers;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Shared.Contracts.IMapper;
import com.Marketplace_Management.Shared.Entities.FileEntity;
import com.Marketplace_Management.Shared.Models.File;

@Component
public class FileMapper implements IMapper<File, FileEntity>{
    @Override
    public File toDomain(FileEntity entity) {
        return new File(entity.getId(), entity.getUrl(), entity.getEntityType(), entity.getEntityId());
    }
    
    @Override
    public FileEntity toEntity(File domain) {
        return new FileEntity(domain.getId(), domain.getUrl(), domain.getEntityType(), domain.getEntityId());
    }
}
