package com.Marketplace_Management.Shared.Contracts;

import com.Marketplace_Management.Shared.Models.File;

public interface IFileRepository {
    File save(File file);
    void delete(File file);
    void delete(String url);
}
