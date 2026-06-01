package com.StoreManagement.Shared.Infrastructure.Mapper;

import org.springframework.stereotype.Component;

import com.StoreManagement.Shared.Domain.File;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;
import com.StoreManagement.Shared.Infrastructure.Persistence.Entity.FileEntity;

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
