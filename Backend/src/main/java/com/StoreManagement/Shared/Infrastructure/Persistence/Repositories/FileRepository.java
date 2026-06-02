package com.StoreManagement.Shared.Infrastructure.Persistence.Repositories;

import com.StoreManagement.Shared.Domain.Contracts.IFileRepository;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;
import com.StoreManagement.Shared.Infrastructure.Persistence.Entity.FileEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.StoreManagement.Shared.Domain.File;

@Repository
public class FileRepository implements IFileRepository {

    @Autowired
    public IMapper<File, FileEntity> fileMapper;
    
    @Autowired
    public FileJpaRepository fileJpaRepository;

    public File save(File file) {
        FileEntity savedFileEntity = fileJpaRepository.save(fileMapper.toEntity(file));
        return fileMapper.toDomain(savedFileEntity);
    }
    
    public void delete(File file) {
        fileJpaRepository.deleteById(file.getId());
    }
    
    public void delete(String url) {
        fileJpaRepository.deleteByUrl(url);
    }
}
