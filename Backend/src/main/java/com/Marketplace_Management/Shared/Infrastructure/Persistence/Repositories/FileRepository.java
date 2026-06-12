package com.Marketplace_Management.Shared.Infrastructure.Persistence.Repositories;

import com.Marketplace_Management.Shared.Domain.Contracts.IFileRepository;
import com.Marketplace_Management.Shared.Domain.Contracts.IMapper;
import com.Marketplace_Management.Shared.Infrastructure.Persistence.Entity.FileEntity;

import org.springframework.stereotype.Repository;

import com.Marketplace_Management.Shared.Domain.File;

@Repository
public class FileRepository implements IFileRepository {

    private final IMapper<File, FileEntity> fileMapper;
    private final FileJpaRepository fileJpaRepository;

    public FileRepository(IMapper<File, FileEntity> fileMapper, FileJpaRepository fileJpaRepository) {
        this.fileMapper = fileMapper;
        this.fileJpaRepository = fileJpaRepository;
    }

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
