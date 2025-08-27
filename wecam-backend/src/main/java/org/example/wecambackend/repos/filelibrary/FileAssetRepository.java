package org.example.wecambackend.repos.filelibrary;

import org.example.model.file.FileAsset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileAssetRepository extends JpaRepository<FileAsset,Long> {
}
