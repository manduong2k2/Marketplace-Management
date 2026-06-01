package com.StoreManagement.Shared.Domain.Contracts;

import com.StoreManagement.Shared.Domain.File;

public interface IFileRepository {
    File save(File file);
    void delete(File file);
}
