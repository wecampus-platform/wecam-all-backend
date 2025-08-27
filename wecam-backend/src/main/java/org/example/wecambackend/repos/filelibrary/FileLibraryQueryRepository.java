package org.example.wecambackend.repos.filelibrary;

import org.example.model.file.FileAsset;
import org.example.wecambackend.dto.projection.FileItemDto;
import org.example.wecambackend.dto.projection.FileLibraryFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileLibraryQueryRepository {
    Page<FileItemDto> search(FileLibraryFilter filter, Pageable pageable);
}
