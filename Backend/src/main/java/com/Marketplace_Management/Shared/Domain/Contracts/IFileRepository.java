package com.Marketplace_Management.Shared.Domain.Contracts;

import com.Marketplace_Management.Shared.Domain.File;

public interface IFileRepository {
    File save(File file);
    void delete(File file);
    void delete(String url);
}
