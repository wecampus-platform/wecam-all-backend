package org.example.wecambackend.service.admin.Enum;

public enum UploadFolder {
    TODO("todo"),
    NOTICE("notice"),
    AFFILIATION("affiliation"),
    MEETING("meeting");

    private final String folderName;

    UploadFolder(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}

