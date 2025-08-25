package org.example.wecambackend.service.admin.filelibrary;


import lombok.RequiredArgsConstructor;
import org.example.wecambackend.dto.projection.FileItemDto;
import org.example.wecambackend.dto.projection.FileLibraryFilter;
import org.example.wecambackend.repos.filelibrary.FileLibraryQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileLibraryService {
    private final FileLibraryQueryRepository repo;

    public Page<FileItemDto> search(FileLibraryFilter filter, Pageable pageable) {
        return repo.search(filter, pageable);
    }
}
