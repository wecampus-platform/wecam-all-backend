package org.example.wecambackend.service.admin.Enum;

public enum UploadFolder {
    TODO("todo"),
    NOTICE("notice"),
    AFFILIATION("affiliation"),
    FILE_ASSET("fileAsset"),
    MEETING("meeting");

    private final String folderName;

    UploadFolder(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}

